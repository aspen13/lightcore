package com.google.code.lightssh.common.web.tag.table.views;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.util.StringUtil;
import com.google.code.lightssh.common.web.tag.table.components.Pagination;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * 分页标签
 * @author YangXiaojin
 *
 */
public class PaginationTag extends AbstractUITag{

	private static final long serialVersionUID = 172488498534505591L;
	
	private static final Logger LOG = LoggerFactory.getLogger(PaginationTag.class );
	
	/**
	 * 分页参数前缀
	 */
	protected String pageParamPrefix;
	
	/**
	 * 页码显示长度
	 */
	protected String length;
	
	/**
	 * 可变动的每页显示条数
	 */
	protected String pageSizeArray;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Pagination(stack, req, res);
	}
	
	/**
	 * @see com.opensymphony.webwork.views.jsp.ui.AbstractUITag#populateParams()
	 */
	protected void populateParams() {
		super.populateParams();

		Pagination pagination = ((Pagination) component);
		pagination.setValue(value);
		
		if( StringUtil.hasText(length))
			pagination.setLength( Integer.valueOf(length) );
		
		if( StringUtil.hasText(pageSizeArray)){
			try{
				String[] array = pageSizeArray.split(",");
				int[] pageSize = new int[array.length];
				for( int i=0;i<array.length;i++ )
					pageSize[i] = Integer.valueOf(array[i]);
				pagination.setPageSizeArray( pageSize );
			}catch( Exception e ){
				LOG.warn("分页标签参数设置错误：",e);
			}
		}
		
		pagination.setPageParamPrefix(pageParamPrefix);
		
	}

	public void setPageParamPrefix(String pageParamPrefix) {
		this.pageParamPrefix = pageParamPrefix;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public void setPageSizeArray(String pageSizeArray) {
		this.pageSizeArray = pageSizeArray;
	}

}
