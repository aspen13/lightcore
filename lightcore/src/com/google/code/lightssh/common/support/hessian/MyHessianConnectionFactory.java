package com.google.code.lightssh.common.support.hessian;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianURLConnection;
import com.caucho.hessian.client.HessianURLConnectionFactory;
import com.google.code.lightssh.common.util.DSAUtil;

public class MyHessianConnectionFactory extends HessianURLConnectionFactory{

	private static final Logger logger = LoggerFactory.getLogger(MyHessianConnectionFactory.class);
	
	private String crypt;//数字签名
	private String privateKey;//私钥
	
	public MyHessianConnectionFactory(String crypt,String privateKey) {
		this.crypt = crypt;
		this.privateKey = privateKey;
	}
	
	@Override
	public HessianConnection open(URL url) throws IOException {
		HessianURLConnection hessianURLConnection = (HessianURLConnection) super.open(url);
		long timeStamp = System.currentTimeMillis();
		if(StringUtils.isNotBlank(crypt)){
			try {
				String signature = createSignature(timeStamp, crypt, privateKey);
				hessianURLConnection.addHeader(MyHessianCommon.SIGNATURE_VALUE_KEY, signature);
				hessianURLConnection.addHeader(MyHessianCommon.TIMESTAMP_VALUE_KEY,Long.toString(timeStamp));
			} catch (Exception e) {
				logger.error("往消息头加入数字签名时发生错误：[{}]",e.getMessage());
				e.printStackTrace();
			}
		}
		return hessianURLConnection;
	}
	private String createSignature(long timeStamp,String crypt,String privateKey) throws Exception{
		if(timeStamp <=0 || StringUtils.isEmpty(crypt)){
			throw new Exception("timestamp or crypt is invalied");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(timeStamp);
		sb.append("},{");
		sb.append(crypt);
		sb.append("}");
		return DSAUtil.sign(sb.toString().getBytes(), privateKey);
	}
}

