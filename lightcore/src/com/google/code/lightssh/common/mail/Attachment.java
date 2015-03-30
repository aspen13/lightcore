package com.google.code.lightssh.common.mail;

import java.io.Serializable;

import javax.activation.DataSource;

/**
 * 邮件附件
 * 
 * FileDataSource fds = new FileDataSource( new
 * 		File("E:\\_zip_test\\邮件附件测试 Mail Attament Test.txt") );
 * 
 */
public class Attachment implements Serializable{

	private static final long serialVersionUID = -7275902762141637637L;

	/**
	 * 数据源
	 */
	private DataSource source;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

	public Attachment() {
		super();
	}

	public Attachment(DataSource source, String name, String description) {
		super();
		this.source = source;
		this.name = name;
		this.description = description;
	}

	public DataSource getSource() {
		return source;
	}

	public void setSource(DataSource source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
