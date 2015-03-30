package com.google.code.lightssh.common.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.code.lightssh.common.entity.Persistence;

/**
 * JPA dao implements
 * @author YangXiaojin
 */
public class JpaAnnotationDao<T extends Persistence<?>> extends JpaDao<T>{
	
	private static final long serialVersionUID = 1966398397680900721L;

	@PersistenceContext
	public void setMyEntityManager(EntityManager entityManager) {
		super.setEntityManager(entityManager);
	}

}
