package com.google.code.lightssh.common.web.interceptor;

import org.apache.struts2.interceptor.TokenInterceptor;

import com.opensymphony.xwork2.ActionInvocation;

public class MyTokenInterceptor extends TokenInterceptor{
	
	public static final String INVALID_TOKEN_CODE = "input";

	private static final long serialVersionUID = -2252238192841446575L;
	
    protected String handleInvalidToken(ActionInvocation invocation) throws Exception {
        super.handleInvalidToken(invocation);

        return INVALID_TOKEN_CODE;
    }

}
