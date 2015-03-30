package com.google.code.lightssh.common.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.util.StringUtil;

/**
 * 用于配置数据库连接参数
 * @author YangXiaojin
 *
 */
@Deprecated
public class ConfigurableDataSource extends BasicDataSource{
	
	private static Logger log = LoggerFactory.getLogger(ConfigurableDataSource.class);
	
	private SystemConfig config;
	
	public void setSystemConfig( SystemConfig config ){
		this.config = config;
	}
	
	/**
	 * 配置参数
	 */
	public void init( ){
		if( config == null )
			return;
		
		super.setDriverClassName( config.getProperty(SystemConfig.DB_DRIVER, getDriverClassName()));
		super.setUrl( config.getProperty(SystemConfig.DB_URL, getUrl()));
		super.setUsername( config.getProperty(SystemConfig.DB_USERNAME, getUsername()));
		super.setPassword( config.getProperty(SystemConfig.DB_PASSWORD, getPassword()));
		
		setConfigInitialSize();//初始连接数
		
		//同一时间可以从池分配的最多连接数量
		setConfigMaxActive();
		
		setConfigMaxIdle(); //最大闲置数
		
		super.setRemoveAbandoned(true);
	}

	/**
	 * 初始连接数据
	 */
	protected void setConfigInitialSize( ){
		int intInitialSize = 10;
		try{
			String initialSize = config.getProperty(SystemConfig.DB_DBCP_INITIAL_SIZE);
			if( StringUtil.hasText(initialSize) )
				intInitialSize = Integer.parseInt(initialSize);
		}catch( Exception e ){
			log.warn("设置数据库连接池[db.dbcp.initial.size]参数异常：{}",e.getMessage());
		}
		
		super.setInitialSize( intInitialSize  ); 
	}
	
	/**
	 * 最大活动数
	 */
	protected void setConfigMaxActive( ){
		int intMaxActive = 20;
		try{
			String maxActive = config.getProperty(SystemConfig.DB_DBCP_MAX_ACTIVE);
			if( StringUtil.hasText(maxActive) )
				intMaxActive = Integer.parseInt(maxActive);
		}catch( Exception e ){
			log.warn("设置数据库连接池[db.dbcp.max.active]参数异常：{}",e.getMessage());
		}
		
		super.setMaxActive( intMaxActive  ); 
	}
	
	/**
	 * 最大闲置数
	 */
	protected void setConfigMaxIdle( ){
		int intMaxIdel = 8;
		try{
			String maxIdle = config.getProperty(SystemConfig.DB_DBCP_MAX_IDLE);
			if( StringUtil.hasText(maxIdle) )
				intMaxIdel = Integer.parseInt(maxIdle);
		}catch( Exception e ){
			log.warn("设置数据库连接池[db.dbcp.max.idle]参数异常：{}",e.getMessage());
		}
		super.setMaxIdle( intMaxIdel  ); 
	}
}
