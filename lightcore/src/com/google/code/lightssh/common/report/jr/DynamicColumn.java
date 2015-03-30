package com.google.code.lightssh.common.report.jr;

/**
 * dynamic column
 * @author YangXiaojin
 *
 */
public class DynamicColumn {
	
	/**
	 * 域名
	 */
	private String field;
	
	/**
	 * 类型
	 */
	@SuppressWarnings("rawtypes")
	private Class clazz;
	
	/**
	 * 标题
	 */
	private String caption;
	
	/**
	 * X坐标
	 */
	private int x;
	
	/**
	 * Y坐标
	 */
	private int y;
	
	/**
	 * 宽度
	 */
	private int width;
	
	/**
	 * 高度
	 */
	private int height;
	
	/**
	 * 是否隐藏
	 */
	private boolean hidden;
	
	//-- constructors ----------------------------------------------------------
		
	public DynamicColumn() {
		super();
	}
	
	public DynamicColumn(String field, String caption) {
		super();
		this.field = field;
		this.caption = caption;
	}

	@SuppressWarnings("rawtypes")
	public DynamicColumn(String field, Class clazz, int width, int height) {
		super();
		this.field = field;
		this.clazz = clazz;
		this.width = width;
		this.height = height;
	}
	
	@SuppressWarnings("rawtypes")
	public DynamicColumn(String field, String caption, Class clazz, int width, int height) {
		super();
		this.field = field;
		this.caption = caption;
		this.clazz = clazz;
		this.width = width;
		this.height = height;
	}

	//-- getters and setters ---------------------------------------------------
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@SuppressWarnings("rawtypes")
	public Class getClazz() {
		return clazz;
	}

	@SuppressWarnings("rawtypes")
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}			
		
}
