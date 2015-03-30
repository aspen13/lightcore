package com.google.code.lightssh.common.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
/**
 * DSA加密解密工具类
 * @author YangHan
 *
 */
public class DSAUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DSAUtil.class);
	
	/*参数常量*/
	private static final String KEY_ALGORITHM = "DSA";
	private static final String SIGNATURE_ALGORITHM = "DSA";
	private static final String DEFAULT_SEED = "HJG8awfjas7";//默认加密种子
	private static final String PUBLIC_KEY = "DSAPublicKey";//公钥KEY名称
	private static final String PRIVATE_KEY = "DSAPrivateKey";//私钥KEY名称
	/*公钥私钥缓存类*/
	private static Map<String,Map<String,Object>> keyMaps = new HashMap<String, Map<String,Object>>();
	/**
	 * 根据加密种子生成密钥
	 * @param seed 加密种子
	 * @return 密钥对象
	 * @throws Exception
	 */
	public static Map<String,Object> initKey(String seed) throws Exception{
		logger.info("开始生成密钥....");
		if(!CollectionUtils.isEmpty(keyMaps.get(seed))){
			return keyMaps.get(seed);
		}
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.setSeed(seed.getBytes());
		keyGen.initialize(640,secureRandom);
		
		KeyPair keyPair = keyGen.generateKeyPair();
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		Map<String,Object> map = new HashMap<String, Object>(2);
		map.put(PRIVATE_KEY, privateKey);
		map.put(PUBLIC_KEY, publicKey);
		keyMaps.put(seed, map);
		return map;
	}
	/**
	 * 生成默认密钥
	 * @return 密钥对象
	 * @throws Exception
	 */
	public static Map<String,Object> initKey() throws Exception{
		return initKey(DEFAULT_SEED);
	}
	/**
	 * 获取私钥
	 * @param keyMap 密钥缓存
	 * @return 私钥字符串
	 */
	public static String getPrivateKey(Map<String,Object> keyMap){
		if(CollectionUtils.isEmpty(keyMap)){
			logger.error("密钥缓存为空,请检查是否生成了密钥....");
			throw new RuntimeException("密钥缓存为空,请检查是否生成了密钥....");
		}
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		if(key == null){
			logger.error("密钥为空,请检查密钥缓存....");
			throw new RuntimeException("密钥为空,请检查密钥缓存....");
		}
		return Base64.encodeBase64String(key.getEncoded());
	}
	/**
	 * 获取公钥
	 * @param keyMap 密钥缓存
	 * @return 公钥字符串
	 */
	public static String getPublicKey(Map<String,Object> keyMap){
		if(CollectionUtils.isEmpty(keyMap)){
			logger.error("密钥缓存为空,请检查是否生成了密钥....");
			throw new RuntimeException("密钥缓存为空,请检查是否生成了密钥....");
		}
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		if(key == null){
			logger.error("密钥为空,请检查密钥缓存....");
			throw new RuntimeException("密钥为空,请检查密钥缓存....");
		}
		return Base64.encodeBase64String(key.getEncoded());
	}
	/**
	 * 用私钥对信息进行数字前面
	 * @param data 签名数据
	 * @param privateKey 私钥-base64加密
	 * @return 签名字符串
	 */
	public static String sign(byte[] data,String privateKey) throws Exception{
		logger.info("用私钥对信息进行数字签名...");
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey priKey = keyFactory.generatePrivate(keySpec);
		//用私钥对信息进行数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);
		return Base64.encodeBase64URLSafeString(signature.sign());
	}
	public static boolean verify(byte[] data,String publicKey,String sign) throws Exception{
		/*转换成公钥*/
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		/*用公钥对信息进行验签*/
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);
		return signature.verify(Base64.decodeBase64(sign));
	}
	public static Map<String,Map<String,Object>> getKeyMaps(){
		return keyMaps;
	}
}
