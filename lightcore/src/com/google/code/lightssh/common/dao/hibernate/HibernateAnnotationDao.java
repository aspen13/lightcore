package com.google.code.lightssh.common.dao.hibernate;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;

import com.google.code.lightssh.common.entity.Persistence;

/**
 * hibernate dao implements
 * @author YangXiaojin
 */
public class HibernateAnnotationDao<T extends Persistence<?>> extends HibernateDao<T>{
	
	@Resource(name="sessionFactory")
	public void setMySessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}

}
