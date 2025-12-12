package ru.netology.data;

import lombok.Value;

public class DataHelper {
    
    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }
    
    @Value
    public static class VerificationCode {
        private String code;
    }
    
    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }
    
    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }
}
