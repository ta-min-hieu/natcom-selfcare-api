package com.ringme.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Formatter;

@Component
@Log4j2
public class SecurityUtil {

    private static String privateKeyString =
                    "MIICWwIBAAKBgQCeb3ZGwLa0g8hzIZNAPHMq1hMzzs22U8hogy4+xYHb4RRqxbRy\n" +
                    "9KQtJ6FeKyN82uQvVPYe2qCzbS5seNSiOP1Em7TDCTZAjo/dlQHcOAeGUJyza7Ag\n" +
                    "RltkdBJydvQ8a2cfLXtkLoZ2zyHI2dLRHrYaSsHKc/bSyZfD3YvK9r8HNQIDAQAB\n" +
                    "AoGAOWr9u2CGFl+YGpl1axc9Sa3xoZn2FXjBrZa1AMzFZjFpG9Tws6STh0XQhnct\n" +
                    "0kbl4X1yAPaMvNn5tBgKP4xVxJ0tz+EckGB3fJrR8efBDhpea9fYom3hgfHY98TR\n" +
                    "VWnCnXZC3soK4LTnA5dF1uD+ou9LQ/jXXoS9nnN6TSwt/1UCQQD5i5dkwEUx7Gqq\n" +
                    "MzsiqyHDURG/bVox1kGAeOQkkAPzR94Q8RzCbQvxugnS/0yUZGdL+L82RBYM7CHM\n" +
                    "ceJcHyR3AkEAooiR/u5Gf5g3iQII6nlFGIE7Dwpjc44CVP7H6eSURMXl8sUYwJhx\n" +
                    "10Z6e66ZieHnygA6GA5W/KDoCixdexW4swJABklY+AXL+HT/PuhpffcXs6bwLoVf\n" +
                    "t0+xeL4S6UTjhJZz5rNcSR2cJmvMYY9i634YCBbtIj3W3F56f38C99UlAwJAbga/\n" +
                    "tnD83nFf3uq2uGvBv5X0MflZW6ddosYa9RXc3TWOtaqBWBeasR2v2rxWigWYkKFY\n" +
                    "smI6tqgCjKh9283XiwJADDJhlfcnBjjW+sftULwm/L0FVKx11FNeDUlPgM3SSBaV\n" +
                    "0eEMmJAJ+M3NrPALyWIDH6nw6md5PA8bKeootIy+Uw==";
    private static String publicKeyString =
                    "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeb3ZGwLa0g8hzIZNAPHMq1hMz\n" +
                    "zs22U8hogy4+xYHb4RRqxbRy9KQtJ6FeKyN82uQvVPYe2qCzbS5seNSiOP1Em7TD\n" +
                    "CTZAjo/dlQHcOAeGUJyza7AgRltkdBJydvQ8a2cfLXtkLoZ2zyHI2dLRHrYaSsHK\n" +
                    "c/bSyZfD3YvK9r8HNQIDAQAB";

