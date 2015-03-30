package com.google.code.lightssh.common.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.page.ListPage;

/**
 * abstract DAO
 * 子类要继承才能得到实际参数类(T)
 * example1:
 * 	class FooDao extends FooDao<Foo>{
 * 		//properties...
 * 		//getters and setters ...
 * 	}
 * 
 * 	FooDao fooDao = new FooDao();
 * 	//fooDao 对象中的entityClass属性已取得值
 * 
 * 	example2:
 * 	class JPADao<T extends Persistence<?>> extends AbstractDao<T>{
 * 		//...
 * 	}
 * 
 * 	JPADao<Foo> jpaDao = new JPADao<Foo>();
 *	//jpaDao 对象中的entityClass属性 为空，特别注意
 *	
 *	example3:
 *	class JPADao<T extends Persistence<?>> extends AbstractDao<T>{
 *		public JPADao(){}
 *
 *		public JPADao( Class<T> t ){ 
 *			super(t);
 *		}
 *	}
 *
 *	//针对上述得不到实际参数类情况，构造对象时传递参数进去
 *	JPADao<Foo> jpaDao = new JPADao<Foo>( Foo.class );
 *
 */
public abstract class AbstractDao<T extends Persistence<?>> implements Dao<T>{
	
	/**
	 * entity class
	 */
    protected Class<T> entityClass;
    
	public AbstractDao( Class<T> t ) {
		this.entityClass = t;		
	}
	
	@SuppressWarnings("unchecked")
	protected AbstractDao() {
		Type type =((ParameterizedType)
                getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if( type instanceof Class){
			this.entityClass = (Class<T>) type;
		}
	}
	
	private void throwExp( ){
		throw new DaoException("Should be implemented in the sub-class!");
	}
	
	public void create(T t) {
		throwExp( );
	}

	public void create(Collection<T> entities) {
		throwExp( );
	}

	public void delete(T t) {
		throwExp( );	
	}

	public void delete(Serializable identity) {
		throwExp( );	
	}

	public void delete(Collection<T> entities) {
		throwExp( );	
	}

	public ListPage<T> list(ListPage<T> page) {
		throwExp( );
		return null;
	}

	public List<T> listAll() {
		throwExp( );
		return null;
	}

	public T read(T t) {
		throwExp( );
		return null;
	}

	public T read(Serializable identity) {
		throwExp( );
		return null;
	}

	public void update(T t) {
		throwExp( );
	}

	public void update(Collection<T> entities) {
		throwExp( );	
	}

}
