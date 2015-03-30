package com.google.code.lightssh.common.web.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

/**
 * 
 * @author YangXiaojin
 *
 */
public class BooleanConverter extends StrutsTypeConverter {
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	
	@SuppressWarnings("rawtypes")
	public Object convertFromString(Map context, String[] values, Class toClass){
		if( values == null || values[0] == null)
			return null;
		else if( TRUE.equalsIgnoreCase( values[0]) )
			return Boolean.TRUE;
		else if( FALSE.equalsIgnoreCase( values[0]) )
			return Boolean.FALSE;
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	public String convertToString(Map context, Object o) {
		return o==null?"":o.toString();
	}
	
}