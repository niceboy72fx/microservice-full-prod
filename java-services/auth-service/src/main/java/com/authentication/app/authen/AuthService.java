package com.authentication.app.authen;

import com.authentication.app.bean.LoginResponse;
import com.authentication.app.bean.UserIdResponse;
import com.authentication.app.bean.TwoFactorSetupResponse;
import com.authentication.app.dao.record.AuthAccountRecord;
import com.authentication.app.dao.AuthAccountSqlRepository;
import com.authentication.app.dao.LoginAuditSqlRepository;
import com.authentication.app.authen.token.RefreshTokenBlacklistService;
import com.authentication.app.authen.twofactor.TwoFactorService;
import java.time.Duration;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenBlacklistService refreshTokenBlacklistService;
    private final AuthAccountSqlRepository authAccountSqlRepository;
    private final TwoFactorService twoFactorService;
    private final LoginAuditSqlRepository loginAuditSqlRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtProperties jwtProperties,
            InMemoryUserDetailsManager userDetailsManager,
            PasswordEncoder passwordEncoder,
            RefreshTokenBlacklistService refreshTokenBlacklistService,
            AuthAccountSqlRepository authAccountSqlRepository,
            TwoFactorService twoFactorService,
            LoginAuditSqlRepository loginAuditSqlRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenBlacklistService = refreshTokenBlacklistService;
        this.authAccountSqlRepository = authAccountSqlRepository;
        this.twoFactorService = twoFactorService;
        this.loginAuditSqlRepository = loginAuditSqlRepository;
    }

    public LoginResponse register(String username, String password) {
        String normalizedUsername = username == null ? "" : username.trim();
        if (normalizedUsername.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }
        if (authAccountSqlRepository.existsByEmail(normalizedUsername)) {
            throw new IllegalArgumentException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);
        authAccountSqlRepository.insert(normalizedUsername, encodedPassword);

        UserDetails user = User.withUsername(normalizedUsername)
                .password(encodedPassword)
                .roles("USER")
                .build();
        userDetailsManager.createUser(user);

        return createTokenPair(normalizedUsername);
    }

    public LoginResponse login(String username, String password, String otpCode, String ipAddress) {
        String normalizedUsername = username == null ? "" : username.trim();
        try {
            syncUserFromSqlIfNeeded(normalizedUsername);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedUsername, password));
            if (!twoFactorService.verifyForLogin(normalizedUsername, otpCode)) {
                loginAuditSqlRepository.insert(normalizedUsername, ipAddress, false, "INVALID_2FA_CODE");
                throw new IllegalArgumentException("Invalid 2FA code");
            }
            loginAuditSqlRepository.insert(normalizedUsername, ipAddress, true, "LOGIN_SUCCESS");
            return createTokenPair(normalizedUsername);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            loginAuditSqlRepository.insert(normalizedUsername, ipAddress, false, "AUTHENTICATION_FAILED");
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    public TwoFactorSetupResponse setupTwoFactor(String username) {
        return twoFactorService.setup(username);
    }

    public void enableTwoFactor(String username, String code) {
        twoFactorService.enable(username, code);
    }

    public void disableTwoFactor(String username, String code) {
        twoFactorService.disable(username, code);
    }

    public LoginResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        if (refreshTokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        String username = jwtService.extractUsername(refreshToken);
        Duration ttl = jwtService.remainingTtl(refreshToken);
        refreshTokenBlacklistService.blacklist(refreshToken, ttl);

        return createTokenPair(username);
    }

    public void logout(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Duration ttl = jwtService.remainingTtl(refreshToken);
        refreshTokenBlacklistService.blacklist(refreshToken, ttl);
    }

    public UserIdResponse getUserIdFromToken(String token) {
        if (!jwtService.isTokenValid(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        return new UserIdResponse(jwtService.extractUserId(token));
    }

    private LoginResponse createTokenPair(String username) {
        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);
        return new LoginResponse(accessToken, refreshToken, "Bearer", jwtProperties.getExpirationSeconds());
    }

    private void syncUserFromSqlIfNeeded(String username) {
        if (username == null || username.isBlank() || userDetailsManager.userExists(username)) {
            return;
        }
        Optional<AuthAccountRecord> accountOptional = authAccountSqlRepository.findByEmail(username);
        if (accountOptional.isEmpty()) {
            return;
        }
        AuthAccountRecord account = accountOptional.get();
        UserDetails user = User.withUsername(account.email())
                .password(account.password())
                .roles("USER")
                .build();
        userDetailsManager.createUser(user);
    }
}
