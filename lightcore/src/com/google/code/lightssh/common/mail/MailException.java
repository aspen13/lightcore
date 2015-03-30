package com.google.code.lightssh.common.mail;

import com.google.code.lightssh.common.ApplicationException;


/**
 * mail exception
 */
public class MailException extends ApplicationException{

	private static final long serialVersionUID = 1L;

	public MailException() {
		super();
	}

	public MailException(String message, Throwable cause) {
		super(message, cause);
	}

	public MailException(String message) {
		super(message);
	}

	public MailException(Throwable cause) {
		super(cause);
	}

}
