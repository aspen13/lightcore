package com.google.code.lightssh.common.web.tag;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.code.lightssh.common.web.SessionKey;
import com.google.code.lightssh.common.web.filter.ThemeFilter;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class Theme extends CookieComponent{
	
	private static final Logger LOG = LoggerFactory.getLogger(Theme.class);
	
	public static final String DEFAULT_THEME = "default";

	public Theme(ValueStack stack, HttpServletRequest request) {
		super(stack, request);
		this.request = request;
	}
	
    public boolean start(Writer writer) {
        String actualValue = null;

        if (value == null) {
            value = "theme";
        }

        Cookie[] cookies = request.getCookies();
        if( cookies != null ){
        	for( Cookie cookie:cookies)
        		if( cookie.getName().equals( value ) ){
        			actualValue = cookie.getValue();
        			break;
        		}
        }
        
        if( !ThemeFilter.ALLOWED_THEME_SET.contains(actualValue)){
        	actualValue = (String)request.getSession().getAttribute(
        			SessionKey.PREFERRED_THEME);
        }
        
        if( defaultValue == null )
        	defaultValue = DEFAULT_THEME;

        try {
            if (actualValue != null) {
                writer.write(prepare(actualValue));
            } else if (defaultValue != null) {
                writer.write(prepare(defaultValue));
            }
        } catch (IOException e) {
            LOG.info("Could not print out value '" + value + "'", e);
        }

        return true;
    }

}
