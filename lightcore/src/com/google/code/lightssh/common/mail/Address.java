package com.google.code.lightssh.common.mail;

import java.io.Serializable;

/**
 * 邮件地址
 */
public class Address implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 名字
	 */
	private String name;

	/**
	 * 邮件地址
	 */
	private String email;

	public Address() {
		super();
	}

	public Address(String email, String name) {
		super();
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
