package com.google.code.lightssh.common.web.tag.table.components;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.util.StringUtil;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * 表格列
 * @author YangXiaojin
 *
 */
@StrutsTag(name="column",tldTagClass = "com.google.code.lightssh.common.web.tag.table.views.ColumnTag", description = "Renders a cell in a table")
public class Column extends UIBean{
	
	private static final Logger LOG = LoggerFactory.getLogger(Column.class );
	
    public static final String COLUMN_OPEN_TEMPLATE = "lightssh-table-col-start";
    public static final String COLUMN_CLOSE_TEMPLATE = "lightssh-table-col-end";
    
    /**
     * 是否排序
     */
    protected boolean sortable;
	
    /**
     * 排序属性
     */
    protected String sortKey;
    
    /**
     * 列宽
     */
    protected String width;
    
    /**
     * 列样式
     */
    protected String colClass;
    
	/**
	 * 是否动态列
	 */
	protected boolean dynamic;
	
    /**
     * 显示顺序
     */
    protected String sequence;
    
	public Column(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}
	
	/**
	 * 列ID
	 */
	public String getColumnIdentity(){
		if( !StringUtils.isEmpty(id) )
			return id;
		else if( !StringUtils.isEmpty(value) )
			return value;
		
		return null;
	}
	
	protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        
        parameters.put("sortKey", sortKey);
        parameters.put("width", width);
        parameters.put("sortable", sortable);
        parameters.put("colClass", colClass);
        parameters.put("value", this.value );
        parameters.put("dynamic", this.dynamic );
        
    }
	
    @Override
    public boolean start(Writer writer) {
        boolean evaluateBody = false;
        Table table = (Table) this.findAncestor(Table.class);
        
        //是否显示该列
        if(!table.isDisplay(this))
        	return false;
        
        if(table.getRowNum() < 0 ) {
            table.addColumn(this);
        }
        evaluateParams();
        try {
            if(table.getRowNum() >= 0) {
                mergeTemplate(writer, buildTemplateName(null,COLUMN_OPEN_TEMPLATE));
                evaluateBody = true;
            }
        } catch(Exception e) {
            LOG.error("不能找到模板文件", e);
            e.printStackTrace();
        }
        return evaluateBody;
    }

    @Override
    public boolean end(Writer writer, String body) {
        Table table = (Table) this.findAncestor(Table.class);
        
        //是否显示该列
        if(!table.isDisplay(this))
        	return false;
        
        evaluateParams();
        try {
            if(table.getRowNum() >= 0) {
            	table.incCellNum();
                mergeTemplate(writer, buildTemplateName(null,COLUMN_CLOSE_TEMPLATE));
            }
        } catch(Exception e) {
            LOG.error("不能找到模板文件", e);
        } finally {
            popComponentStack();
        }

        return false;
    }
    
    /**
     * 排序
     */
    public Column getSortedSubstitute( ){
    	Table table = (Table) this.findAncestor(Table.class);
    	
    	if( table.getColumns() == null || table.getColumns().isEmpty() )
    		return null;
    	
    	int len = table.getColumns().size();
    	return table.getColumns().get( table.getCellNum() % len );
    }

	@Override
	protected String getDefaultTemplate() {
		return null;
	}
	
	/**
	 * 获取可能的排序属性
	 * @return
	 */
	public String getPossibleSortKey(){
		if( StringUtil.hasText(sortKey) )
			return StringUtil.clean(sortKey);
		
		return StringUtil.clean(value);
	}

    @StrutsTagAttribute(description = "sortable",type="Boolean", defaultValue="false")
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    @StrutsTagAttribute(description = "sortKey",type="String")
    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
    
	@StrutsTagAttribute(description = "是否动态表格",type="Boolean")
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

    public boolean isSortable() {
        return sortable;
    }

    public String getSortKey() {
        return sortKey;
    }
    
    public String getTitle(){
    	return title;
    }

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}
	
	public String getCssClass( ){
		return this.cssClass;
	}
	
	public String getCssStyle( ){
		return this.cssStyle;
	}

	public String getColClass() {
		return colClass;
	}

	public void setColClass(String colClass) {
		this.colClass = colClass;
	}
	
	public String getValue(){
		return this.value;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

}
