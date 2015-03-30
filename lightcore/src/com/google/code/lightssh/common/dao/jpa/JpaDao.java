package com.google.code.lightssh.common.dao.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.dao.Dao;
import com.google.code.lightssh.common.dao.DaoException;
import com.google.code.lightssh.common.dao.SearchCondition;
import com.google.code.lightssh.common.dao.Term;
import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.page.Aggregation;
import com.google.code.lightssh.common.model.page.ListPage;
import com.google.code.lightssh.common.model.page.ListPage.OrderType;
import com.google.code.lightssh.common.model.page.OrderBy;
import com.google.code.lightssh.common.util.ParserUtil;
import com.google.code.lightssh.common.util.ReflectionUtil;
import com.google.code.lightssh.common.util.StringUtil;

/**
 * JPA DBDao implement
 * @author YangXiaojin
 *
 * @param <T>
 */
public class JpaDao<T extends Persistence<?>> implements Dao<T>,Serializable{
	
	private static final long serialVersionUID = -1688932844858330144L;

	private static Logger log = LoggerFactory.getLogger(JpaDao.class);
	
	/**
	 * entity class
	 */
    protected Class<T> entityClass;
    
    private EntityManager entityManager;
    
	public JpaDao( Class<T> t ) {
		this.entityClass = t;		
	}
	
