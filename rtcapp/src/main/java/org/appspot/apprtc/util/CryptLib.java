package org.appspot.apprtc.util;
/*
 * MIT License
 *
 * Copyright (c) 2017 Kavin Varnan

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.util.Arrays;

import android.util.Base64;


public class CryptLib {

    /**
     * Encryption mode enumeration
     */
    public enum EncryptMode {
        ENCRYPT, DECRYPT
    }

    // cipher to be used for encryption and decryption
    private Cipher _cx;

    // encryption key and initialization vector
    private byte[] _key, _iv;


    public CryptLib() throws NoSuchAlgorithmException, NoSuchPaddingException {
        // initialize the cipher with transformation AES/CBC/PKCS5Padding
        _cx = Cipher.getInstance("AES/CBC/PKCS5Padding");
        _key = new byte[32]; //256 bit key space
        _iv = new byte[16]; //128 bit IV
    }


    /**
     * @param inputText     Text to be encrypted or decrypted
     * @param encryptionKey Encryption key to used for encryption / decryption
     * @param mode          specify the mode encryption / decryption
     * @param initVector    Initialization vector
     * @return encrypted or decrypted bytes based on the mode
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private byte[] encryptDecrypt(String inputText, String encryptionKey,
                                  EncryptMode mode, String initVector) throws UnsupportedEncodingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        int len = encryptionKey.getBytes("UTF-8").length; // length of the key	provided

        if (encryptionKey.getBytes("UTF-8").length > _key.length)
            len = _key.length;

        int ivlength = initVector.getBytes("UTF-8").length;

        if (initVector.getBytes("UTF-8").length > _iv.length)
            ivlength = _iv.length;

        System.arraycopy(encryptionKey.getBytes("UTF-8"), 0, _key, 0, len);
        System.arraycopy(initVector.getBytes("UTF-8"), 0, _iv, 0, ivlength);


        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES"); // Create a new SecretKeySpec for the specified key data and algorithm name.

        IvParameterSpec ivSpec = new IvParameterSpec(_iv); // Create a new IvParameterSpec instance with the bytes from the specified buffer iv used as initialization vector.

        // encryption
        if (mode.equals(EncryptMode.ENCRYPT)) {
            // Potentially insecure random numbers on Android 4.3 and older. Read for more info.
            // https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
            _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);// Initialize this cipher instance
            return _cx.doFinal(inputText.getBytes("UTF-8")); // Finish multi-part transformation (encryption)
        } else {
            _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);// Initialize this cipher instance

            byte[] decodedValue = Base64.decode(inputText.getBytes("UTF-8"), Base64.DEFAULT);
            return _cx.doFinal(decodedValue); // Finish multi-part transformation (decryption)
        }
    }


    public byte[] encryptDecrypt(byte[] inputText, String encryptionKey,
                                 EncryptMode mode, String initVector) throws UnsupportedEncodingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        if (encryptionKey.equals("ChatappAdani")) {
            return inputText;
        } else {
            try {
                encryptionKey = CryptLib.SHA256(encryptionKey, 32);
                System.out.println("===encryptkey" + encryptionKey);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            int len = encryptionKey.getBytes("UTF-8").length; // length of the key	provided

            if (encryptionKey.getBytes("UTF-8").length > _key.length)
                len = _key.length;

            int ivlength = initVector.getBytes("UTF-8").length;

            if (initVector.getBytes("UTF-8").length > _iv.length)
                ivlength = _iv.length;

            System.arraycopy(encryptionKey.getBytes("UTF-8"), 0, _key, 0, len);
            System.arraycopy(initVector.getBytes("UTF-8"), 0, _iv, 0, ivlength);


            SecretKeySpec keySpec = new SecretKeySpec(_key, "AES"); // Create a new SecretKeySpec for the specified key data and algorithm name.

            IvParameterSpec ivSpec = new IvParameterSpec(_iv); // Create a new IvParameterSpec instance with the bytes from the specified buffer iv used as initialization vector.

            // encryption
            if (mode.equals(EncryptMode.ENCRYPT)) {
                byte[] two = initVector.getBytes("UTF-8");
                byte[] combined = new byte[two.length + inputText.length];


                System.arraycopy(two, 0, combined, 0, two.length);
                System.arraycopy(inputText, 0, combined, two.length, inputText.length);
                // Potentially insecure random numbers on Android 4.3 and older. Read for more info.
                // https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
                _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);// Initialize this cipher instance
                byte[] en = _cx.doFinal(combined);
                //  return Base64.encode(en,Base64.DEFAULT); // Finish multi-part transformation

                return en;
            } else {
                _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);// Initialize this cipher instance
                byte[] de = _cx.doFinal(inputText);

                //  return Base64.decode(de, Base64.DEFAULT); // Finish multi-part transformation
                byte[] de1 = Arrays.copyOfRange(de, 16, de.length);


                return de1;
            }
        }
    }


    /***
     * This function computes the SHA256 hash of input string
     * @param text input text whose SHA256 hash has to be computed
     * @param length length of the text to be returned
     * @return returns SHA256 hash of input text
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String SHA256(String text, int length) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String resultString;
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes("UTF-8"));
        byte[] digest = md.digest();

        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            result.append(String.format("%02x", b)); //convert to hex
        }

        if (length > result.toString().length()) {
            resultString = result.toString();
        } else {
            resultString = result.toString().substring(0, length);
        }

        return resultString;

    }


//    public String encryptPlainText(String plainText, String key, String iv) throws Exception {
//        byte[] bytes = encryptDecrypt(plainText, CryptLib.SHA256(key, 32), EncryptMode.ENCRYPT, iv);
//        return Base64.encodeToString(bytes, Base64.DEFAULT);
//    }
//
//    public String decryptCipherText(String cipherText, String key, String iv) throws Exception {
//        byte[] bytes = encryptDecrypt(cipherText, CryptLib.SHA256(key, 32), EncryptMode.DECRYPT, iv);
//        return new String(bytes);
//    }


//    public  byte[] encryptPlainTextWithBytearray(String plainText, String key) throws Exception {
//        byte[] bytes = encryptDecrypt(generateRandomIV16() + plainText, CryptLib.SHA256(key, 32), EncryptMode.ENCRYPT, generateRandomIV16());
//        return Base64.encode(bytes,Base64.DEFAULT);
//    }
//
//    public byte[] decryptCipherTextWithBytearray(String cipherText, String key) throws Exception {
//        byte[] bytes = encryptDecrypt(cipherText, CryptLib.SHA256(key, 32), EncryptMode.DECRYPT, generateRandomIV16());
//        String out = new String(bytes);
//        String orgOut=out.substring(16, out.length());
//        return orgOut.getBytes("UTF-8");
//    }


    public String encryptPlainTextWithRandomIV(String plainText, String key) throws Exception {
        if (key.equals("ChatappAdani")) {
            return plainText;
        } else {
            byte[] bytes = encryptDecrypt(generateRandomIV16() + plainText, CryptLib.SHA256(key, 32), EncryptMode.ENCRYPT, generateRandomIV16());
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }
    }

    public String decryptCipherTextWithRandomIV(String cipherText, String key) throws Exception {
        byte[] bytes = encryptDecrypt(cipherText, CryptLib.SHA256(key, 32), EncryptMode.DECRYPT, generateRandomIV16());
        String out = new String(bytes);
        return out.substring(16, out.length());
    }


    /**
     * Generate IV with 16 bytes
     *
     * @return
     */
    public String generateRandomIV16() {
        SecureRandom ranGen = new SecureRandom();
        byte[] aesKey = new byte[16];
        ranGen.nextBytes(aesKey);
        StringBuilder result = new StringBuilder();
        for (byte b : aesKey) {
            result.append(String.format("%02x", b)); //convert to hex
        }
        if (16 > result.toString().length()) {
            System.out.println("==stringgenerated" + result.toString());
            return result.toString();

        } else {
            System.out.println("==stringgenerated" + result.toString().substring(0, 16));
            return result.toString().substring(0, 16);
        }
    }


//    public  byte[] encodeFile(String yourKey, byte[] fileData)
//            throws Exception {
//        byte[] encrypted = null;
//
//
//        byte[] data = CryptLib.SHA256(yourKey, 32).getBytes("UTF-8");
//        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
//                "AES/CBC/ZeroBytePadding");
//
//        Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding");
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
//        encrypted = cipher.doFinal(fileData);
//        return encrypted;
//    }
//
//    public  byte[] decodeFile(String yourKey, byte[] fileData)
//            throws Exception {
//        byte[] decrypted = null;
//        Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding");
//
//        byte[] data = CryptLib.SHA256(yourKey, 32).getBytes("UTF-8");
//        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
//                "AES/CBC/ZeroBytePadding");
//        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
//        decrypted = cipher.doFinal(fileData);
//        return decrypted;
//    }

}