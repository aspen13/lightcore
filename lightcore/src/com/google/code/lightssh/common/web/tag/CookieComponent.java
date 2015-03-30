package com.google.code.lightssh.common.web.tag;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class CookieComponent extends Component{

    private static final Logger LOG = LoggerFactory.getLogger(CookieComponent.class);
    
    protected HttpServletRequest request;

    protected String defaultValue;
    protected String value;
    protected boolean escapeHtml = true;
    protected boolean escapeJavaScript = false;
    protected boolean escapeXml = false;
    protected boolean escapeCsv = false;
    
	public CookieComponent(ValueStack stack,HttpServletRequest request) {
		super(stack);
		this.request = request;
	}

    @StrutsTagAttribute(description="The default value to be used if <u>value</u> attribute is null")
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @StrutsTagAttribute(description="Deprecated. Use 'escapeHtml'. Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscape(boolean escape) {
        this.escapeHtml = escape;
    }

    @StrutsTagAttribute(description="Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscapeHtml(boolean escape) {
        this.escapeHtml = escape;
    }

    @StrutsTagAttribute(description="Whether to escape Javascript", type="Boolean", defaultValue="false")
    public void setEscapeJavaScript(boolean escapeJavaScript) {
        this.escapeJavaScript = escapeJavaScript;
    }

    @StrutsTagAttribute(description="Value to be displayed", type="Object", defaultValue="&lt;top of stack&gt;")
    public void setValue(String value) {
        this.value = value;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @StrutsTagAttribute(description="Whether to escape CSV (useful to escape a value for a column)", type="Boolean", defaultValue="false")
    public void setEscapeCsv(boolean escapeCsv) {
        this.escapeCsv = escapeCsv;
    }

    @StrutsTagAttribute(description="Whether to escape XML", type="Boolean", defaultValue="false")
    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        String actualValue = null;

        if (value == null) {
            value = "top";
        }
        else {
        	value = stripExpressionIfAltSyntax(value);
        }

        Cookie[] cookies = request.getCookies();
        if( cookies != null ){
        	for( Cookie cookie:cookies)
        		if( cookie.getName().equals( value ) ){
        			actualValue = cookie.getValue();
        			break;
        		}
        }

        try {
            if (actualValue != null) {
                writer.write(prepare(actualValue));
            } else if (defaultValue != null) {
                writer.write(prepare(defaultValue));
            }
        } catch (IOException e) {
            LOG.info("Could not print out value '" + value + "'", e);
        }

        return result;
    }

    protected String prepare(String value) {
    	String result = value;
        if (escapeHtml) {
        	result = StringEscapeUtils.escapeHtml3(result);
        }
        if (escapeJavaScript) {
        	result = StringEscapeUtils.escapeEcmaScript(result);
        }
        if (escapeXml) {
        	result = StringEscapeUtils.escapeXml(result);
        }
        if (escapeCsv) {
            result = StringEscapeUtils.escapeCsv(result);
        }

        return result;
    }

}
