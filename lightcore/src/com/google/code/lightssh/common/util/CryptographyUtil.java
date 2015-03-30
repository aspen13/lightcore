package com.google.code.lightssh.common.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.google.code.lightssh.common.ApplicationException;

/**
 * 加密/解密/哈希值 实用类
 * @author YangXiaojin
 *
 */
public class CryptographyUtil{
		
	/**
	 * 16进行HASH值
	 * @param algorithm Hash 算法
	 * @param val 字符
	 * @return String
	 */
	private static String hashHex( String algorithm,String val ){
		if( val== null )
			return null;
		
		MessageDigest digest = null;
		try{
			digest = MessageDigest.getInstance( algorithm );
		}catch( NoSuchAlgorithmException e ){
			throw new ApplicationException( e );
		}
		
		return Hex.encodeHexString( digest.digest( val.getBytes() ));
	}
	
	/**
	 * 16进行HASH值
	 * @param algorithm Hash 算法
	 * @param val 字符
	 * @return String
	 */
	private static String hashBase64( String algorithm,String val ){
		if( val== null )
			return null;
		
		MessageDigest digest = null;
		try{
			digest = MessageDigest.getInstance( algorithm );
		}catch( NoSuchAlgorithmException e ){
			throw new ApplicationException( e );
		}
		
		return Base64.encodeBase64String( digest.digest( val.getBytes() ));
	}
	
	/**
	 * 16进制 Md5 Hash 值
	 */
	public static String hashMd5Hex( String val ){
		return hashHex("MD5",val);
	}
	
	/**
	 * base64编码  Md5 Hash 值
	 */
	public static String hashMd5Base64( String val ){
		return hashBase64("MD5",val);
	}
	
	/**
	 * 16进制 sha256 Hash 值
	 */
	public static String hashSha256Hex( String val ){
		return hashHex("SHA-256",val);
	}
	
	/**
	 * base64编码  sha256 Hash 值
	 */
	public static String hashSha256Base64( String val ){
		return hashBase64("SHA-256",val);
	}
	
	
	/**
	 * base64编码  SHA-1 Hash 值
	 */
	public static String hashSha1Base64( String val ){
		return hashBase64("SHA-1",val);		
	}
	
	/**
	 * 16进制 SHA-1 Hash 值
	 */
	public static String hashSha1Hex( String val ){
		return hashHex("SHA-1",val);
	}
	
	/**
	 * AES解密
	 * @param content  待解密内容
	 * @param password 解密密钥
	 * @return byte array
	 */
	private static byte[] aesDecrypt(byte[] content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	   /**将二进制转换成16进制 
	    * @param buf 
	    * @return 
	    */  
	private static String byte2Hex(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	
	
    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    private static byte[] hex2Byte(String hexStr) {
            if (hexStr.length() < 1)
                    return null;
            byte[] result = new byte[hexStr.length()/2];
            for (int i = 0;i< hexStr.length()/2; i++) {
                    int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
                    int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
                    result[i] = (byte) (high * 16 + low);
            }
            return result;
    }
	
	/**
	 * AES解密
	 * @param content 待解密内容
	 * @param password 密钥
	 * @return String
	 */
	public static String aesDecrypt( String content, String password){
		if( content == null )
			return null;
		byte[] byte_result = aesDecrypt( hex2Byte(content),password );
			
		//return byte2Hex( byte_result );		
		return new String( byte_result ); 
		//return String.valueOf( byte_result ); 
	}
	
	 /** 
	  * AES加密 
	  * @param content 需要加密的内容 
	  * @param password  加密密码 
	  * @return byte array
	  */  
	public static String aesEncrypt(String content, String password) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			
			return byte2Hex( result ); // ACTION
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
