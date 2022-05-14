package com.am.pswenc;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Crypt {
    private SecretKey key;
    private final int key_length = 128;
    private Cipher cipher = Cipher.getInstance("AES"); // Zatím ECB

    // Konstruktor
    Crypt(String keyString, byte[] saltIn, int iterace) throws Exception {
        char[] keyArray = keyString.toCharArray();
        byte[] salt = Arrays.copyOf(saltIn, saltIn.length);
        // SKF - Pro převádění mezi průhlednými - KeySpec
        // a neprůhlednými - Key reprezentacemi klíčů
        PBEKeySpec kSpec = new PBEKeySpec(keyArray, salt, iterace, key_length);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        key = new SecretKeySpec((skf.generateSecret(kSpec)).getEncoded(), "AES");
    }
    public String Decrypt(String EncText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, this.key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(EncText)));
    }
}
