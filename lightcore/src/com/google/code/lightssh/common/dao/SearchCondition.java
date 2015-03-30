package com.google.code.lightssh.common.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.code.lightssh.common.util.StringUtil;

/**
 * 查询条件对象
 * @author YangXiaojin
 *
 */
public class SearchCondition implements Serializable{

	private static final long serialVersionUID = -4065661279187116872L;
	
	private Set<Term> terms = new HashSet<Term>();
	
	public Set<Term> toTerms(){
		return this.terms;
	}
	
	private SearchCondition term(Term.Type type,String key,Object value ){
		if(StringUtil.clean(key) == null || value == null )
			return this;
		
		Term term = new Term( type,key,value);
		terms.add( term );
		return this;
	}
	
	private SearchCondition term(Term.Type type,String key ){
		if(StringUtil.clean(key) == null )
			return this;
		
		Term term = new Term( type,key,null);
		terms.add( term );
		return this;
	}
	
	/**
	 * 全等
	 */
	public SearchCondition equal(String key, Object value) {
		return term( Term.Type.EQUAL,key,value);
	}
	
	/**
	 * 不等查询
	 */
	public SearchCondition notEqual(String key, Object value) {
		return term( Term.Type.NOT_EQUAL,key,value);
	}
	
	/**
	 * 为空
	 */
	public SearchCondition isNull(String key) {
		return term(Term.Type.NULL,key);
	}
	
	/**
	 * 不为空
	 */
	public SearchCondition isNotNull(String key) {
		return term(Term.Type.NOT_NULL,key);
	}
	
	/**
	 * 小于
	 */
	public SearchCondition lessThan(String key, Object value) {
		return term(Term.Type.LESS_THAN,key,value);
	}
	
	/**
	 * 小于等于
	 */
	public SearchCondition lessThanOrEqual(String key, Object value) {
		return term(Term.Type.LESS_THAN_EQUAL,key,value);
	}
	
	/**
	 * 大于
	 */
	public SearchCondition greateThan(String key, Object value) {
		return term(Term.Type.GREATE_THAN,key,value);
	}
	
	/**
	 * 大于等于
	 */
	public SearchCondition greateThanOrEqual(String key, Object value) {
		return term(Term.Type.GREATE_THAN_EQUAL,key,value);
	}
	
	/**
	 * 包含
	 */
	public SearchCondition in(String key, Object ... value) {
		return term(Term.Type.IN,key,value);
	}
	
	/**
	 * 不包含
	 */
	public SearchCondition notIn(String key, Object ... value) {
		return term(Term.Type.NOT_IN,key,value);
	}
	
	/**
	 * 模糊匹配
	 */
	public SearchCondition like(String key, Object value) {
		return term(Term.Type.LIKE,key,value);
	}
	/**
	 * 模糊匹配
	 */
	public SearchCondition likeLeft(String key, Object value) {
		return term(Term.Type.LIKE_LEFT,key,value);
	}
	/**
	 * 模糊匹配
	 */
	public SearchCondition likeRight(String key, Object value) {
		return term(Term.Type.LIKE_RIGHT,key,value);
	}

}
