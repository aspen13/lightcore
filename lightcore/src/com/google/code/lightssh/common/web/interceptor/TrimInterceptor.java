package com.google.code.lightssh.common.web.interceptor;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 字符空格裁剪
 * @author YangXiaojin
 *
 */
public class TrimInterceptor extends AbstractInterceptor{

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> parameters = invocation.getInvocationContext().getParameters();
        for (String key : parameters.keySet()) {
            Object value = parameters.get(key);
            if ( value != null && value instanceof String[]) {
               String[] valueArray = (String[]) value;
               for (int i = 0; i < valueArray.length; i++) { 
            	   if( valueArray[i] != null )
            		   valueArray[i] = valueArray[i].trim();
               }
               parameters.put(key, valueArray); 
            }
        }
        
        return invocation.invoke();
	}

}
