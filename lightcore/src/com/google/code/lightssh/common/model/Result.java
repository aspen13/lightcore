package com.google.code.lightssh.common.model;

import java.io.Serializable;

/** 
 * @author YangXiaojin
 * @date 2012-12-13
 *  
 */
public class Result implements Serializable{

	private static final long serialVersionUID = -5705613543441028464L;
	
	public enum Status{
		SUCCESS("成功"),
		FAILURE("失败"),
		WAITING_FOR_REPLY("等待回复"),
		EXCEPTION("异常")
		;
		
		private String value;
		
		Status( String value ){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public String toString(){
			return this.value;
		}
	}

	/**
	 * 状态
	 */
	protected Status status;
	
	/**
	 * 关键字
	 */
	protected String key;
	
	/**
	 * 对象
	 */
	protected Object object;
	
	/**
	 * 消息
	 */
	protected String message;

	public Result( ) {
		super();
	}
	
	public Result(Status status, String key, Object object, String message) {
		super();
		this.status = status;
		this.key = key;
		this.object = object;
		this.message = message;
	}
	
	public Result(boolean success, String key, Object object, String message) {
		this(success?Status.SUCCESS:Status.FAILURE,key,object,message);
	}
	
	public Result(boolean success, String key, Object object) {
		this.status = success?Status.SUCCESS:Status.FAILURE;
		this.key = key;
		this.object = object;
	}
	
	public Result( boolean success ) {
		this.status = success?Status.SUCCESS:Status.FAILURE;
	}
	
	public Result( boolean success,String message ) {
		this.status = success?Status.SUCCESS:Status.FAILURE;
	}

	/**
	 * 是否成功
	 */
	public boolean isSuccess() {
		return Status.SUCCESS.equals(this.status);
	}
	
	/**
	 * 是否调用成功
	 */
	public boolean isInvokeSuccess() {
		return Status.SUCCESS.equals(this.status) 
			|| Status.WAITING_FOR_REPLY.equals(status);
	}
	
	/**
	 * 是否异常
	 */
	public boolean isException() {
		return Status.EXCEPTION.equals(this.status);
	}
	
	/**
	 * 是否失败
	 */
	public boolean isFailure() {
		return Status.FAILURE.equals(status);
	}
	
	/**
	 * 是否等待回复
	 */
	public boolean isWaitingForReply() {
		return Status.WAITING_FOR_REPLY.equals(status);
	}
	
	/**
	 * 设置状态
	 */
	public void setStatus( boolean success ){
		this.status = success?Status.SUCCESS:Status.FAILURE;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
