package com.google.code.lightssh.common.entity;

import java.io.Serializable;


/**
 * persistence 
 * @author YangXiaojin
 */
public interface Persistence<ID extends Serializable> extends Serializable,Insertable{
	
	/**
	 * identity for persistence
	 */
	public ID getIdentity( );

}
