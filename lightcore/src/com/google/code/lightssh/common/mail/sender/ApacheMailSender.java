package com.google.code.lightssh.common.mail.sender;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.mail.Address;
import com.google.code.lightssh.common.mail.Attachment;
import com.google.code.lightssh.common.mail.MailAddress;
import com.google.code.lightssh.common.mail.MailContent;
import com.google.code.lightssh.common.mail.MailException;
import com.google.code.lightssh.common.model.ConnectionConfig;

/**
 * MailSender 实现
 *
 */
public class ApacheMailSender implements MailSender{
	
	private static final long serialVersionUID = 3964558689373678912L;

	private static Logger log = LoggerFactory.getLogger(ApacheMailSender.class);
	
	/**
	 * 是否调试
	 */
	private boolean debug = false;
	
	/**
	 * 编码
	 */
	private String charset = "UTF-8";
	
	public void setDebug( boolean debug ){
		this.debug = debug;
	}
		
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * fill connection parameters
	 * @param email
	 * @param conf
	 * @param mailAddress
	 * @param content
	 */
	protected void fillConfig( Email email,ConnectionConfig conf, 
			MailAddress mailAddress,MailContent content){
		  
		  if( conf == null )
			  throw new MailException("邮件服务器连接参数为空！");
		  
		  email.setHostName( conf.getHost() );
		  email.setAuthentication( conf.getUsername(), conf.getPassword() );
		  int port = 25;
		  try{
			  port = Integer.valueOf( conf.getPort()).intValue();
		  }catch( NumberFormatException e ){
			  log.warn("邮箱端口设置出错，使用默认值[25]。");
		  }
		  
		  if( conf.isSsl() ){
			  email.setSslSmtpPort(String.valueOf(port));
			  email.setTLS(true);
		  }else
			  email.setSmtpPort( port );
		  
		  email.setSSL(conf.isSsl());
		  
		  //邮件地址
		  if( mailAddress == null || mailAddress.getFrom()==null 
				  || mailAddress.getTo() == null ){
			  throw new MailException("邮件发送人地址、邮件接收人地址为空！");
		  }
		  
		  try{
			  //mail from address 
			  Address from = mailAddress.getFrom();
			  email.setFrom( from.getEmail(), from.getName() );
			  
			  //添加收件人
			  if( mailAddress.getTo() == null || mailAddress.getTo().isEmpty() )
				  throw new MailException("未设定邮件接收者！");
			  
			  for( Address to:mailAddress.getTo() ){
				  email.addTo( to.getEmail(), to.getName() );
			  }
			  
			  //添加抄送人
			  if( mailAddress.getCc() != null ){
				  for( Address cc:mailAddress.getCc() ){
					  email.addCc( cc.getEmail(), cc.getName() );
				  }
			  }
			  
			  email.setSubject( content.getSubject() );
			  email.setCharset( charset ); //("UTF-8")避免中文内容出现乱码
			  if( email instanceof HtmlEmail)
				  ((HtmlEmail) email).setHtmlMsg(content.getText());
			  else
				  email.setMsg( content.getText() );		
			  
			  email.setDebug( debug );
		  }catch( org.apache.commons.mail.EmailException ee){
			  throw new MailException( ee );
		  }	  
		  
	}
	
	/**
	 * 发送文本邮件
	 * @param conf ConnectionConfig 邮件服务器连接参数
	 * @param mailAddress MailAddress 邮件地址
	 * @param mail TextMail 文本内容
	 */
	public void send( ConnectionConfig conf, MailAddress mailAddress, MailContent mail ){		
		  SimpleEmail email = new SimpleEmail();		  
		  fillConfig( email,conf,mailAddress,mail );
		  
		  try{
			  email.send();
		  }catch( org.apache.commons.mail.EmailException ee){
			  throw new MailException( ee );
		  }	  
	}
	
	/**
	 * 发送文本邮件
	 * @param conf ConnectionConfig 邮件服务器连接参数
	 * @param mailAddress MailAddress 邮件地址
	 * @param mail TextMail 文本内容
	 */
	public void sendHtml( ConnectionConfig conf, MailAddress mailAddress, MailContent mail ){		
		HtmlEmail email = new HtmlEmail();		  
		fillConfig( email,conf,mailAddress,mail );
		
		try{
			email.send();
		}catch( org.apache.commons.mail.EmailException ee){
			throw new MailException( ee );
		}	  
	}
	
	/**
	 * 
	 * 发送带附件的文本邮件
	 * @param conf ConnectionConfig 邮件服务器连接参数
	 * @param mailAddress MailAddress 邮件地址
	 * @param mail TextMail 文本内容
	 * @param attachments 附件
	 */
	public void send( ConnectionConfig conf, MailAddress mailAddress, 
			MailContent mail, Attachment[] attachments){
		
		MultiPartEmail email = new MultiPartEmail();	  
		fillConfig( email,conf,mailAddress,mail );
				
		try{		
			for( Attachment att: attachments ){			
				email.attach( att.getSource(), att.getName(), att.getDescription() );
			}
			email.send();
		}catch( org.apache.commons.mail.EmailException ee){
			throw new MailException( ee );
		}	 
		
	}
	
	/** 
	 * 发送带附件的文本邮件
	 */
	public void send( ConnectionConfig conf, MailAddress mailAddress, 
			MailContent mail, Attachment attachment){
		this.send(conf, mailAddress, mail, new Attachment[]{ attachment });
	}
	
	/** 
	 * 发送带附件的文本邮件
	 */
	public void send( ConnectionConfig conf, MailAddress mailAddress, 
			String mailtext, Attachment attachment){
		this.send(conf, mailAddress, new MailContent("", mailtext), new Attachment[]{ attachment });
	}
	
}
