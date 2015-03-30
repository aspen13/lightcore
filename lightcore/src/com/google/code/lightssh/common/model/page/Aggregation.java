package com.google.code.lightssh.common.model.page;

import java.io.Serializable;

/**
 * 聚合
 * @author YangXiaojin
 *
 */
public class Aggregation implements Serializable{
	
	private static final long serialVersionUID = -8075450365860368203L;

	/**
	 * 聚合函数
	 */
	public enum AggregateFunction{
		AVG,SUM,MIN,MAX;
	}
	
	/**
	 * 聚合类型
	 */
	private AggregateFunction fun;
	
	/**
	 * 属性名称
	 */
	private String property;
	
	/**
	 * 聚合值
	 */
	private Object value;
	
	public Aggregation(){
		
	}

	public Aggregation(AggregateFunction fun, String property) {
		super();
		this.fun = fun;
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public AggregateFunction getFun() {
		return fun;
	}

	public void setFun(AggregateFunction fun) {
		this.fun = fun;
	}

}
