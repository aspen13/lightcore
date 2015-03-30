package com.google.code.lightssh.common.report.jr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * jasper report elements and parameters
 * 
 * @author yangxiaojin
 */
public class ReportParameter {
	/**
	 * title
	 */
	private String title;
	
	/**
	 * summary
	 */
	private String summary;
	
	/**
	 * hidden column title
	 */
	private boolean hiddenColumnTitle;
	
	/**
	 * fileds
	 */
	private List<DynamicColumn> dynamicColumns;

	//	-- getters and setters ---------------------------------------------------
	
	public List<DynamicColumn> getDynamicColumns() {
		return dynamicColumns;
	}

	public void setDynamicColumns(List<DynamicColumn> dynamicColumns) {
		this.dynamicColumns = dynamicColumns;
	}

	public boolean isHiddenColumnTitle() {
		return hiddenColumnTitle;
	}

	public void setHiddenColumnTitle(boolean hiddenColumnTitle) {
		this.hiddenColumnTitle = hiddenColumnTitle;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	//-- util methods ----------------------------------------------------------
	
	/**
	 * add one column
	 */
	public void addColumn( DynamicColumn column ){
		if( dynamicColumns == null )
			dynamicColumns = new ArrayList<DynamicColumn>( );
		dynamicColumns.add( column );
	}
	
	/**
	 * add some columns
	 * @param collection
	 */
	public void addAllColumn( Collection<DynamicColumn> collection ){
		if( dynamicColumns == null )
			dynamicColumns = new ArrayList<DynamicColumn>( );
		dynamicColumns.addAll( collection );
	}
	
}
