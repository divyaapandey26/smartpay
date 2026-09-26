package com.example.smartpay;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;   // authentication tag bits
    private static final int GCM_IV_LENGTH = 12;     // 96-bit IV recommended for GCM

    // In production this would come from a secure vault / env var.
    // Must be exactly 16 bytes for AES-128.
    private static final String SECRET = "SmartPayKey12345";

    private SecretKey getSecretKey() {
        return new SecretKeySpec(SECRET.getBytes(), ALGORITHM);
    }

    /**
     * Encrypts plaintext using AES-GCM.
     * Returns Base64( IV || ciphertext || authTag ).
     */
    public String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        // Prepend IV to ciphertext so we can retrieve it during decryption
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypts Base64( IV || ciphertext || authTag ) back to plaintext.
     * Throws AEADBadTagException if the ciphertext was tampered with.
     */
    public String decrypt(String token) throws Exception {
        byte[] combined = Base64.getDecoder().decode(token);

        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        byte[] cipherText = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText);
    }
}