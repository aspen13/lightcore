package com.google.code.lightssh.common.web.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;

public class CookieTag extends ComponentTagSupport{

	private static final long serialVersionUID = 8559911878283347793L;
	
	protected String defaultValue;
	protected String value;
	protected boolean escapeHtml = true;
	protected boolean escapeJavaScript = false;
	protected boolean escapeXml = false;
	protected boolean escapeCsv = false;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new CookieComponent(stack,req);
    }

    protected void populateParams() {
        super.populateParams();

        CookieComponent tag = (CookieComponent) component;
        tag.setDefault(defaultValue);
        tag.setValue(value);
        tag.setEscape(escapeHtml);
        tag.setEscapeJavaScript(escapeJavaScript);
        tag.setEscapeXml(escapeXml);
        tag.setEscapeCsv(escapeCsv);
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setEscape(boolean escape) {
        this.escapeHtml = escape;
    }

    public void setEscapeHtml(boolean escapeHtml) {
        this.escapeHtml = escapeHtml;
    }

    public void setEscapeJavaScript(boolean escapeJavaScript) {
        this.escapeJavaScript = escapeJavaScript;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setEscapeCsv(boolean escapeCsv) {
        this.escapeCsv = escapeCsv;
    }

    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

}
