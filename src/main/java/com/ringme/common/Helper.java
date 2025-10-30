package com.ringme.common;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;
import java.util.Random;

public class Helper {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String OTP_CHARACTERS = "0123456789";

    public static void setResponse(HttpServletResponse response, int status, String jsonResponse) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        response.getWriter().close();
    }

    public static String generateRandomOTP() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(OTP_CHARACTERS.length());
            char randomChar = OTP_CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static String sha256(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static String hmacSha256(String secretKey, String data) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(keySpec);
        byte[] hmac = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmac);
    }

    public static String getSecApi(Map<String, String> headers) {
        String secApi = headers.get("sec-api");
        if (StringUtils.isEmpty(secApi)) {
            secApi = headers.get("mocha-api");
        }
        return secApi;
    }

    public static double safeParseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            // Chuyển byte thành chuỗi hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static boolean checkErrorCode(String errorCode) throws InterruptedException {
        if(errorCode != null && (errorCode.equals("0") || errorCode.equals("00") || errorCode.equals("000"))) {
            Thread.sleep(Duration.ofSeconds(2));
            return true;
        }

        return false;
    }

    public static String processStringSearch(String input) {
        if(StringUtils.isNotEmpty(input))
            return input.trim();
        return null;
    }

    public static BigDecimal calculateTkg(BigDecimal total) {
        BigDecimal divisor = new BigDecimal("1.1"); // nên dùng String để tránh sai số
        // 2 = làm tròn 2 chữ số thập phân, HALF_UP = 4 làm tròn xuống, 5 làm tròn lên

        return total.divide(divisor, 2, RoundingMode.HALF_UP);
    }

    public static double calculateTkg(double total) {
        double x = total / 1.1;
        return Math.round(x * 100.0) / 100.0; // làm tròn đến 2 chữ số thập phân
    }

    public static String calculateTkg(String total) {
        return String.valueOf(calculateTkg(Double.parseDouble(total)));
    }

//    public static double roundingMode(double amount) {
//        BigDecimal roundedAmount = BigDecimal.valueOf(amount)
//                .setScale(2, RoundingMode.HALF_UP);
//
//        return roundedAmount.doubleValue();
//    }

    public static BigDecimal roundingMode(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundingMode(int amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
