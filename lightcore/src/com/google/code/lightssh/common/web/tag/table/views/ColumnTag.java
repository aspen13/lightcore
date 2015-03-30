package com.google.code.lightssh.common.web.tag.table.views;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import com.google.code.lightssh.common.web.tag.table.components.Column;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * 表格列标签
 * @author YangXiaojin
 *
 */
public class ColumnTag extends AbstractUITag{

	private static final long serialVersionUID = -5633593295091876206L;
	
	protected String sortKey;
	protected String sortable;
	protected String width;
	protected String colClass;
    
	/**
	 * 是否动态列
	 */
	protected String dynamic;
	
    /**
     * 显示顺序
     */
    protected String sequence;

	/**
	 * @see org.apache.struts2.views.jsp.ComponentTagSupport#getBean(com.opensymphony.xwork2.util.ValueStack,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Column(stack, req, res);
	}

	/**
	 * @see com.opensymphony.webwork.views.jsp.ui.AbstractUITag#populateParams()
	 */
	protected void populateParams() {
		super.populateParams();

		Column column = ((Column) component);
		column.setSortKey(sortKey);
		column.setSortable("true".equals(sortable) );
		column.setDynamic("true".equals(dynamic) );
		column.setTitle(title);
		column.setWidth(width);
		column.setColClass( colClass );
		column.setValue(value);
		column.setSequence(sequence);
	}

	public void setSortable(String sortable) {
		this.sortable = sortable;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setDynamic(String dynamic) {
		this.dynamic = dynamic;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

}
