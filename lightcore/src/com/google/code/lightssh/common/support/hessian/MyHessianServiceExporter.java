package com.google.code.lightssh.common.support.hessian;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.util.CollectionUtils;

import com.google.code.lightssh.common.util.DSAUtil;

public class MyHessianServiceExporter extends HessianServiceExporter{

	private static final Logger logger = LoggerFactory.getLogger(MyHessianServiceExporter.class);
	
	private String crypt;
	private Long timeValue;//消息过期时间(单位毫秒)
	private boolean isUseDSA = false;//是否使用加密
	
	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if(!isUseDSA){
			super.handleRequest(request, response);
			return;
		}
		String signature = request.getHeader(MyHessianCommon.SIGNATURE_VALUE_KEY);
		String timestamp = request.getHeader(MyHessianCommon.TIMESTAMP_VALUE_KEY);
		if(StringUtils.isEmpty(signature)){
			logger.error("获取到的数字签名为空....");
			throw new ServletException("获取到的数字签名为空....");
		}
		if(StringUtils.isEmpty(timestamp)){
			logger.error("获取到的时间戳为空....");
			throw new ServletException("获取到的时间戳为空....");
		}
		long now = System.currentTimeMillis();
		long range = now - Long.parseLong(timestamp);
		if(range < 0){
			range = -range;
		}
		if(range > timeValue){
			logger.error("消息已过期，请重新调用...");
			throw new ServletException("消息已过期，请重新调用...");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(timestamp);
		sb.append("},{");
		sb.append(crypt);
		sb.append("}");
		try {
			if(DSAUtil.verify(sb.toString().getBytes(), DSAUtil.getPublicKey(CollectionUtils.isEmpty(DSAUtil.getKeyMaps().get(crypt)) ? DSAUtil.initKey(crypt) : DSAUtil.getKeyMaps().get(crypt)), signature)){
				super.handleRequest(request, response);
			}else{
				logger.error("验签失败..,请检查数字前面和密钥,重新调用...");
				throw new  ServletException("验签失败..,请检查数字前面和密钥,重新调用...");
			}
		} catch (Exception e) {
			logger.error("Failed to process remote request!",e);
			throw new ServletException(e);
		}
	}
	
	public void setCrypt(String crypt) {
		this.crypt = crypt;
	}
	public void setTimeValue(Long timeValue) {
		this.timeValue = timeValue;
	}
	public void setIsUseDSA(boolean isUseDSA) {
		this.isUseDSA = isUseDSA;
	}
}
