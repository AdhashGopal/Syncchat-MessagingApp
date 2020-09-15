//package com.chatapp.android.app;
//
//import android.util.Base64;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
//public class AesEncryp {
//
//    private AesEncryp(byte[] key,byte[] iv) throws Exception {
//        this.encCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        this.decCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//        IvParameterSpec ivc =  new IvParameterSpec(iv);
//        this.encCipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivc);
//        this.decCipher.init(Cipher.DECRYPT_MODE, skeySpec, ivc);
//    }
//    private Cipher encCipher =  null;
//    private Cipher decCipher =  null;
//
//    public byte[] encBytes(byte[] srcBytes) throws Exception{
//        byte[] encrypted = encCipher.doFinal(srcBytes);
//        return encrypted;
//    }
//    public byte[] decBytes(byte[] srcBytes) throws Exception{
//        byte[] decrypted = decCipher.doFinal(srcBytes);
//        return decrypted;
//    }
//    public String encText(String srcStr) throws Exception {
//        byte[] srcBytes = srcStr.getBytes("utf-8");
//        byte[] encrypted = encBytes(srcBytes);
//        return Base64.encode(encrypted,Base64.DEFAULT);
//    }
//    public String decText(String srcStr) throws Exception {
//        byte[] srcBytes = Base64.decode(srcStr);
//        byte[] decrypted = decBytes(srcBytes);
//        return new String(decrypted,"utf-8");
//    }
//    public static final AesEncryp getInstance(byte[] key,byte[] iv) throws Exception{
//        AesEncryp sub = new AesEncryp(key,iv);
//        return sub;
//    }
//}
