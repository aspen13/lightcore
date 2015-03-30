package com.google.code.lightssh.common.web.tag.table.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.google.code.lightssh.common.util.StringUtil;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * 分页组件
 * @author YangXiaojin
 *
 */
@StrutsTag(name = "pagination", tldTagClass = "com.google.code.lightssh.common.web.tag.table.views.PaginationTag", description = "分页标签")
public class Pagination extends AbstractURLBean{
	
	private static final String PAGINATION = "lightssh-pagination";
	/** 分页属性参数前缀*/
	private static final String DEFAULT_PAGE_PARAM_PREFIX = "page";
	/** 默认显示页面范围*/
	private static final int DEFAULT_PAGE_LENGTH = 8;
	/** 默认可变动每页显示条数*/
	private static final int[] DEFAULT_PAGE_SIZE_ARRAY = {1,5,10,15,20,50,100};
	
	/**
	 * 分页参数前缀
	 */
	protected String pageParamPrefix;
	
	/**
	 * 页码显示长度
	 */
	protected int length;
	
	/**
	 * 参数顺序
	 */
	protected String[] paramsOrder;
	
	/**
	 * 可设置的每页大小
	 */
	protected int[] pageSizeArray;
	
	public Pagination(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}
	
	protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        
        parameters.put("value", this.value );
        parameters.put("pageNumberQueryParams", pageNumberQueryParams() );
        parameters.put("pageSizeQueryParams", pageSizeQueryParams() );
    }
    
    protected String pageNumberQueryParams( ){
    	return queryParams( this.getPageNumberParamName() );
    }
    
    protected String pageSizeQueryParams( ){
    	return queryParams( this.getPageSizeParamName() );
    }

	@Override
	protected String getDefaultTemplate() {
		return PAGINATION;
	}
	
	@StrutsTagAttribute(description = "分页参数前缀")
	public void setPageParamPrefix(String pageParamPrefix) {
		this.pageParamPrefix = pageParamPrefix;
	}

	@StrutsTagAttribute(description = "分页码显示长度")
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * 分页大小参数名
	 */
	public String getPageSizeParamName( ){
		return (StringUtil.hasText(pageParamPrefix)?pageParamPrefix.trim()
				:DEFAULT_PAGE_PARAM_PREFIX) +".size";
	}

	/**
	 * 分页当前页参数名
	 */
	public String getPageNumberParamName( ){
		return (StringUtil.hasText(pageParamPrefix)?pageParamPrefix.trim()
				:DEFAULT_PAGE_PARAM_PREFIX) +".number";
	}
	
	/**
	 * 页码显示长度
	 */
	public int getPageLength( ){
		return this.length==0?DEFAULT_PAGE_LENGTH:this.length;
	}
	
	/**
	 * 页码显示左侧长度
	 */
	public int getPageLeftLength( ){
		return getPageLength()/2;
	}
	
	/**
	 * 页码显示右侧长度
	 */
	public int getPageRightLength( ){
		return getPageLength()-getPageLeftLength()-1;
	}

	public int[] getPageSizeArray() {
		return pageSizeArray==null||pageSizeArray.length==0
			?DEFAULT_PAGE_SIZE_ARRAY:pageSizeArray;
	}

	public void setPageSizeArray(int[] pageSizeArray) {
		this.pageSizeArray = pageSizeArray;
	}
	
}
