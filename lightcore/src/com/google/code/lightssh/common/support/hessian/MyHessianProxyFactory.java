 package com.google.code.lightssh.common.support.hessian;

import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianProxyFactory;

/**
 * 扩展HessianProxyFactory
 * @author YangXiaojin
 *
 */
public class MyHessianProxyFactory extends HessianProxyFactory{
	
	/**
	 * 自定义Hessian连接工厂,在开启连接的时候在消息头内假如数字签名和时间戳
	 * @param crypt
	 * @param privateKey
	 */
	public void sendSignHeader(String crypt,String privateKey){
		HessianConnectionFactory connectionFactory = new MyHessianConnectionFactory(crypt, privateKey);
		connectionFactory.setHessianProxyFactory(this);
		this.setConnectionFactory(connectionFactory);
	}

}
