package com.google.code.lightssh.common.mail.sender;

import java.io.Serializable;

import com.google.code.lightssh.common.mail.MailAddress;
import com.google.code.lightssh.common.mail.MailContent;
import com.google.code.lightssh.common.model.ConnectionConfig;

/**
 * email sender
 *
 */
public interface MailSender extends Serializable{
	
	/**
	 * 邮件发送
	 * @param config 连接参数
	 * @param mailAddress 发件人/收件人/抄送人
	 * @param content 邮件内容
	 */
	public void send( ConnectionConfig config, MailAddress mailAddress, MailContent content );
	
	/**
	 * 邮件发送
	 * @param config 连接参数
	 * @param mailAddress 发件人/收件人/抄送人
	 * @param content 邮件内容
	 */
	public void sendHtml( ConnectionConfig config, MailAddress mailAddress, MailContent content );

}
