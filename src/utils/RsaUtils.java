package utils;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class RsaUtils {


    public static String modulusString = "BD325CE52FC6BA090AC0C7A2039236587F99C30FA518F601F2AD33019514EE5A4340A964853E1BDF5374AB4AC22F5CFF3288E5DB94E6752B4999972DF4E23DACACAE4E4DCFB6CBAE256F1B19C4BA892D54C7A3E068F93AB47EC50635556FC223F02CB1F520631E2F03E5509B6C1E24DFB7962BCD6DC74159BF0E5AFC03D9A00D";
    public static String publicExponentString = "10001";
    public static String privateExponentString = "private";

    /**
     * 加密,用公钥.是16进制形式的公钥，适用于新浪，腾讯。
     *
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        // TODO Auto-generated method stub
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        byte[] encryptedBytes = null;
        try {


            //由n和e获取公钥
            publicKey = getPublicKey(modulusString, publicExponentString);

            //由n和d获取私钥
            //privateKey=getPrivateKey(modulusString, privateExponentString);

            //公钥加密
            encryptedBytes = encrypt(data.getBytes(), publicKey);

            //私钥解密
            //byte[] decryptedBytes=decrypt(encryptedBytes, privateKey);
            //System.out.println("解密后："+new String(decryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytesToHexString(encryptedBytes);
    }

    /**
     * 公钥加密，公钥为base64的形式，适用于百度。
     *
     * @param data
     * @param base64Key base64的key,请解密后转为16进制再传进来。
     * @return
     */
    public static String encryptBase64(String data, String base64Key) {
        // TODO Auto-generated method stub
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        byte[] encryptedBytes = null;
        try {


            //由n和e获取公钥 先把base64解密出byte，再转换成16进制的字符串。
            publicKey = getPublicKey(base64Key, "010001");

            //由n和d获取私钥
            //privateKey=getPrivateKey(modulusString, privateExponentString);

            //公钥加密
            encryptedBytes = encrypt(data.getBytes(), publicKey);

            //私钥解密
            //byte[] decryptedBytes=decrypt(encryptedBytes, privateKey);
            //System.out.println("解密后："+new String(decryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    //将base64编码后的公钥字符串转成PublicKey实例
    public static PublicKey getPublicKey(String modulusStr, String exponentStr) throws Exception {
        BigInteger modulus = new BigInteger(modulusStr, 16);
        BigInteger exponent = new BigInteger(exponentStr, 16);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }

    //将base64编码后的私钥字符串转成PrivateKey实例
    public static PrivateKey getPrivateKey(String modulusStr, String exponentStr) throws Exception {
        BigInteger modulus = new BigInteger(modulusStr);
        BigInteger exponent = new BigInteger(exponentStr);
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(privateKeySpec);
    }

    //公钥加密
    public static byte[] encrypt(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    //私钥解密
    public static byte[] decrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

    //byte数组转十六进制字符串
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    //十六进制字符串转byte数组
    public static byte[] hexString2Bytes(String hex) {
        int len = (hex.length() / 2);
        hex = hex.toUpperCase();
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;

    }
}
