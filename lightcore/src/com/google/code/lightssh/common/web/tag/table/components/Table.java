package com.google.code.lightssh.common.web.tag.table.components;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.IteratorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.model.page.ListPage;
import com.google.code.lightssh.common.model.page.Pagination;
import com.google.code.lightssh.common.util.StringUtil;
import com.google.code.lightssh.common.web.tag.table.model.IColumn;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * struts2 Table 组件
 * @author YangXiaojin
 *
 */

@StrutsTag(name = "table", tldTagClass = "com.google.code.lightssh.common.web.tag.table.views.TableTag", description = "Renders a html table")
public class Table extends AbstractURLBean{
	
	private static final Logger LOG = LoggerFactory.getLogger(Table.class );
	
	/** 分页属性参数前缀*/
	private static final String DEFAULT_PAGE_PARAM_PREFIX = "page";
	/** 分页排序属性参数后缀*/
	private static final String PAGE_ORDERBY_PROPERTY_SUFFIX = ".orderBy.property";
	/** 分页排序类型参数后缀*/
	private static final String PAGE_ORDERBY_TYPE_SUFFIX = ".orderBy.type";
	
	private static final String TABLE_HEADER = "lightssh-table-header";
	private static final String TABLE_FOOTER = "lightssh-table-footer";
	private static final String TABLE_ROW_START = "lightssh-table-row-start";
	private static final String TABLE_ROW_END = "lightssh-table-row-end";
	
	protected String statusAttr;
	protected Object oldStatus;
	protected IteratorStatus status;
	protected IteratorStatus.StatusState statusState;
	
	/**
	 * 迭代值
	 */
	@SuppressWarnings("rawtypes")
	protected Iterator iterator;
	
	/**
	 * 表格列
	 */
	protected ArrayList<Column> columns = new ArrayList<Column>();
	
	/**
	 * 是否可分页
	 */
	protected boolean pageable;
	
	/**
	 * 是否动态表格
	 */
	protected boolean dynamic;
	
	/**
	 * 动态列
	 */
	protected List<IColumn> dynamicCols;
	
	/**
	 * 分页参数前缀
	 */
	protected String pageParamPrefix;
	
	/**
	 * 行数
	 */
	private int rowNum = -1;
	
	/**
	 * 单元格数
	 */
	private int cellNum = 0;
	
	public Table(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected String getDefaultTemplate() {
		return null;
	}
	
    public void addColumn(Column header) {
        this.columns.add(header);
    }
	
	protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        
        parameters.put("columns", columns);
        parameters.put("value", this.value );
        parameters.put("orderByPropertyValue",
        		request.getParameter(getPageOrderByPropertyParamName()));
        
        String orderByTypeValue = request.getParameter(getPageOrderByTypeParamName());
        parameters.put("orderByTypeValue",orderByTypeValue);
        
        boolean asc = ListPage.OrderType.ASCENDING.name().equals(orderByTypeValue);
        parameters.put("toggleOrderByTypeValue",asc?
        	ListPage.OrderType.DESCENDING.name():ListPage.OrderType.ASCENDING.name());
        
        parameters.put("dynamic", this.dynamic );
        parameters.put("dynamicCols", this.dynamicCols );
    }
	
	/**
	 * 列排序
	 */
	protected void sortColumn( ){
		if( !dynamic || this.columns == null || columns.isEmpty() )
			return;
		
		//根据动态列排序
		if( dynamicCols != null && !dynamicCols.isEmpty() ){
			Collections.sort(dynamicCols, new Comparator<IColumn>(){
				public int compare(IColumn c1, IColumn c2){
					String s1 = c1.getColSequence()==null?"":c1.getColSequence();
					String s2 = c2.getColSequence()==null?"":c2.getColSequence();
					
					return s1.compareTo( s2 );
				}
			});
			
			Map<String,Column> map = new HashMap<String,Column>();
			for(Column item:this.columns){
				map.put(item.getColumnIdentity(), item);
			}
			
			ArrayList<Column> sortedCols = new ArrayList<Column>();
			for( IColumn item:dynamicCols){
				Column col = map.get(item.getColIdentity());
				if( col != null )
					sortedCols.add( col );
			}
			
			if( !sortedCols.isEmpty() ){
				this.columns.removeAll( sortedCols );
				columns.addAll(0, sortedCols);
			}
		}
		
		adujstColumn();
	}
	
	/**
	 * 整理列，根据列sequence属性调整首列或尾列
	 */
	protected void adujstColumn( ){
		int first=-1,last=-1;
		Column temp = null;
		//调整顺序
		for(int i=0;i<columns.size();i++ ){
			if( "first".equalsIgnoreCase(columns.get(i).getSequence()) ){
				first = i;
				break;
			}
		}
		if( first > 0 ){
			temp = columns.get(first);
			columns.remove(first);
			columns.add(0, temp );
		}
		
		for(int i=0;i<columns.size();i++ ){
			if("last".equalsIgnoreCase(columns.get(i).getSequence()) ){
				last = i;
				//break;
			}
		}
		if( last < columns.size()-1 && last >=0 ){
			temp = columns.get(last);
			columns.remove(last);
			
			columns.add(columns.size(), temp );
		}
	}
    
    /**
     * 排序URL
     */
    public String getPageOrderByQueryParams( ){
    	return queryParams( new String[]{getPageOrderByPropertyParamName()
    			,this.getPageOrderByTypeParamName()} );
    }
    
