package com.google.code.lightssh.common.web.interceptor;


import org.apache.struts2.json.JSONValidationInterceptor;

import com.opensymphony.xwork2.ValidationAware;

/**
 * json 验证
 * @author YangXiaojin
 *
 */
public class JsonValidationInterceptor extends JSONValidationInterceptor{

	private static final long serialVersionUID = 3518588313671303478L;
	
    /**
     * 在原方法基本上移除首尾 "/*" 和" * /"符号
     */
    protected String buildResponse(ValidationAware validationAware) {
        return super.buildResponse(validationAware).replaceFirst("^/\\*","").replaceAll("\\*/$","");
    }

}
