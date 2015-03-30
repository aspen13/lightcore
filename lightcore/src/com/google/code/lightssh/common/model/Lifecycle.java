package com.google.code.lightssh.common.model;

import java.util.Date;

/**
 * 生命周期
 * @author YangXiaojin
 */
public interface Lifecycle {
	
	/**
	 * 开始时间
	 */
	public Date getFromDate();
	
	/**
	 * 结束时间
	 */
	public Date getThruDate();
	
	/**
	 * 是否过期
	 */
	public boolean isExpired( Date date );
	
	/**
	 * 是否过期
	 */
	public boolean isExpired( );
	
}
