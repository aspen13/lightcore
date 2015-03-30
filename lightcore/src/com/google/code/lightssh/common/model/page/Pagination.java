package com.google.code.lightssh.common.model.page;

/**
 * pagination
 * @author YangXiaojin
 */
public interface Pagination {
			
	/**
	 * all elements size
	 */
	public int getAllSize( );
	
	/**
	 * elements size per page
	 */
	public int getSize( );
	
	/**
	 * all page size 
	 */
	public int getAllPage( );
	
	/**
	 * current page number
	 */
	public int getNumber( );
	
	/**
	 * is first page
	 */
	public boolean isFirst( );
	
	/**
	 * is last page
	 */
	public boolean isLast( );
	
	/**
	 * next page number
	 */
	public int getNextNumber( );
	
	/**
	 * previous page number
	 */
	public int getPreviousNumber( );

}
