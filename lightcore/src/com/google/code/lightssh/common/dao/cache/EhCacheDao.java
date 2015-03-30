package com.google.code.lightssh.common.dao.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.google.code.lightssh.common.dao.Dao;
import com.google.code.lightssh.common.dao.DaoException;
import com.google.code.lightssh.common.dao.SearchCondition;
import com.google.code.lightssh.common.dao.Term;
import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.page.ListPage;

/**
 * EhCache DAO
 * @author YangXiaojin
 *
 */
public class EhCacheDao <T extends Persistence<?>> implements Dao<T>{
	
	/**
	 * cache
	 */
	private Cache cache;
	
	public void setCache( Cache cache ){
		this.cache = cache;
	}

	@Override
	public void create(T t) {
		if( t == null )
			return;
		
		cache.put( new Element( t.getIdentity(), t ) );
	}

	@Override
	public void create(Collection<T> entities) {
		if( entities == null || entities.size() < 0 )
			return;
		
		for( T t:entities )
			create( t );
	}

	@Override
	public void delete(T t) {
		if( t == null || t.getIdentity() == null )
			return;
		
		delete( t.getIdentity() );
	}

	@Override
	public void delete(Serializable identity) {
		cache.remove( identity );
	}

	@Override
	public void delete(Collection<T> entities) {
		if( entities == null || entities.size() < 0 )
			return;
		
		for( T t:entities )
			delete( t );
	}

	@Override
	public ListPage<T> list(ListPage<T> page) {
		throw new CacheException( "不支持此方法！" );
	}

	@Override
	public ListPage<T> list(ListPage<T> page, T t) {
		throw new CacheException( "不支持此方法！" );
	}
	
	@Override
	public ListPage<T> list(ListPage<T> page, T t,Collection<String> properties) {
		throw new CacheException( "不支持此方法！" );
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<T> listAll() {
		List keys = cache.getKeys();
		if( keys == null || keys.isEmpty() )
			return null;
		
		List<T> result = new ArrayList<T>( );
		for( Object key:keys ){
			if( key instanceof Serializable ){
				T t = read( (Serializable)key );
				if( t != null )
					result.add( t );
			}
		}
		
		return result;
	}

	@Override
	public T read(T t) {
		return read( t.getIdentity() );
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(Serializable identity) {
		Element element = cache.get( identity );
		if( element == null )
			return null;
		
		return (T)element.getValue();
	}
	
	@Override
	public T readWithLock(Serializable identity) {
		throw new DaoException("实体("+identity+")带锁的查询没有实现！");
	}

	@Override
	public void update(T t) {
		create( t );
	}

	@Override
	public void update(Collection<T> entities) {
		create( entities );
	}

	@Override
	public void deleteAll() {
		cache.removeAll();
	}

	@Override
	public ListPage<T> list(ListPage<T> page, String select,
			Collection<Term> terms) {
		throw new CacheException( "不支持此方法！" );
	}

	@Override
	public ListPage<T> list(ListPage<T> page, Collection<Term> terms) {
		throw new CacheException( "不支持此方法！" );
	}

	@Override
	public ListPage<T> list(ListPage<T> page, String select, SearchCondition sc) {
		throw new CacheException( "不支持此方法！" );
	}

	@Override
	public ListPage<T> list(ListPage<T> page, SearchCondition sc) {
		throw new CacheException( "不支持此方法！" );
	}

	@Override
	public int update(String idName,Object idValue
			,String property,Object originalValue,Object newValue ) {
		throw new CacheException( "不支持此方法！" );
	}

}
