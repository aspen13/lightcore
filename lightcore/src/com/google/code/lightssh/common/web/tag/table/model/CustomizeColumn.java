package com.google.code.lightssh.common.web.tag.table.model;

/**
 * 
 * @author Aspen
 * @date 2013-10-30
 * 
 */
public class CustomizeColumn implements IColumn{
	
	/**
	 * 列标识
	 */
	private String colIdentity;
	
	/**
	 * 列顺序
	 */
	private String colSequence;

	public CustomizeColumn() {
		super();
	}

	public CustomizeColumn(String colIdentity, String colSequence) {
		super();
		this.colIdentity = colIdentity;
		this.colSequence = colSequence;
	}

	public String getColIdentity() {
		return colIdentity;
	}

	public void setColIdentity(String colIdentity) {
		this.colIdentity = colIdentity;
	}

	public String getColSequence() {
		return colSequence;
	}

	public void setColSequence(String colSequence) {
		this.colSequence = colSequence;
	}

}
