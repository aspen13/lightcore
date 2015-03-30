package com.google.code.lightssh.common.model.page;

import java.util.ArrayList;
import java.util.List;

/**
 * list page
 * @author YangXiaojin
 */
public class ListPage<T> extends AbstractPage{	
	
	private static final long serialVersionUID = 1884414910547087386L;

	/** 每页默认List大小 */
	public static final int DEFAULT_LIST_SIZE = 15;
	
	/** 
	 * 页内容
	 */
	private List<T> list = new ArrayList<T>();
		
	/**
	 * 排序
	 */
	private List<OrderBy> orderByList;
	
	/**
	 * 页面自定义排序
	 */
	private OrderBy orderBy;
	
	/**
	 * 聚合列表
	 */
	private List<Aggregation> aggregationList;
	
	/**
	 * 是否允许超出页数。为true，查询页大于总页，返回空记录
	 */
	private Boolean overNumber;
				
	public ListPage() {
		super( DEFAULT_LIST_SIZE );
	}
		
	public ListPage(List<T> list,int allSize, int size, int number) {
		super(allSize, size, number);
		this.list = list;
	}

	public ListPage(int allSize, int size, int number) {
		this(null,allSize, size, number);
	}

	public ListPage(int size, int number) {
		this(0,size, number);
	}
	
	public ListPage(int size ) {
		this(0,size, 1);
	}
	
	/**
	 * 取第一个列表数据
	 */
	public T getFirst(){
		if( list == null || list.isEmpty() )
			return null;
		
		return this.list.get(0);
	}
	
	/**
	 * 取最后一个列表数据
	 */
	public T getLast(){
		if( list == null || list.isEmpty() )
			return null;
		
		return list.get(list.size()-1);
	}
	
	/**
	 * 增加排序
	 */
	protected void addOrder( String property , OrderType type ){
		if( property == null || "".equals(property))
			return;
		
		if( orderByList == null )
			orderByList = new ArrayList<OrderBy>();
		
		orderByList.add( new OrderBy(property,type) );
	}
	
	/**
	 * 添加升序
	 */
	public void addAscending( String property ){
		addOrder( property ,OrderType.ASCENDING );
	}
	
	/**
	 * 添加降序
	 */
	public void addDescending( String property ){
		addOrder( property ,OrderType.DESCENDING );
	}
	
	/**
	 * 所有排序属性
	 */
	public List<OrderBy> listAllOrderBy() {
		List<OrderBy> results = new ArrayList<OrderBy>();
		if( orderBy != null )
			results.add(orderBy);
		
		if( orderByList != null )
			results.addAll( this.orderByList );
		return results;
	}

	public int getDefaultSize() {
		return DEFAULT_LIST_SIZE;
	}
	
	/**
	 * 添加聚合
	 */
	protected void addAggregate(Aggregation.AggregateFunction fun,String property ){
		if( this.aggregationList == null )
			this.aggregationList = new ArrayList<Aggregation>();
		
		Aggregation agg = new Aggregation( fun,property );
		this.aggregationList.add(agg);
	}
	
	/**
	 * 添加SUM聚合
	 */
	public void addAggregateSum( String property ){
		addAggregate(Aggregation.AggregateFunction.SUM,property);
	}
	
	/**
	 * 添加AVG聚合
	 */
	public void addAggregateAvg( String property ){
		addAggregate(Aggregation.AggregateFunction.AVG,property);
	}
	
	/**
	 * 添加MAX聚合
	 */
	public void addAggregateMax( String property ){
		addAggregate(Aggregation.AggregateFunction.MAX,property);
	}
	
	/**
	 * 添加MIN聚合
	 */
	public void addAggregateMin( String property ){
		addAggregate(Aggregation.AggregateFunction.MIN,property);
	}
	
	/**
	 * 赋值聚合值
	 */
	public void assignAggregateValue( Object[] values ){
		if( values == null || this.aggregationList == null )
			return;
		
		for( int i=0;i<values.length&&i<aggregationList.size();i++){
			aggregationList.get(i).setValue(values[i]);
		}
	}
	
	/**
	 * 取聚合值
	 */
	protected Object getAggregateValue(
			Aggregation.AggregateFunction fun, String property ){
		if( property == null )
			return null;
		
		for( Aggregation item:aggregationList )
			if( item != null && property.equals(item.getProperty()) && item.getFun().equals(fun) )
				return item.getValue();
		
		return null;
	}
	
	/**
	 * 取SUM聚合值
	 */
	public Object getSumValue( String property ){
		return this.getAggregateValue(Aggregation.AggregateFunction.SUM, property);
	}
	
	/**
	 * 取AVG聚合值
	 */
	public Object getAvgValue( String property ){
		return this.getAggregateValue(Aggregation.AggregateFunction.AVG, property);
	}
	
	/**
	 * 取MAX聚合值
	 */
	public Object getMaxValue( String property ){
		return this.getAggregateValue(Aggregation.AggregateFunction.MAX, property);
	}
	
	/**
	 * 取MIN聚合值
	 */
	public Object getMinValue( String property ){
		return this.getAggregateValue(Aggregation.AggregateFunction.MIN, property);
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
	public List<OrderBy> getOrderByList() {
		return orderByList;
	}
	
	public OrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public List<Aggregation> getAggregationList() {
		return aggregationList;
	}

	public Boolean getOverNumber() {
		return overNumber;
	}

	public void setOverNumber(Boolean overNumber) {
		this.overNumber = overNumber;
	}

	/** 
	 * 排序类型
	 */
	public enum OrderType{
		ASCENDING
		,DESCENDING
	}
	
}
