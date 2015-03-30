package com.google.code.lightssh.common.model;

/**
 * 序号生成接口
 * @author YangXiaojin
 *
 */
public interface Sequenceable {
	
	/**
	 * 序号KEY
	 */
	public String getSequenceKey( );
	
	/**
	 * 流水号步长
	 */
	public int getSequenceStep( );
	
	/**
	 * 流水号长度
	 */
	public int getSequenceLength( );

}
