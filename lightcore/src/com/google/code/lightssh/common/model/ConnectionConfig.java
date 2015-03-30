package com.google.code.lightssh.common.model;

/**
 * 连接配置
 * @author Aspen
 *
 */
public class ConnectionConfig {
	
    /**
     * 协议
     */
    private String protocol;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private String port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    
    /**
     * SSL
     */
    private boolean ssl;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setPort( int port ){
		this.port = String.valueOf( port );
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
    
}
