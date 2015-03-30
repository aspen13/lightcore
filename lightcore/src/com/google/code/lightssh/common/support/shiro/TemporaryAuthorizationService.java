
package com.google.code.lightssh.common.support.shiro;

import javax.servlet.ServletRequest;

/** 
 * @author YangXiaojin
 * @date 2013-3-7
 * @description：临时授权服务
 */

public interface TemporaryAuthorizationService {
	
	/**
	 * 是否临时授权
	 */
	public boolean authorize(String[] perms,ServletRequest request);

}
