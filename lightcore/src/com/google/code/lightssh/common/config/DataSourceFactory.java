package com.google.code.lightssh.common.config;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.ApplicationException;
import com.google.code.lightssh.common.util.StringUtil;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

/**
 * 数据源工厂方法
 */
public class DataSourceFactory {
	
	private static Logger log = LoggerFactory.getLogger(DataSourceFactory.class);
	
	/**
	 * 连接池类型
	 */
	public enum JDBCPoolType{
		DBCP("DBCP"),
		C3P0("C3P0"),
		BONECP("BoneCP"),
		;
		
		private String value;

		private JDBCPoolType(String value) {
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
	 * 参数配置
	 */
	protected SystemConfig config;
	
	public void setSystemConfig( SystemConfig config ){
		this.config = config;
	}
	
	/**
	 * 创建数据源
	 */
	public DataSource createDataSource(){
		JDBCPoolType type = JDBCPoolType.DBCP;
		
		String pool = config.getProperty(SystemConfig.DB_POOL);
		if( config != null && StringUtil.hasText(pool) ){
			if(JDBCPoolType.DBCP.name().equalsIgnoreCase(pool))
				type = JDBCPoolType.DBCP;
			else if(JDBCPoolType.C3P0.name().equalsIgnoreCase(pool) )
				type = JDBCPoolType.C3P0;
			else if(JDBCPoolType.BONECP.name().equalsIgnoreCase(pool) )
				type = JDBCPoolType.BONECP;
		}
		
		if( JDBCPoolType.DBCP.equals(type) ){
			return createDBCP();
		}else if( JDBCPoolType.BONECP.equals(type) ){
			return createBoneCP();
		}
		
		throw new ApplicationException("数据库连接池["+type+"]未配置完成！");
	}
	
	/**
	 * 创建DBCP数据源连接池
	 */
	protected BasicDataSource createDBCP( ){
		BasicDataSource dbcp = new BasicDataSource();
		dbcp.setDriverClassName( this.getDriverClassName() );
		dbcp.setUrl( this.getUrl() );
		dbcp.setUsername( this.getUsername() );
		dbcp.setPassword( this.getPassword() );
		
		dbcp.setRemoveAbandoned(true); //TODO
		dbcp.setRemoveAbandonedTimeout(this.getTimeoutInSeconds() );
		
		//初始连接数
		dbcp.setInitialSize( getInitialSize() );
		//同一时间可以从池分配的最多连接数量
		dbcp.setMaxActive(getMaxActive());
		//最大闲置数
		dbcp.setMaxIdle( getMaxIdle() );
		
		log.info("DBCP连接池创建完成！");
		return dbcp;
	}
	
	/**
	 * 创建BoneCP数据源连接池
	 */
	protected BoneCPDataSource createBoneCP( ){
		BoneCPConfig config = new BoneCPConfig();
		try {
			Class.forName( this.getDriverClassName() );
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(
					"初始化连接池[BoneCP]无法找到驱动["
					+getDriverClassName()+"]:",e);
		}
		config.setJdbcUrl( this.getUrl() );
		config.setUsername( this.getUsername() );
		config.setPassword( this.getPassword() );
		
		config.setPartitionCount( this.getPartitionCount() );
		config.setAcquireIncrement(this.getAcquireIncrement());
		config.setMinConnectionsPerPartition(getMinConnectionsPerPartition());
		config.setMaxConnectionsPerPartition(getMaxConnectionsPerPartition());
		if( getTimeoutInSeconds() > 0 )
			config.setConnectionTimeout(getTimeoutInSeconds(),TimeUnit.SECONDS );
		
		config.setIdleMaxAgeInSeconds(getIdleMaxAgeInSeconds());
		config.setIdleConnectionTestPeriodInSeconds(getIdleConnectionTestPeriodInSeconds());
		config.setReleaseHelperThreads(getReleaseHelperThreads());
		config.setStatementsCacheSize(getStatementsCacheSize());
		
		BoneCPDataSource bonecp = new BoneCPDataSource(config);
		log.info("BoneCP连接池创建完成！");
		return bonecp;
	}
	
	public String getDriverClassName( ){
		return config.getProperty(SystemConfig.DB_DRIVER);
	}
	
	public String getUrl( ){
		return config.getProperty(SystemConfig.DB_URL);
	}
	
	public String getUsername( ){
		return config.getProperty(SystemConfig.DB_USERNAME);
	}
	
	public String getPassword( ){
		return config.getProperty(SystemConfig.DB_PASSWORD);
	}
	
	/**
	 * 超时（秒）
	 */
	public int getTimeoutInSeconds( ){
		return getParamValue(SystemConfig.DB_TIMEOUT_IN_SECONDS,300);
	}
	
	/**
	 * DBCP初始连接数据
	 */
	protected int getInitialSize( ){
		return getParamValue(SystemConfig.DB_DBCP_INITIAL_SIZE,10);
	}
	
	/**
	 * DBCP最大活动数
	 */
	protected int getMaxActive( ){
		return getParamValue(SystemConfig.DB_DBCP_MAX_ACTIVE,8);
	}
	
	/**
	 * DBCP最大闲置数
	 */
	protected int getMaxIdle( ){
		return getParamValue(SystemConfig.DB_DBCP_MAX_IDLE,8);
	}
	
	/**
	 * BoneCP分区数
	 */
	protected int getPartitionCount( ){
		return getParamValue(SystemConfig.DB_BONECP_PARTITION_COUNT,2);
	}
	
	/**
	 * BoneCP 连接增加步长
	 * up to a maximum of maxConnectionsPerPartition
	 */
	protected int getAcquireIncrement( ){
		return getParamValue(SystemConfig.DB_BONECP_ACQUIRE_INCREMENT,10);
	}
	
	/**
	 * BoneCP 最小连接数据每个分区
	 */
	protected int getMinConnectionsPerPartition( ){
		return getParamValue(SystemConfig.DB_BONECP_MIN_CONNECTIONS_PER_PARTITION,5);
	}
	
	/**
	 * BoneCP 最大连接数据每个分区
	 */
	protected int getMaxConnectionsPerPartition( ){
		return getParamValue(SystemConfig.DB_BONECP_MAX_CONNECTIONS_PER_PARTITION,10);
	}
	
	/**
	 * IDLE MAX AGE 时间（秒）
	 */
	protected int getIdleMaxAgeInSeconds( ){
		return getParamValue(SystemConfig.DB_BONECP_IDLE_MAX_AGE_IN_SECONDS,240);
	}
	
	/**
	 * IDLE Conn test period（秒）
	 */
	protected int getIdleConnectionTestPeriodInSeconds( ){
		return getParamValue(SystemConfig.DB_BONECP_IDLE_CONN_TEST_PERIOD_IN_SECONDS,60);
	}
	
	/**
	 * release helper threads
	 */
	protected int getReleaseHelperThreads( ){
		return getParamValue(SystemConfig.DB_BONECP_RELEASE_HELPER_THREADS,5);
	}
	
	/**
	 * statements cache size
	 */
	protected int getStatementsCacheSize( ){
		return getParamValue(SystemConfig.DB_BONECP_STATEMENTS_CACHE_SIZE,50);
	}
	
	/**
	 * 获取参数值
	 */
	protected int getParamValue(String key,int defValue ){
		int result = defValue;
		try{
			String value = config.getProperty(key);
			if( StringUtil.hasText(value) )
				result = Integer.parseInt(value);
		}catch( Exception e ){
			log.warn("设置数据库连接池[{}]参数异常：{}",key,e.getMessage());
		}
		
		return result;
	}

}
