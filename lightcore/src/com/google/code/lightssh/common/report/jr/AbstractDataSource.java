package com.google.code.lightssh.common.report.jr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * abstract datesource
 *
 * 
 * @author YangXiaojin
 *
 */
public abstract class AbstractDataSource {
	/** method prefix */
	public static final String METHOD_PREFIX = "get";
	
	/** method split */
	public static final String DOT = ".";

	/**
	 * field value
	 */
	@SuppressWarnings("rawtypes")
	public Object fieldValue(Object object, String name ){
		Object invokeObj = null;
		
		int intDot = name.indexOf( DOT );
		String currentName = name;
		String remainName = null;		
		
		if( intDot > 0 ){
			currentName = name.substring( 0,intDot );
			remainName	 = name.substring( intDot + 1 );
		}
		
		Class clazz = object.getClass();
		Method[] methods = clazz.getMethods();
				
		for( Method item:methods ){
			if( item.getName().equalsIgnoreCase( METHOD_PREFIX + currentName ) ){					
				try {
					invokeObj = item.invoke( object );	
					break;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}				
			}
		}
		
		if( intDot < 0 || invokeObj == null ) return invokeObj;

		return fieldValue( invokeObj,remainName );
	}
	
}
