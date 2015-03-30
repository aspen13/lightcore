package com.google.code.lightssh.common.web.tag.table.components;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.UIBean;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * 公共Bean
 * @author YangXiaojin
 *
 */
public abstract class AbstractURLBean extends UIBean{

	public AbstractURLBean(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}
	
	protected String queryParams( String excludeName ){
		return queryParams(new String[]{excludeName});
	}
	
	/**
	 * 是否包含
	 */
	private boolean include( String name,String[] array ){
		if( array == null || array.length == 0 )
			return false;
		
		for( String item:array )
			if(( item == null && name == null) 
					|| (item != null && item.equals(name)))
				return true;
		
		return false;
	}
	
    @SuppressWarnings("rawtypes")
	protected String queryParams( String[] excludeNames ){
    	Enumeration pNames = request.getParameterNames();
    	StringBuffer sb = new StringBuffer("?");
    	while( pNames.hasMoreElements() ){
    		String name = pNames.nextElement().toString();
    		if( include(name,excludeNames) )
    			continue;
    		
    		String[] values = request.getParameterValues(name);
    		if( values != null )
    			for( String value:values )
    				sb.append("&" + name + "=" + value );
    	}
    	
    	return sb.toString();
    }

}
