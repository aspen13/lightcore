package com.google.code.lightssh.common.support.shiro;

import org.apache.shiro.authc.AuthenticationException;


/**
 * 帐户登录锁定异常
 * @author YangXiaojin
 *
 */
public class TimeLockedException extends AuthenticationException{

	private static final long serialVersionUID = 5038485428928343149L;

	public TimeLockedException() {
		super();
	}

	public TimeLockedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeLockedException(String message) {
		super(message);
	}

	public TimeLockedException(Throwable cause) {
		super(cause);
	}
}
