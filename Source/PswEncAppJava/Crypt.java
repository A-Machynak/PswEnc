package com.am.pswenc;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

class Crypt {
    private char[] keyArray;
    private SecretKey key;
    private byte[] saltC = new byte[16];
    private static int iterace;
    private final int key_length = 128;
    private PBEKeySpec kSpec;
    private Random r = new SecureRandom();
    // SKF - Pro převádění mezi "průhlednými" - KeySpec
    // a "neprůhlednými" - Key reprezentacemi klíčů
    private SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    private Cipher cipher = Cipher.getInstance("AES"); // Zatím ECB

    // Konstruktory
    Crypt(String keyString, int iter) throws Exception {
        iterace = iter;
        this.keyArray = keyString.toCharArray();
        // Vygenerovat salt
        r.nextBytes(saltC);
        // KeySpec - "průhledná" reprezentace klíče s metadaty
        kSpec = new PBEKeySpec(keyArray, saltC, iterace, key_length);
        key = new SecretKeySpec((skf.generateSecret(kSpec)).getEncoded(), "AES");
    }

    Crypt(String keyString, byte[] saltIn, int iter) throws Exception {
        iterace = iter;
        keyArray = keyString.toCharArray();
        saltC = Arrays.copyOf(saltIn, saltIn.length);
        kSpec = new PBEKeySpec(keyArray, saltC, iterace, key_length);
        key = new SecretKeySpec((skf.generateSecret(kSpec)).getEncoded(), "AES");
    }

    String Encrypt(String text) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new String(Base64.getEncoder().encode(cipher.doFinal(text.getBytes())));
    }
    String Decrypt(String EncText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(EncText)));
    }
    byte[] getSalt() {
        return this.saltC;
    }
}