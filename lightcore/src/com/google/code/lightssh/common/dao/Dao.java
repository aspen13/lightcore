package com.google.code.lightssh.common.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.page.ListPage;


/** 
 * data access object ,CURD
 * @author YangXiaojin
 */
public interface Dao<T extends Persistence<?>> {
	
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
	 * update
	 */
	public int update(String idName,Object idValue
			,String property,Object originalValue,Object newValue );
	
	/**
	 * update all 
	 */
	public void update( Collection<T> entities );
	
	/**
	 * read
	 */
	public T read( T t);
	
	/**
	 * read
	 */
	public T read(Serializable identity);
	
	/**
	 * read with lock
	 */
	public T readWithLock(Serializable identity);
			
	/**
	 * delete
	 */
	public void delete( T t );
	
	/**
	 * delete
	 */
	public void delete( Serializable identity );
	
	/**
	 * delete all
	 */
	public void delete( Collection<T> entities );	
	
	/**
	 * delete all
	 */
	public void deleteAll( );	
	
	/**
	 * list all
	 * @return
	 */
	public List<T> listAll( );
	
	/**
	 * 分页查询
	 */
	public ListPage<T> list( ListPage<T> page );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list(ListPage<T> page,T t );
	
	/**
	 * 匹配指定属性的分页查询
	 */
	public ListPage<T> list(ListPage<T> page,T t,Collection<String> properties );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,String select,Collection<Term> terms );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,Collection<Term> terms );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,String select,SearchCondition sc );
	
	/**
	 * 带条件的分页查询
	 */
	public ListPage<T> list( ListPage<T> page,SearchCondition sc );
}
