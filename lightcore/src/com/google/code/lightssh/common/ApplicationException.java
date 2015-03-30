package com.google.code.lightssh.common;

/**
 * 系统根异常
 * @author YangXiaojin ++
 *
 */
public class ApplicationException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public ApplicationException() {
		super();
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}

}