    /**
     * 初始化迭代器
     */
    @SuppressWarnings("rawtypes")
	private boolean initializeIterator() {
        // Create an iterator status if the status attribute was set.
        if(statusAttr != null) {
            statusState = new IteratorStatus.StatusState();
            status = new IteratorStatus(statusState);
        }
    	
        ValueStack stack = getStack();

        if(value == null) {
            value = "top";
        }

        Object valObject = findValue(value);
        if( valObject instanceof Pagination ){
        	valObject = ((ListPage)valObject).getList();
        }else{
        	this.pageable = false;
        }
        iterator = MakeIterator.convert( valObject );
        if( iterator == null || !iterator.hasNext()) 
        	return false;

        Object currentValue = iterator.next();
        stack.push(currentValue);

        String id = getId();
        if((id != null) && (currentValue != null)) {
            stack.getContext().put(id, currentValue);
        }

        // Status object
        if(statusAttr != null) {
            statusState.setLast(!iterator.hasNext());
            oldStatus = stack.getContext().get(statusAttr);
            stack.getContext().put(statusAttr, status);
        }
        
        return true;
    }
    
    /**
     * 下一个迭代值
     * @return
     */
    private boolean nextIteratorValue() {
        ValueStack stack = getStack();

        if(iterator != null) {
            stack.pop();
        }

        if((iterator != null) && iterator.hasNext()) {
            Object currentValue = iterator.next();

            stack.push(currentValue);
            String id = getId();

            if((id != null) && (currentValue != null)) {
                stack.getContext().put(id, currentValue);
            }

            // Update status
            if(status != null) {
                statusState.next(); // Increase counter
                statusState.setLast(!iterator.hasNext());
            }
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 清空迭代器
     */
    private void cleanupIterator() {
        if(status != null) {
            if(oldStatus == null) {
                stack.getContext().put(statusAttr, null);
            } else {
                stack.getContext().put(statusAttr, oldStatus);
            }
        }
    }

    public boolean start(Writer writer) {
    	boolean result = initializeIterator();
        if( result ) {
            super.start(writer);
            evaluateParams();
        } 
        /*else {
            super.end(writer, "");
        }*/
        return result;
    }
    
    /**
     * 动态列匹配为显示
     * @param colIdentity 动态列ID或属性名
     * @return
     */
    protected boolean matchDynamicColumn( String colIdentity ){
    	if( dynamicCols == null || dynamicCols.isEmpty() 
    			|| StringUtils.isEmpty(colIdentity))
    		return false;
    	
    	for(IColumn item:this.dynamicCols )
    		if( item.getColIdentity().equals(colIdentity) )
    			return true;
    	
    	return false;
    }
    
    /**
     * 是否显示动态列
     */
    public boolean isDisplay( Column col ){
    	if( !this.dynamic || col == null || !col.isDynamic() )
    		return true;
    	
    	return matchDynamicColumn(col.getColumnIdentity());
    }

	public boolean end(Writer writer, String body) {
        rowNum++;
        parameters.put("rowIndex", rowNum);
        
        if(rowNum == 0) {
        	sortColumn( ); //排序
        	
            try {
                mergeTemplate(writer, buildTemplateName(null, TABLE_HEADER));
                mergeTemplate(writer, buildTemplateName(null, TABLE_ROW_START));
            } catch(Exception e) {
                LOG.error("渲染表头异常：",e);
                e.printStackTrace();
                return false;
            }
            return true;
        }

        try {
            mergeTemplate(writer, buildTemplateName(null, TABLE_ROW_END));
        } catch(Exception e) {
            LOG.error("Could not open template", e);
            e.printStackTrace();
        }

        if(nextIteratorValue()) {
            try {
                mergeTemplate(writer, buildTemplateName(null, TABLE_ROW_START));
            } catch(Exception e) {
                LOG.error("Could not open template", e);
                e.printStackTrace();
            }
            return true;
        } else {
            cleanupIterator();
            try {
                super.end(writer, body, false);
                mergeTemplate(writer, buildTemplateName(null,TABLE_FOOTER));
            } catch(Exception e) {
                LOG.error("Could not open template", e);
                e.printStackTrace();
            }
            return false;
        }
        
    }
	
    /**
     * @return the headers
     */
    public List<Column> getColumns() {
        return columns;
    }

	public int getRowNum() {
		return rowNum;
	}
	
	public int getCellNum() {
		return cellNum;
	}

	public void setCellNum(int cellNum) {
		this.cellNum = cellNum;
	}
	
	public void incCellNum( ){
		this.cellNum ++;
	}

	public String getValue(){
		return value;
	}
	
	public void setValue( String value ){
		this.value =value;
	}

	public void setStatus(String statusAttr) {
		this.statusAttr = statusAttr;
	}

	@StrutsTagAttribute(description = "是否分页",type="Boolean")
	public void setPageable(boolean pageable) {
		this.pageable = pageable;
	}

	@StrutsTagAttribute(description = "是否动态表格",type="Boolean")
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	@StrutsTagAttribute(description = "所显示的动态表",type="Collection")
	public void setDynamicCols(List<IColumn> dynamicCols) {
		this.dynamicCols = dynamicCols;
	}

	@StrutsTagAttribute(description = "分页参数前缀")
	public void setPageParamPrefix(String pageParamPrefix) {
		this.pageParamPrefix = pageParamPrefix;
	}
	
	/**
	 * 分页排序属性参数名
	 */
	public String getPageOrderByPropertyParamName( ){
		return (StringUtil.hasText(pageParamPrefix)?pageParamPrefix.trim()
				:DEFAULT_PAGE_PARAM_PREFIX) + PAGE_ORDERBY_PROPERTY_SUFFIX;
	}
	
	/**
	 * 分页排序类型参数名
	 */
	public String getPageOrderByTypeParamName( ){
		return (StringUtil.hasText(pageParamPrefix)?pageParamPrefix.trim()
				:DEFAULT_PAGE_PARAM_PREFIX) + PAGE_ORDERBY_TYPE_SUFFIX;
	}
	
}
