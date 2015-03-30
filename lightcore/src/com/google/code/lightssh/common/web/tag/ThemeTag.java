package com.google.code.lightssh.common.web.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Theme tag
 * @author YangXiaojin
 *
 */
public class ThemeTag extends CookieTag{

	private static final long serialVersionUID = 1L;
	
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Theme(stack,req);
    }
	
    protected void populateParams() {
        super.populateParams();

        Theme tag = (Theme) component;
        tag.setDefault(defaultValue);
        tag.setValue(value);
        tag.setEscape(escapeHtml);
        tag.setEscapeJavaScript(escapeJavaScript);
        tag.setEscapeXml(escapeXml);
        tag.setEscapeCsv(escapeCsv);
    }

}
