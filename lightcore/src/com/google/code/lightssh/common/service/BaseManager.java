package com.google.code.lightssh.common.service;

import java.io.Serializable;
import java.util.Collection;

import com.google.code.lightssh.common.dao.SearchCondition;
import com.google.code.lightssh.common.dao.Term;
import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.page.ListPage;

/**
 * base manager 
 * @author YangXiaojin
 */
public interface BaseManager<T extends Persistence<?>> extends Manager{
	
	/**
	 * read
	 */
	public T get( T t );
	
	/**
	 * read
	 */
	public T get( Serializable identity );
	
	/**
	 * read with lock
	 */
	public T getWithLock( Serializable identity );
	
	/**
	 * read with lock
	 */
	public T getWithLock( T t );
	
	/**
	 * create
	 */
	public void create( T t );
	
	/**
	 * create all
	 */
	public void create( Collection<T> entities );
	
	/**
	 * update
	 */
	public void update( T t );
	
	/**
	 * update all 
	 */
	public void update( Collection<T> entities );
	
	/**
	 * create or update 
	 */
	public void save( T t );
	
	/**
	 * create or update all
	 */
	public void save( Collection<T> entities );
	
	/**
	 * delete
	 */
	public void remove( T t );
	
	/**
	 * delete all
	 */
	public void remove( Collection<T> entities );
	
	/**
	 * delete
	 */
	public void remove( Serializable identity );
	
	/**
	 * 分页查询
	 */
	public ListPage<T> list( ListPage<T> page );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list(ListPage<T> page,T t );
	
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,Collection<Term> terms );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,SearchCondition sc );
	
	/**
	 * 检查属性是否唯一
	 * 属性通过 JPA 注解定义
	 */
	public boolean isUniqueProperty(T t);
	
	/**
	 * 检查属性是否唯一
	 * @param property 属性名
	 * @return 属性唯一返回true
	 */
	public boolean isUniqueProperty(String property,T t);

}