    private static PublicKey publicKey;
    private static PrivateKey privateKey;
    protected static final String RSA_ALGORITHM = "RSA";
    public static String md5Encrypt(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte[] byteData = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error(s, e);
        }
        return null;
    }
    public static String sha1Encrypt(String password) {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static UserDetails validateJWT(String inputJWT, String jwtKey){
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtKey)).build().verify(inputJWT);

        String username = jwt.getSubject();
        String[] usernameSplit = username.split("@");
        String password = "-";
        if(jwt.getClaim("data") != null && jwt.getClaim("data").asString() != null) {
            String data = jwt.getClaim("data").asString();
            try {
                String plainText  = SecurityUtil.rsaDecrypt(data);
                String[] values = plainText.split("@@");
                password = values[2];
                if(!username.equals(values[0])) {
                    log.info("DEBUG: {}", values);
                    throw new JWTVerificationException("OTTSDK - Username Invalid");
                }
                log.debug("DEBUG: {}", plainText);
            } catch (Exception e) {
                log.error("JWT: {}, Data: {}, Error message: {}", inputJWT, data, e.getMessage(), e);
                throw new JWTVerificationException("OTTSDK - JWT data invalid");
            }
        }
        return new User(usernameSplit[0], password, new ArrayList<>());

    }

    public static DecodedJWT validateToken(String token, String jwtkey) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtkey)).build().verify(token);
        return jwt;
    }

    public static String getTokenFromHttpRequest(HttpServletRequest httpRequest, String jwtKey) {
        String authorization = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String bearer = authorization.split(" ")[1].trim();
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtKey)).build().verify(bearer);


        String password = "-";
        if(jwt.getClaim("data") != null && jwt.getClaim("data").asString() != null) {
            String data = jwt.getClaim("data").asString();
            try {
                String plainText = SecurityUtil.rsaDecrypt(data);
                String[] values = plainText.split("@@");
                password = values[2];
                return password;

            } catch (Exception e) {
                log.error("JWT: {}, Data: {}, Error message: {}", bearer, data, e.getMessage(), e);
                throw new JWTVerificationException("OTTSDK - JWT data invalid");
            }
        }
        return "";
    }

    public static String getUserNameFromHttpRequest(HttpServletRequest httpRequest, String jwtKey) {
        String authorization = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String bearer = authorization.split(" ")[1].trim();
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtKey)).build().verify(bearer);

        return jwt.getSubject();
    }


    /**
     * Init java security to add BouncyCastle as an RSA provider
     */
    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        publicKey = getPublicKeyFromString(publicKeyString.replace("\n", ""));
        privateKey = getPrivateKeyFromString(privateKeyString.replace("\n", ""));
    }

    /**
     * Generate key which contains a pair of privae and public key using 1024
     * bytes
     *
     * @return key pair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();

        return key;
    }

    /**
     * Encrypt a text using public key.
     *
     * @param text The original unencrypted text
     * @return Encrypted text
     * @throws Exception
     */
    private static byte[] rsaEncrypt(byte[] text) throws Exception {
        //
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        // encrypt the plaintext using the public key
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(text);
    }
    private static byte[] rsaEncrypt(byte[] text, PublicKey pubKey) throws Exception {
        //
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        // encrypt the plaintext using the public key
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(text);
    }
    /**
     * Encrypt a text using public key. The result is enctypted BASE64 encoded
     * text
     *
     * @param text The original unencrypted text
     * @return Encrypted text encoded as BASE64
     * @throws Exception
     */
    public static String rsaEncrypt(String text) throws Exception {
        String encryptedText;
        byte[] cipherText = rsaEncrypt(text.getBytes("UTF8"));
        encryptedText = encodeBASE64(cipherText);
        return encryptedText;
    }

    /**
     * Decrypt text using private key
     *
     * @param text The encrypted text
     * @return The unencrypted text
     * @throws Exception
     */
    private static byte[] rsaDecrypt(byte[] text) throws Exception {
        // decrypt the text using the private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(text);

    }
    private static byte[] rsaDecrypt(byte[] text, PrivateKey prvKey) throws Exception {
        // decrypt the text using the private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, prvKey);
        return cipher.doFinal(text);

    }
    /**
     * Decrypt BASE64 encoded text using private key
     *
     * @param text The encrypted text, encoded as BASE64
     * @return The unencrypted text encoded as UTF8
     * @throws Exception
     */
    public static String rsaDecrypt(String text) throws Exception {
        String result;
        // decrypt the text using the private key
        byte[] dectyptedText = rsaDecrypt(decodeBASE64(text));
        result = new String(dectyptedText, "UTF8");
        return result;

    }
    private static String rsaEncrypt(String text, PublicKey pkey) throws Exception {
        String encryptedText;
        byte[] cipherText = rsaEncrypt(text.getBytes("UTF8"), pkey);
        encryptedText = encodeBASE64(cipherText);
        return encryptedText;
    }

    private static String rsaDecrypt(String strEncrypted, PrivateKey prvKey) throws Exception {
        String result;
        // decrypt the text using the private key
        byte[] dectyptedText = rsaDecrypt(decodeBASE64(strEncrypted), prvKey);
        result = new String(dectyptedText, "UTF8");
        return result;
    }
    /**
     * Convert a Key to string encoded as BASE64
     *
     * @param key The key (private or public)
     * @return A string representation of the key
     */
    public static String getKeyAsString(Key key) {
        // Get the bytes of the key
        byte[] keyBytes = key.getEncoded();
        return encodeBASE64(keyBytes);
    }

    /**
     * Generates Private Key from BASE64 encoded string
     *
     * @param key BASE64 encoded string which represents the key
     * @return The PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromString(String key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodeBASE64(key));

        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    /**
     * Generates Public Key from BASE64 encoded string
     *
     * @param key BASE64 encoded string which represents the key
     * @return The PublicKey
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromString(String key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodeBASE64(key));
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    /**
     * Encode bytes array to BASE64 string
     *
     * @param bytes
     * @return Encoded string
     */
    private static String encodeBASE64(byte[] bytes) {
        // BASE64Encoder b64 = new BASE64Encoder();
        // return b64.encode(bytes, false);
        return Base64.encodeBase64String(bytes);
    }

    /**
     * Decode BASE64 encoded string to bytes array
     *
     * @param text The string
     * @return Bytes array
     * @throws IOException
     */
    private static byte[] decodeBASE64(String text) throws IOException {
        // BASE64Decoder b64 = new BASE64Decoder();
        // return b64.decodeBuffer(text);
        return Base64.decodeBase64(text);
    }

    /**
     * Sinh chữ ký ( Đã endcode Base 64 )
     *
     * @param data các trường ghép vào nhau theo quy định
     * @param privateKey
     * @return chữ ký đã encode base64
     */
    public static String generateSignBase64(String data, PrivateKey privateKey) {

        String encryptData = "";
        try {
            Signature s = Signature.getInstance("SHA1withRSA");
            s.initSign(privateKey);
            s.update(data.getBytes());
            byte[] signature = s.sign();
            // Encrypt data
            encryptData = new String(Base64.encodeBase64(signature));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            log.error("Exception when generate signature", e);
        }
        return encryptData;
    }

    /**
     * Xác thực chữ ký
     *
     * @param plainText các trường ghép vào nhau theo quy định
     * @param signBase64 chữ ký bản tin cần verify đã encode base64
     * @param publicKey public key được cung cấp
     * @return
     */
    public static boolean verifySign(String plainText, String signBase64, PublicKey publicKey) {

        try {
            // decode base64
            byte[] signBytes = Base64.decodeBase64(signBase64.getBytes());
            Signature sig = Signature.getInstance("SHA1WithRSA");
            sig.initVerify(publicKey);
            sig.update(plainText.getBytes());

            return sig.verify(signBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            log.error("Exception when verify signature", e);
        }
        return false;
    }

    public static String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        System.out.println(fileData.toString());
        return fileData.toString();
    }

    public static void main(String[] args) throws Exception {

        PublicKey pkey = getPublicKeyFromString(publicKeyString.replace("\n", ""));

        String strEncrypted = rsaEncrypt("xxx", pkey);

        System.out.println("strEncrypted ==> " + strEncrypted);
        Security.addProvider(new BouncyCastleProvider());

        PrivateKey prvKey = getPrivateKeyFromString(privateKeyString.replace("\n", ""));

        String strOrgin = rsaDecrypt(strEncrypted, prvKey);

        System.out.println("strOrgin ==> " + strOrgin);
    }

    public static String decryptAES256(String encryptedText, String key) throws Exception {
        byte[] decodedKey = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
