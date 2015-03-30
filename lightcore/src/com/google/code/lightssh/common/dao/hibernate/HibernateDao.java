package com.google.code.lightssh.common.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.code.lightssh.common.dao.Dao;
import com.google.code.lightssh.common.dao.DaoException;
import com.google.code.lightssh.common.dao.SearchCondition;
import com.google.code.lightssh.common.dao.Term;
import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.page.Aggregation;
import com.google.code.lightssh.common.model.page.ListPage;
import com.google.code.lightssh.common.model.page.OrderBy;
import com.google.code.lightssh.common.model.page.ListPage.OrderType;
import com.google.code.lightssh.common.util.ParserUtil;
import com.google.code.lightssh.common.util.ReflectionUtil;
import com.google.code.lightssh.common.util.StringUtil;

/**
 * hibernate dao implements
 * @author YangXiaojin
 */
public class HibernateDao<T extends Persistence<?>> extends HibernateDaoSupport implements Dao<T>{
	
	/**
	 * entity class
	 */
    protected Class<T> entityClass;
    
	public HibernateDao( Class<T> t ) {
		this.entityClass = t;		
	}
	
	@SuppressWarnings("unchecked")
	protected HibernateDao() {
		Type type = null;
		if( getClass().getGenericSuperclass() instanceof ParameterizedType)
			type =((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		if( type != null && type instanceof Class){
			this.entityClass = (Class<T>) type;
		}
	}

	@Override
	public void create(T t) {
		t.preInsert();
		getHibernateTemplate().save( t );	
	}

	@Override
	public void create(Collection<T> entities) {
		for( T t:entities )
			t.preInsert();
		getHibernateTemplate().saveOrUpdateAll(entities);
	}

	@Override
	public void delete(T t) {
		if( t == null )
			return;
		getHibernateTemplate().delete(t);
	}

	@Override
	public void delete(Serializable identity) {
		delete( this.read(identity) );
	}

	@Override
	public void delete(Collection<T> entities) {
		getHibernateTemplate().deleteAll(entities);
	}
	
	protected int rowCount() {
		String hql = " SELECT COUNT( m ) from " + entityClass.getName() + " AS m ";
		
		return rowCount( hql );
	}
	
	protected int rowCount( String hql ) {
		return rowCount( hql, null);
	}
	
	/**
	 * 总记录数
	 */
	protected int rowCount( Session session,String count_hql, Object[] params ) {
		Query query = session.createQuery( count_hql );
		addQueryParams( query,params );
		Object count = query.uniqueResult();
		return ParserUtil.parseInt( count );
	}
	
	/**
	 * 总记录数
	 */
	@SuppressWarnings({ "rawtypes" })
	protected int rowCount( String hql , Object[] params ) {
		Object count = getHibernateTemplate().find( hql ,params);
		
		return ( count != null && count instanceof List)?
			 ParserUtil.parseInt(((List)count).get(0)):0;
	}
	
	protected ListPage<T> query( ListPage<T> page , String from_jqpl){
		return query( page,from_jqpl, null );
	}
	
	
	/**
	 * order by 
	 */
	protected String addOrderBy( ListPage<?> page ){
		//add order
		StringBuffer orderby = new StringBuffer("");
		List<OrderBy> orderByList = page.listAllOrderBy();
		if( orderByList != null && !orderByList.isEmpty() ){
			for(OrderBy each:orderByList ){
				if( each == null )
					continue;
				orderby.append("".equals(orderby.toString())?" ORDER BY ":" ,");
				orderby.append( " m." + each.getProperty() 
					+ (OrderType.ASCENDING.equals( each.getType() )?" ASC ":" DESC ") );
			}
		}
		
		return orderby.toString();
	}
	
	/**
	 * add Query parameters
	 */
	protected Query addQueryParams( Query query ,Object[] params ){
		for( int i=0;params != null && i<params.length;i++ )
			query.setParameter(i, params[i] );
		
		return query;
	}
	
	protected ListPage<T> query( final ListPage<T> page , 
			final String from_jqpl , final Object[] params ){
		return query( page,null,from_jqpl,params );
	}
	
	@SuppressWarnings("unchecked")
	protected ListPage<T> query( final ListPage<T> page ,final String select_jpql, 
			final String from_jqpl , final Object[] params ){
		return (ListPage<T>)this.queryObject(page, select_jpql, from_jqpl, params);
	}
	
	/**
	 * 聚合查询
	 */
	@SuppressWarnings("unchecked")
	protected ListPage<?> queryAggregation(Session session
			,ListPage<?> page,String from_jqpl,Object[] params ){
		if( session == null || page == null || page.getAggregationList() == null 
				|| page.getAggregationList().isEmpty() )
			return page;
		
		StringBuffer agg_hql = new StringBuffer(" SELECT ");
		for( int i=0;i<page.getAggregationList().size();i++ ){
			Aggregation item = page.getAggregationList().get(i);
			if( i != 0 )
				agg_hql.append(",");
			agg_hql.append( item.getFun().name());
			agg_hql.append( " ( "+item.getProperty()+" ) ");
		}
		
		agg_hql.append( from_jqpl );
		Query query = session.createQuery( agg_hql.toString() );
		addQueryParams( query,params );
		
		List<Object> results = query.list();
		if( results != null && !results.isEmpty() ){
			if( results.get(0) instanceof Object[] )
				page.assignAggregateValue( (Object[])results.get(0) );
			else
				page.assignAggregateValue( new Object[]{results.get(0)} );
		}
		
		return page;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ListPage<?> queryObject( final ListPage<?> page ,final String select_jpql, 
			final String from_jqpl , final Object[] params ){
		
		return (ListPage<T>) getHibernateTemplate().execute(
			new HibernateCallback() {
	        	public Object doInHibernate(Session session) throws HibernateException {
	        		String count_hql = " SELECT COUNT(m) ";
	        		if(StringUtil.clean(select_jpql)!=null){
	        			String[] sels = select_jpql.trim().split("( )+");
	        			if( sels.length == 2 ){
	        				count_hql = sels[0] + " COUNT( " + sels[1] + " )";
	        			}else if( sels.length == 3 ){
	        				count_hql = sels[0] + " COUNT( " + sels[1] + " " + sels[2]+ " )";
	        			}
	        		}
	        		count_hql += from_jqpl;
	        		page.setAllSize( rowCount( session,count_hql,params ) ); //all size
	        			
	        		if( page.getNumber() > page.getAllPage() )
	        			page.setNumber(Math.min(page.getNumber(), page.getAllPage()));
	        		
	        		if( page.getAllSize() > 0 && page.getSize() > 0 ){
	        			//聚合查询
	        			queryAggregation(session,page,from_jqpl,params);
	        			
	        			String jpql = ((StringUtil.clean(select_jpql)==null)?" SELECT m ":select_jpql) + from_jqpl;
	        			jpql += addOrderBy( page );//add order
	        			
	        			Query query = session.createQuery( jpql )
	        				.setFirstResult(page.getStart()-1)
	        				.setMaxResults(page.getSize());
	        			
	        			addQueryParams( query,params );
	        			
	        			page.setList( query.list());
	        		}//end if
	        		
	        		return page;
        		}// end doInHibernate
        	}); 
	}
	
	@Override
	public ListPage<T> list(ListPage<T> page) {
		String jpql = " FROM " + entityClass.getName() + " AS m ";
		
		return query( page , jpql );
	}
	
	public ListPage<T> list(ListPage<T> page,T t ){
		if( t == null )
			return list( page );
		
		throw new DaoException("实体("+entityClass+")带条件的查询没有实现！");
	}

	@Override
	public List<T> listAll() {
		ListPage<T> page = new ListPage<T>(Integer.MAX_VALUE);
		page = list( page );
		
		return page.getList();
	}

	@Override
	public T read(T t) {
		return (T)getHibernateTemplate().get( this.entityClass , t.getIdentity() );
	}

	@Override
	public T read(Serializable identity) {
		return (T)getHibernateTemplate().get( this.entityClass , identity );
	}
	
	@Override
	public T readWithLock(Serializable identity) {
		throw new DaoException("实体("+entityClass+")带锁的查询没有实现！");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected T readBySql(String sql,Object[] params ){
		final String f_sql = sql;
		final Object[] f_params = params;
		return (T)getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException {
						Query query = session.createSQLQuery( f_sql ).addEntity( entityClass );
						
						if( f_params != null ){
							for( int i=0;i<f_params.length;i++)
								query.setParameter(i,f_params[i] );
						}
						
		        		List<T> results = query.list();
		        		return (results==null||results.isEmpty())?null:results.get(0);
		        	}// end doInHibernate
	        	}); 
	}

	@Override
	public void update(T t) {
		getHibernateTemplate().update(t);
	}

	@Override
	public void update(Collection<T> entities) {
		getHibernateTemplate().saveOrUpdateAll(entities);
	}
	
	@Override
	public int update(String idName,Object idValue
			,String property,Object originalValue,Object newValue ){
		if( StringUtil.clean(property) == null || 
				(originalValue ==null && newValue == null ) )
			return 0;
		
		List<Object> params = new ArrayList<Object>( );
		params.add( newValue );
		params.add( idValue );
		String field = property.trim();
		String hql = "UPDATE " + this.entityClass.getName() + " AS m SET m."
			+ field + " = ? WHERE m."+ idName + " = ? AND m."+ field ;
		if( originalValue == null )
			hql += " IS NULL ";
		else{
			hql += " = ? ";
			params.add( originalValue );
		}
		
		return getHibernateTemplate().bulkUpdate(hql, params.toArray() );
	}

	@Override
	public void deleteAll() {
		throw new DaoException("删除实体("+entityClass+")"+
				"所有集合，需要在具体子类中实现！");
	}
	
	public ListPage<T> list(ListPage<T> page, T t,Collection<String> properties ){
		return this.list(entityClass, page, t, properties);
	}
	
	protected ListPage<T> list(Class<?> clazz,ListPage<T> page, T t,Collection<String> properties ){
		if( t == null || page == null || properties == null)
			return null;
		
		HashSet<String> set = new HashSet<String>( properties );
		if( set.isEmpty() )
			return null;
		
		List<Object> params = new ArrayList<Object>( set.size() );
		StringBuffer sb = new StringBuffer( " FROM " + clazz.getName() + " AS m WHERE 1 = 1 " );
		for( String property:set ){
			Object value = ReflectionUtil.reflectGetValue(t, property);
			sb.append( " and m." + property + " = ? ");
			params.add( value );
		}
		
		return this.query(page, sb.toString(), params.toArray());
	}

	public ListPage<T> list(Class<?> clazz,ListPage<T> page, String select,Collection<Term> terms ){
		if( clazz == null || page == null || terms == null )
			return page;
		
		StringBuffer sb = new StringBuffer( " FROM " + clazz.getName() + " AS m WHERE 1 = 1 " );
		List<Object> params = new ArrayList<Object>( );
		for( Term term:terms ){
			if( Term.Type.EQUAL.equals( term.getType() ) 
					|| Term.Type.NOT_EQUAL.equals( term.getType() )
					|| Term.Type.LESS_THAN.equals( term.getType() )
					|| Term.Type.LESS_THAN_EQUAL.equals( term.getType() )
					|| Term.Type.GREATE_THAN.equals( term.getType() )
					|| Term.Type.GREATE_THAN_EQUAL.equals( term.getType() )
					){
				sb.append( " AND m." + term.getKey() + term.getType().getSymbol() +" ? ");
				params.add( term.getValue() );
			}else if( Term.Type.NULL.equals( term.getType() ) 
					|| Term.Type.NOT_NULL.equals( term.getType() ) ){
				sb.append( " AND m." + term.getKey() + term.getType().getSymbol()+" ");
			}else if( Term.Type.LIKE.equals( term.getType() ) ){
				sb.append( " AND m." + term.getKey() + " LIKE ? ");
				params.add( "%" + term.getValue() + "%");
			}else if( Term.Type.LIKE_LEFT.equals( term.getType() ) ){
				sb.append( " AND m." + term.getKey() + " LIKE ? ");
				params.add( "%" + term.getValue() );
			}else if( Term.Type.LIKE_RIGHT.equals( term.getType() ) ){
				sb.append( " AND m." + term.getKey() + " LIKE ? ");
				params.add( term.getValue() + "%");
			}else if( Term.Type.IN.equals( term.getType() ) 
					|| Term.Type.NOT_IN.equals( term.getType() )){
				if( term.getValue() instanceof Object[] ){
					Object[] objects = (Object[])term.getValue();
					for( int i=0;i<objects.length;i++ ){
						if( i== 0 )
							sb.append( " AND m." + term.getKey() + term.getType().getSymbol() + "( ");
						sb.append( ((i==0)?"":",") + "'"+ objects[i] +"'" );
						if( i==objects.length-1 )
							sb.append(" )");
					}//end for
				}else{ //仅有一个参数
					sb.append( " AND m." + term.getKey() + " = ? ");
					params.add( term.getValue() );
				}
			}
		}
		
		return query(page,select,sb.toString(),params.toArray());
	}

	@Override
	public ListPage<T> list(ListPage<T> page, String select,
			Collection<Term> terms) {
		return list( this.entityClass,page,select,terms);
	}

	@Override
	public ListPage<T> list(ListPage<T> page, Collection<Term> terms) {
		return list( page,null,terms);
	}

	@Override
	public ListPage<T> list(ListPage<T> page, String select, SearchCondition sc) {
		return list( this.entityClass,page,select,sc==null?null:sc.toTerms());
	}

	@Override
	public ListPage<T> list(ListPage<T> page, SearchCondition sc) {
		return list( page,null,sc);
	}
	
}
