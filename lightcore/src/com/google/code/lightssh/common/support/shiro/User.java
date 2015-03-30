package com.google.code.lightssh.common.support.shiro;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 
 * @author Aspen
 * @date 2013-3-28
 * 
 */
public interface User extends Serializable{
	
	/**
	 * 用户ID
	 */
	public String getUserId();
	
	/**
	 * 用户名
	 */
	public String getUserName();
	
	/**
	 * 最后锁定时间
	 */
	public Calendar getLastLoginLockTime( );
	
	/**
	 * 是否有效
	 */
	public boolean isEnabled( );
	
	/**
	 * 是否过期
	 */
	public boolean isExpired( );

}
