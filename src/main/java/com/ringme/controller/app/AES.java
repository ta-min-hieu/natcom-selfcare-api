package com.ringme.controller.app;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Log4j2
public class AES {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private enum EncryptMode {
        ENCRYPT, DECRYPT;
    }

    static String key = "myvtg";

    static String iv = "151e7b7548668ea2";

    static Cipher _cx;

    static byte[] _key;

    static byte[] _iv;

    public AES() {
        init();
    }

    private static void init() {
        try {
            _cx = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Security.addProvider((Provider)new BouncyCastleProvider());
            _key = new byte[16];
            _iv = new byte[16];
        } catch (NoSuchAlgorithmException e) {
            _cx = null;
            log.error("Sai thuat toan ma hoa md5:", e);
        } catch (NoSuchPaddingException e) {
            _cx = null;
            log.error("Sai thuat toan ma hoa md5:", e);
        }
    }

    public static final String md5(String inputString) {
        String MD5 = "MD5";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(inputString.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Sai thuat toan ma hoa md5:", e);
            return "";
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0xF];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        return data;
    }

    private static String encryptDecrypt(String _inputText, String _encryptionKey, EncryptMode _mode, String _initVector) throws UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String _out = "";
        int len = (_encryptionKey.getBytes("UTF-8")).length;
        if ((_encryptionKey.getBytes("UTF-8")).length > _key.length)
            len = _key.length;
        int ivlen = (_initVector.getBytes("UTF-8")).length;
        if ((_initVector.getBytes("UTF-8")).length > _iv.length)
            ivlen = _iv.length;
        System.arraycopy(_encryptionKey.getBytes("UTF-8"), 0, _key, 0, len);
        System.arraycopy(_initVector.getBytes("UTF-8"), 0, _iv, 0, ivlen);
        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(_iv);
        if (_mode.equals(EncryptMode.ENCRYPT)) {
            _cx.init(1, keySpec, ivSpec);
            byte[] results = _cx.doFinal(_inputText.getBytes("UTF-8"));
            _out = bytesToHex(results);
        }
        if (_mode.equals(EncryptMode.DECRYPT)) {
            _cx.init(2, keySpec, ivSpec);
            byte[] decodedValue = hexStringToByteArray(_inputText);
            byte[] decryptedVal = _cx.doFinal(decodedValue);
            _out = new String(decryptedVal);
        }
        return _out;
    }

    public static String SHA256(String text, int length) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String resultStr;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes("UTF-8"));
        byte[] digest = md.digest();
        StringBuffer result = new StringBuffer();
        for (byte b : digest) {
            result.append(String.format("%02x", new Object[] { Byte.valueOf(b) }));
        }
        if (length > result.toString().length()) {
            resultStr = result.toString();
        } else {
            resultStr = result.toString().substring(0, length);
        }
        return resultStr;
    }

    public String encrypt(String _plainText, String _key, String _iv) {
        String strResult = "";
        try {
            strResult = encryptDecrypt(_plainText, _key, EncryptMode.ENCRYPT, _iv);
        } catch (InvalidKeyException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (UnsupportedEncodingException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (InvalidAlgorithmParameterException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (IllegalBlockSizeException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (BadPaddingException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        }
        return strResult;
    }

    public static String encrypt(String _plainText) {
        init();
        String strResult = "";
        try {
            String key = SHA256(AES.key, 16);
            String iv = AES.iv;
            strResult = encryptDecrypt(_plainText, key, EncryptMode.ENCRYPT, iv);
        } catch (InvalidKeyException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (UnsupportedEncodingException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (InvalidAlgorithmParameterException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (IllegalBlockSizeException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (BadPaddingException e) {
            strResult = null;
            log.error("Loi trong qua trinh ma hoa", e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return strResult;
    }

    public static String decrypt(String _encryptedText) {
        init();
        String strResult = "";
        try {
            String key = SHA256(AES.key, 16);
            String iv = AES.iv;
            strResult = encryptDecrypt(_encryptedText, key, EncryptMode.DECRYPT, iv);
        } catch (InvalidKeyException e) {
            strResult = null;
            log.error("Loi trong qua trinh giai ma", e);
        } catch (UnsupportedEncodingException e) {
            strResult = null;
        } catch (InvalidAlgorithmParameterException e) {
            strResult = null;
            log.error("Loi trong qua trinh giai ma", e);
        } catch (IllegalBlockSizeException e) {
            strResult = null;
            log.error("Loi trong qua trinh giai ma", e);
        } catch (BadPaddingException e) {
            strResult = null;
            log.error("Loi trong qua trinh giai ma", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Loi trong qua trinh giai ma", e);
        }
        return strResult;
    }

    public static String generateRandomIV(int length) {
        SecureRandom ranGen = new SecureRandom();
        byte[] aesKey = new byte[16];
        ranGen.nextBytes(aesKey);
        StringBuffer result = new StringBuffer();
        for (byte b : aesKey) {
            result.append(String.format("%02x", new Object[] { Byte.valueOf(b) }));
        }
        if (length > result.toString().length())
            return result.toString();
        return result.toString().substring(0, length);
    }

    public static void main(String[] args) {
        log.info("aaaaaaaaaaa: {}", decrypt("813821FE1280B47E9C14B5C6429406B2E3107E14681B4C9CEFCCD566E66065ADE39E81DB1ED8FF86B8300D237EF1BEA7011404D75605C20CD8739A164A92B560F6C338813C9918FA111C4A501FF02E64612273C0FBFEA9502D92F270BE30FAC045FDC8890F92ADA922A9BBB1E0374A710908D4960A102A4E31E54AB9A1EED2EE1F3BB00E9E18BCFE19F11F25159833CB629EF82905C0E7D33ADE699791710757CA4F1EBB77EFF87A6CEB05F4AF50C0917EA902648F3DEE3BC6F781B1C357B3FB89113B4C6B9C4B2B36A78C95CC301C4CF3609C92DEB0055DC6FC24BA016D0E6F4635CC09C6F094E3999FB0C70DBDE3F57594807FDA1B4DAF56B83232484DEF25"));
    }
}
