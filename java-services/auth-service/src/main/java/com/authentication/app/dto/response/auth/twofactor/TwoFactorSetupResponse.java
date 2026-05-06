package com.authentication.app.dto.response.auth.twofactor;

public class TwoFactorSetupResponse {

    private final String secret;
    private final String otpAuthUri;

    public TwoFactorSetupResponse(String secret, String otpAuthUri) {
        this.secret = secret;
        this.otpAuthUri = otpAuthUri;
    }

    public String getSecret() {
        return secret;
    }

    public String getOtpAuthUri() {
        return otpAuthUri;
    }
}