	@SuppressWarnings("unchecked")
	protected JpaDao() {
		Type type = null;
		if( getClass().getGenericSuperclass() instanceof ParameterizedType)
			type =((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		if( type != null && type instanceof Class){
			this.entityClass = (Class<T>) type;
		}
	}
	
	@Deprecated
	protected EntityManager getJpaTemplate(){
		return this.getEntityManager();
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * 关闭连接
	 */
	protected void close(ResultSet rs,PreparedStatement ps,Connection conn ){
		if( rs != null ){
			try{
				rs.close();
			}catch( Exception e ){
				log.warn("关闭ResultSet异常：",e);
			}
		}
		
		if( ps != null ){
			try{
				ps.close();
			}catch( Exception e ){
				log.warn("关闭PreparedStatement异常：",e);
			}
		}
		
		if( conn != null ){
			try{
				conn.close();
			}catch( Exception e ){
				log.warn("关闭Connection异常：",e);
			}
		}
	}
	
	/**
	 * FROM JPQL
	 */
	protected String getFromJpql(Class<?> clazz,String symbol){
		if( clazz == null )
			clazz = this.entityClass;
		
		StringBuffer sb = new StringBuffer( " FROM ");
		sb.append( clazz.getName() );
		
		if( StringUtil.hasText(symbol) )
			sb.append(" AS " + symbol.trim() );
		
		return sb.toString();
	}
	
	/**
	 * FROM JPQL
	 */
	protected String getFromJpql(Class<?> clazz){
		return getFromJpql(clazz,"m");
	}
	
	/**
	 * FROM JPQL
	 */
	protected String getFromJpql( ){
		return getFromJpql( entityClass );
	}

	@Override
	public void create(T t) {
		t.preInsert();
		getEntityManager().persist(t );	
	}

	@Override
	public void create(Collection<T> entities) {
		for( T t:entities ){
			create(t);
		}
	}

	@Override
	public void delete(T t) {
		if( t == null )
			return;
		getEntityManager().remove(t);
	}

	@Override
	public void delete(Serializable identity) {
		delete( this.read(identity) );
	}

	@Override
	public void delete(Collection<T> entities) {
		if( entities == null )
			return;
		
		for(T t:entities )
			getEntityManager().remove(t);
	}
	
	/**
	 * 总记录数
	 */
	protected int rowCount() {
		String hql = " SELECT COUNT( m ) " + getFromJpql();
		
		return rowCount( hql );
	}
	
	protected int rowCount( String hql ) {
		return rowCount( hql, null);
	}
	
	/**
	 * 总记录数
	 */
	protected int rowCount( String hql , Object[] params ) {
		Query query = getEntityManager().createQuery(hql);
		this.addQueryParams(query,params);
		Object count = query.getSingleResult();
		
		return ( count != null && count instanceof Long)?
			 ParserUtil.parseInt(((Long)count).intValue()):0;
	}
	
	protected ListPage<T> query( ListPage<T> page , String from_jqpl){
		return query( page,from_jqpl, null );
	}
	
	/**
	 * order by 
	 */
	protected String addOrderBy( ListPage<?> page ){
		return addOrderBy(page,"m");
	}
	
	/**
	 * order by 
	 */
	protected String addOrderBy( ListPage<?> page ,String symbol){
		boolean flag = StringUtil.hasText(symbol);
		symbol = StringUtil.hasText(symbol)?symbol.trim():symbol;
		
		//add order
		StringBuffer orderby = new StringBuffer("");
		List<OrderBy> orderByList = page.listAllOrderBy();
		if( orderByList != null && !orderByList.isEmpty() ){
			for(OrderBy each:orderByList ){
				if( each == null )
					continue;
				orderby.append("".equals(orderby.toString())?" ORDER BY ":" ,");
				orderby.append( (flag?(" " + symbol + "."):"") + each.getProperty() 
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
			query.setParameter(i+1, params[i] );
		
		return query;
	}
	
	/**
	 * add Query parameters
	 */
	protected Query addQueryParams( Query query ,Object param ){
		if( param != null){
			if(param instanceof Object[] ){
				for( int i=0;i<((Object[])param).length;i++ )
					query.setParameter(i+1, ((Object[])param)[i] );
			}else if( param instanceof Collection<?> ){
				int i=1;
				for( Object item:(Collection<?>)param)
					query.setParameter(i++, item);
			}
			else
				query.setParameter(1, param );
		}
		
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
	protected ListPage<?> queryAggregation( ListPage<?> page,String from_jqpl,Object[] params ){
		if( page == null || page.getAggregationList() == null 
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
		//List<Object> results = getJpaTemplate().find( agg_hql.toString(),params );
		Query query = getEntityManager().createQuery(agg_hql.toString());
		this.addQueryParams(query, params);
		List<Object> results = query.getResultList();
		
		if( results != null && !results.isEmpty() ){
			if( results.get(0) instanceof Object[] )
				page.assignAggregateValue( (Object[])results.get(0) );
			else
				page.assignAggregateValue( new Object[]{results.get(0)} );
		}
		
		return page;
	}
	
	/**
	 * 取 AS 后面的符号
	 */
	protected String fetchAsSymbol(String hql ){
		String symbol = ""; //符号
		String as_matche = "(.*\\s+)(?i)AS(\\s+.*)"; 
		String as_replace = "(\\s+)(?i)AS(\\s+)";
		String as_symbol_replace = "(\\s+)(?i)AS(\\s+)\\w+(\\s*)";
		if( hql.matches( as_matche ) ){ //存在AS
			String[] array = hql.split(as_symbol_replace);
			if( array.length == 1){
				symbol = hql.substring(array[0].length() );
				symbol = symbol.replaceFirst(as_replace,"").trim();
			}else if( array.length == 2){
				symbol = hql.substring(array[0].length(), 
						hql.length() - array[1].length() );
				symbol = symbol.replaceFirst(as_replace,"").trim();
			}
		}
		
		return symbol;
	}
	
	@SuppressWarnings("unchecked")
	protected ListPage<?> queryObject( final ListPage<?> page ,final String select_jpql, 
			final String from_jqpl , final Object[] params ){
		
		String symbol = fetchAsSymbol( from_jqpl );
		String count_hql = " SELECT COUNT("+(!StringUtil.hasText(symbol)?"*":symbol)+") ";
		if(StringUtil.clean(select_jpql)!=null){
			String[] sels = select_jpql.trim().split("( )+");
			if( sels.length == 2 ){
				count_hql = sels[0] + " COUNT( " + sels[1] + " )";
			}else if( sels.length == 3 ){
				count_hql = sels[0] + " COUNT( " + sels[1] + " " + sels[2]+ " )";
			}
		}
		count_hql += from_jqpl;
		page.setAllSize( rowCount(count_hql,params ) ); //all size
		
		if( !Boolean.TRUE.equals(page.getOverNumber())
				&& (page.getNumber() > page.getAllPage()) )
			page.setNumber(Math.min(page.getNumber(), page.getAllPage()));
		
		if( page.getAllSize() > 0 && page.getSize() > 0 ){
			//聚合查询
			queryAggregation(page,from_jqpl,params);
			
			String jpql = null;
			if(StringUtil.hasText(select_jpql) )
				jpql = select_jpql + from_jqpl;
			else if ( StringUtil.hasText(symbol)  )
				jpql = " SELECT "+symbol+" " + from_jqpl;
			else
				jpql = from_jqpl;
			jpql += addOrderBy( page,symbol);//add order
			
			Query query = getEntityManager().createQuery( jpql )
				.setFirstResult(page.getStart()-1)
				.setMaxResults(page.getSize());
			
			addQueryParams( query,params );
			
			page.setList( query.getResultList());
			//ACTION:force all results to be pulled back before we close the entity manager
			//list.size();
		}//end if
		
		return page;
	}
	
	@Override
	public ListPage<T> list(ListPage<T> page) {
		return query( page , this.getFromJpql() );
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
		return (T)getEntityManager().find( this.entityClass , t.getIdentity() );
	}

	@Override
	public T read(Serializable identity) {
		return (T)getEntityManager().find( this.entityClass , identity );
	}
	
	@Override
	public T readWithLock(Serializable identity) {
		return (T)getEntityManager().find( this.entityClass 
				, identity,LockModeType.PESSIMISTIC_READ );
	}
	
	@Override
	public void update(T t) {
		getEntityManager().merge(t);
	}

	@Override
	public void update(Collection<T> entities) {
		for( T t:entities )
			update(t);
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
		
		Query query = getEntityManager().createQuery(hql);
		this.addQueryParams(query, params);
		return query.executeUpdate();
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
		
		StringBuffer sb = new StringBuffer( getFromJpql( clazz ) + " WHERE 1 = 1 " );
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
