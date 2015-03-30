package com.google.code.lightssh.common.support.shiro;

import java.util.Calendar;

/**
 * 
 * @author Aspen
 * @date 2013-3-28
 * 
 */
public interface UserService {
	
	/**
	 * 查询用户
	 * @param username 用户名称
	 * @return
	 */
	public User getUser( String username );
	
	/**
	 * 锁定登录帐户
	 * @param username 登录用户名
	 * @param time 时间
	 */
	public boolean lock(String username,Calendar time);
	
	/**
	 * 登录成功日志
	 * @param user 用户
	 * @param time 时间
	 * @param ip IP地址
	 */
	public void logLoginSuccess(User user,Calendar time,String ip);
	
	/**
	 * 登录失败志
	 * @param user 用户
	 * @param time 时间
	 * @param ip IP地址
	 * @param sessionId session id
	 */
	public void logLoginFailure(User user,Calendar time,String ip,String sessionId );

}
