package com.google.code.lightssh.common.mail;

/**
 * mail content
 *
 */
public class MailContent {
	
	/**
	 * 主题
	 */
	protected String subject;
	
	/**
	 * 文字内容
	 */
	protected String text;
	
	public MailContent(String subject, String text) {
		super();
		this.subject = subject;
		this.text = text;
	}
	
	public MailContent(String text) {
		this(null,text);
	}
	
	public MailContent( ) {
		this(null,null);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
		
}
