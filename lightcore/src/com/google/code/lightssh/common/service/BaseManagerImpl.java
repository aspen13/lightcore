package com.google.code.lightssh.common.service;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.code.lightssh.common.ApplicationException;
import com.google.code.lightssh.common.dao.Dao;
import com.google.code.lightssh.common.dao.SearchCondition;
import com.google.code.lightssh.common.dao.Term;
import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.page.ListPage;
import com.google.code.lightssh.common.util.StringUtil;

/**
 * base manager implement 
 * @author YangXiaojin
 *
 */
public class BaseManagerImpl<T extends Persistence<?>> implements BaseManager<T>{
	
	private static final long serialVersionUID = 3769349392225411691L;
	
	protected Dao<T> dao;
		
	public BaseManagerImpl( ){
		super();
	}
	
	public void setDao(Dao<T> dao) {
		this.dao = dao;
	}

	public BaseManagerImpl( Dao<T> dao ){
		this.dao = dao;
	}

	public T get(T t) {
		return dao.read(t);
	}
	
	public T get(Serializable identity) {
		return dao.read(identity);
	}
	
	public T getWithLock(Serializable identity) {
		return dao.readWithLock(identity);
	}
	
	public T getWithLock(T t) {
		if( t== null || t.getIdentity() == null )
			return null;
		
		return dao.readWithLock(t.getIdentity());
	}
	
	public ListPage<T> list(ListPage<T> page) {
		return dao.list(page);
	}
	
	public ListPage<T> list(ListPage<T> page,T t ) {
		return dao.list(page,t);
	}

	public void remove(T t) {
		dao.delete(t);		
	}
	
	public void remove(Serializable identity) {
		dao.delete(identity);
	}

	public void remove(Collection<T> entities) {
		dao.delete(entities);
	}

	public void save(T t) {
		if( t.isInsert() ){
			dao.create(t);
		}else			
			dao.update(t);		
	}
	
	public void save( Collection<T> entities ){
		for( T t:entities )
			save(t);
	}

	public void create(T t) {
		dao.create(t);
	}

	public void create(Collection<T> entities) {
		dao.create(entities);
	}

	public void update(T t) {
		dao.update(t);
	}

	public void update(Collection<T> entities) {
		dao.update(entities);
	}
	
	/**
	 * 唯一属性名
	 */
	protected String[] getUniqueProperty( T t) {
		Field[] fields = t.getClass().getDeclaredFields();
		if( fields == null )
			return null;
		
		List<String> properties = new ArrayList<String>();
		for( Field field:fields ){
			Annotation[] anns = field.getAnnotations();
			if( anns == null ) continue;
			for( Annotation ann:anns ){
				if( ann instanceof javax.persistence.Column
						&& ((javax.persistence.Column)ann).unique()){
					properties.add( field.getName() );
				}
			}//inner for
		}
		
		return properties.isEmpty()?null:properties.toArray(new String[]{});
	}
	
	public boolean isUniqueProperty(T t){
		String[] properties = getUniqueProperty(t);
		if( properties == null )
			throw new ApplicationException("未通过JPA Annotation定义实体("
					+t.getClass().getName()+")唯一属性！");
		
		for( String item:properties)
			if( !isUniqueProperty(item, t) ) 
				return false;
		
		return true;
	}
	
	@Override
	public boolean isUniqueProperty(String property,T t){
		String fieldName = StringUtil.clean(property);
		if( t == null )
			return true;
		
		ListPage<T> page = new ListPage<T>(1);
		List<String> properties = new ArrayList<String>(1);
		properties.add( fieldName );
		page = dao.list(page,t,properties);
		
		T exists = (page.getList()==null||page.getList().isEmpty())
			?null:page.getList().get(0);
		
		return exists == null || exists.getIdentity().equals( t.getIdentity() );
	}

	@Override
	public ListPage<T> list(ListPage<T> page, Collection<Term> terms) {
		return dao.list(page, terms);
	}

	@Override
	public ListPage<T> list(ListPage<T> page, SearchCondition sc) {
		return dao.list(page,sc);
	}

}
