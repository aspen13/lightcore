package com.google.code.lightssh.common.web.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ContextBeanTag;

import com.opensymphony.xwork2.util.ValueStack;

public class TreeTag extends ContextBeanTag {
	
	private static final long serialVersionUID = 6260067463708847285L;
	
	protected String value;
    protected String children;
    protected String parent;
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TreeComponent(stack);
    }
	
    protected void populateParams() {
        super.populateParams();

        TreeComponent tag = (TreeComponent) getComponent();
        tag.setValue(value);
    }
    
    public int doEndTag() throws JspException {
        component = null;
        return EVAL_PAGE;
    }
    
    public int doAfterBody() throws JspException {
        boolean again = component.end(pageContext.getOut(), getBody());

        if (again) {
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (Exception e) {
                    throw new JspException(e.getMessage());
                }
            }
            return SKIP_BODY;
        }
    }

	public void setValue(String value) {
		this.value = value;
	}

	public void setChildren(String children) {
		this.children = children;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

}
