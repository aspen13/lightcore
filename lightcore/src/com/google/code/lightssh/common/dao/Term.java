package com.google.code.lightssh.common.dao;

import java.io.Serializable;

/**
 * 条件
 * @author YangXiaojin
 *
 */
public class Term implements Serializable{
	
	private static final long serialVersionUID = -9214524979458816198L;

	/**
	 * 条件类型 
	 */
	public enum Type{
		EQUAL("等于"," = ")
		,NOT_EQUAL("不等于"," != ")
		,LESS_THAN("小于"," < ")
		,LESS_THAN_EQUAL("小于等于"," <= ")
		,GREATE_THAN("大于"," > ")
		,GREATE_THAN_EQUAL("大于等于"," >= ")
		,NULL("为空"," is null ")
		,NOT_NULL("不为空"," is not null ")
		,IN("包含"," in ")
		,NOT_IN("不包含"," not in ")
		,LIKE("模糊匹配"," like ")
		,LIKE_LEFT("模糊匹配"," like ")
		,LIKE_RIGHT("模糊匹配"," like ")
		;
		
		private String value;
		
		private String symbol;
		
		Type( String value,String symbol ){
			this.value = value;
			this.symbol = symbol;
		}

		public String getValue() {
			return value;
		}
		
		public String getSymbol() {
			return symbol;
		}

		public String toString(){
			return this.value;
		}
	}

	/**
	 * 类型
	 */
	private Type type;
	
	/**
	 * 查询属性
	 */
	private String key;
	
	/**
	 * 查询值
	 */
	private Object value;

	public Term(Type type, String key, Object value) {
		super();
		this.type = type;
		this.key = key;
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Term other = (Term) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
