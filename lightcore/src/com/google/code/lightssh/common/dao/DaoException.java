package com.google.code.lightssh.common.dao;

import com.google.code.lightssh.common.ApplicationException;


/**
 * DAO exception
 * @author YangXiaojin
 *
 */
public class DaoException extends ApplicationException{

	private static final long serialVersionUID = 1L;

	public DaoException() {
		super();
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}
	
}
