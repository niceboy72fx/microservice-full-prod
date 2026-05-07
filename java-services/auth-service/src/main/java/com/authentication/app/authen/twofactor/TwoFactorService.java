package com.authentication.app.authen;

import com.authentication.app.bean.TwoFactorSetupResponse;
import com.authentication.app.dao.record.TwoFactorAccountRecord;
import com.authentication.app.dao.TwoFactorSqlRepository;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {

    private static final String ISSUER = "auth-service";
    private static final int STEP_SECONDS = 30;
    private static final int WINDOW = 1;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final TwoFactorSqlRepository twoFactorSqlRepository;

    public TwoFactorService(TwoFactorSqlRepository twoFactorSqlRepository) {
        this.twoFactorSqlRepository = twoFactorSqlRepository;
    }

    public TwoFactorSetupResponse setup(String username) {
        String normalized = normalize(username);
        String secret = generateSecret();
        twoFactorSqlRepository.upsertSecret(normalized, secret);
        String uri = "otpauth://totp/" + url(ISSUER + ":" + normalized)
                + "?secret=" + secret
                + "&issuer=" + url(ISSUER)
                + "&algorithm=SHA1&digits=6&period=30";
        return new TwoFactorSetupResponse(secret, uri);
    }

    public void enable(String username, String code) {
        TwoFactorAccountRecord account = loadRequired(username);
        if (!verifyCode(account.secret(), code)) {
            throw new IllegalArgumentException("Invalid 2FA code");
        }
        twoFactorSqlRepository.setEnabled(account.email(), true);
    }

    public void disable(String username, String code) {
        TwoFactorAccountRecord account = loadRequired(username);
        if (!verifyCode(account.secret(), code)) {
            throw new IllegalArgumentException("Invalid 2FA code");
        }
        twoFactorSqlRepository.setEnabled(account.email(), false);
    }

    public boolean verifyForLogin(String username, String code) {
        Optional<TwoFactorAccountRecord> account = twoFactorSqlRepository.findByEmail(normalize(username));
        if (account.isEmpty() || !account.get().enabled()) {
            return true;
        }
        return verifyCode(account.get().secret(), code);
    }

    private TwoFactorAccountRecord loadRequired(String username) {
        return twoFactorSqlRepository.findByEmail(normalize(username))
                .orElseThrow(() -> new IllegalArgumentException("2FA setup not found for user"));
    }

    private boolean verifyCode(String secret, String inputCode) {
        if (inputCode == null || !inputCode.matches("\\d{6}")) {
            return false;
        }
        long counter = Instant.now().getEpochSecond() / STEP_SECONDS;
        for (int i = -WINDOW; i <= WINDOW; i++) {
            String generated = generateTotp(secret, counter + i);
            if (generated.equals(inputCode)) {
                return true;
            }
        }
        return false;
    }

    private String generateTotp(String secret, long counter) {
        try {
            byte[] key = Base64.getDecoder().decode(secret);
            byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(counterBytes);
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int otp = binary % 1_000_000;
            return String.format("%06d", otp);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Failed to generate TOTP", exception);
        }
    }

    private String generateSecret() {
        byte[] bytes = new byte[20];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    private String normalize(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        return normalized;
    }

    private String url(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
