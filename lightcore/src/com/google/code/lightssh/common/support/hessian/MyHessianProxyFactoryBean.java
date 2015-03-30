package com.google.code.lightssh.common.support.hessian;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import com.google.code.lightssh.common.config.SystemConfig;
import com.google.code.lightssh.common.util.DSAUtil;

/**
 * 扩展HessianProxyFactoryBean
 * @author YangXiaojin
 *
 */
public class MyHessianProxyFactoryBean extends HessianProxyFactoryBean{
	
	/**
	 * 远程服务系统名称前缀
	 */
	public static final String REMOTING_SYSTEM_PREFIX = "remoting.system.";
	
	/**
	 * 连接超时KEY
	 */
	public static final String REMOTING_CONN_TIMEOUT_KEY = "remoting.connect.timout";
	
	/**
	 * 读超时KEY
	 */
	public static final String REMOTING_READ_TIMEOUT_KEY = "remoting.read.timout";
	
	/**
	 * 密钥加密种子
	 */
	public static final String REMOTING_CRYPT_VALUE_KEY = "remoting.crypt.value";
	
	/**
	 * 连接超时
	 */
	private int connectTimeout = 5000;

	/**
	 * 读超时
	 */
    private int readTimeout = 5000;
	
    /**
     * 是否使用DES加密
     */
    private boolean isUsedDes = false;
    /**
     * 密钥缓存
     */
    private Map<String,Object> dsaKeyMap;
	/**
	 * 扩展DSA加密验证参数
	 **/
	private String crypt;//生成密钥加密种子
	
	/**
	 * 子系统名称
	 */
	private String system;
	
	/** 
	 * 系统参数 
	 */
	@Resource
	private SystemConfig systemConfig;
	
	/**
	 * 扩展Hessian代理
	 */
	private MyHessianProxyFactory proxyFactory = new MyHessianProxyFactory();
	
	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setUsedDes(boolean isUsedDes) {
		this.isUsedDes = isUsedDes;
	}

	/**
	 * 系统远程服务地址前缀
	 */
	protected String getServiceUrlPrefix( ){
		if( systemConfig != null ){
			return systemConfig.getProperty(REMOTING_SYSTEM_PREFIX + system );
		}
		
		return null;
	}
	
	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getServiceUrl() {
		String serviceAction = super.getServiceUrl();
		if( serviceAction != null && serviceAction.startsWith("http"))
			return serviceAction;
		
		String prefix = getServiceUrlPrefix();
		if( prefix != null && serviceAction != null ){
			if( prefix.endsWith("/") && serviceAction.startsWith("/") )
				prefix = prefix.substring(0, prefix.length()-1);
			
			if( !prefix.endsWith("/") && !serviceAction.startsWith("/") )
				prefix = prefix + "/";
		}
		
		return (prefix==null?"":prefix) + serviceAction;
	}
	
	private void initConfig(){
		if( systemConfig != null ){
			try{
				String connTimeoutTxt = systemConfig.getProperty(REMOTING_CONN_TIMEOUT_KEY);
				this.connectTimeout = Integer.parseInt(connTimeoutTxt );
			}catch( Exception e ){
				//ignore
			}

			try{
				String readTimeoutTxt = systemConfig.getProperty(REMOTING_READ_TIMEOUT_KEY);
				this.readTimeout = Integer.parseInt(readTimeoutTxt );
			}catch( Exception e ){
				//ignore
			}
			
			try{
				if(isUsedDes){
					String cryptTxt = systemConfig.getProperty(REMOTING_CRYPT_VALUE_KEY);
					this.crypt = cryptTxt;
					dsaKeyMap = StringUtils.isEmpty(crypt) ? DSAUtil.initKey() : DSAUtil.initKey(crypt);
				}
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
    public void afterPropertiesSet() {
    	initConfig();
    	if(isUsedDes){
			proxyFactory.sendSignHeader(crypt, DSAUtil.getPrivateKey(dsaKeyMap));
		}
        proxyFactory.setReadTimeout(readTimeout);
        proxyFactory.setConnectTimeout(connectTimeout);
        setProxyFactory(proxyFactory);
        super.afterPropertiesSet();
    }

}
