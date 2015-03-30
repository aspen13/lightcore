package com.google.code.lightssh.common.config;

import net.sf.log4jdbc.Spy;
import net.sf.log4jdbc.tools.Log4JdbcCustomFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.util.StringUtil;

/**
 * 数据库日志配置
 *
 */
public class JdbcLogCustomFormatter extends Log4JdbcCustomFormatter{
	
	public static final String SQL_TIMING_LOGGER = "jdbc.sql.timing";
	
	public static final String SQL_EXCEPTION_LOGGER = "jdbc.sql.exception";
	
	public static final long DEFAULT_TIMING_MILLIS = 10;
	
	public static final String LOG_SQL_TIMING_MILLIS_KEY = "log.sql.timing.millis";
	
	public static final String LOG_SQL_TIMING_ENABLED_KEY = "log.sql.timing.enabled";
	
	private static Logger log = LoggerFactory.getLogger(JdbcLogCustomFormatter.class);
	
    /**
     * Logger that shows only the SQL that is occuring
     */
    private final Logger sqlExceptionLogger = LoggerFactory.getLogger( SQL_EXCEPTION_LOGGER );
    
    /**
     * Logger that shows the SQL timing, post execution
     */
    private final Logger sqlTimingLogger = LoggerFactory.getLogger( SQL_TIMING_LOGGER );
    
	private SystemConfig config;
	
	/**
	 * 是否输出SQL耗时日志
	 */
	protected boolean isTimingLogEnabled( ){
		return config != null && "true".equals(config.getProperty( LOG_SQL_TIMING_ENABLED_KEY ,"false"));
	}
	
	/**
	 * 超出配置时间
	 */
	protected boolean isOverTiming( long execTime ){
		long timing_millis = DEFAULT_TIMING_MILLIS;
		try{
			timing_millis = Long.valueOf(config.getProperty(LOG_SQL_TIMING_MILLIS_KEY));
		}catch( Exception e ){
			log.warn("SQL耗时日志参数配置错误，使用默认值[{}]",DEFAULT_TIMING_MILLIS);
		}
		
		return execTime > timing_millis;
	}
	
	public void setSystemConfig( SystemConfig config ){
		this.config = config;
	}
	
    /**
     * Called when a jdbc method throws an Exception.
     * 
     * @param spy the Spy wrapping the class that threw an Exception.
     * @param methodCall a description of the name and call parameters of the method generated the Exception.
     * @param e the Exception that was thrown.
     * @param sql optional sql that occured just before the exception occured.
     * @param execTime   optional amount of time that passed before an exception was thrown when sql was being executed.
     *                   caller should pass -1 if not used
     */
    public void exceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime)
    {
        String classType = spy.getClassType();
        Integer spyNo = spy.getConnectionNumber();
        String header = "SQL执行异常，Conn NO.["+spyNo + "],execTime["+execTime+"] " + classType + "." + methodCall;
    	//String header = "SQL执行异常";
        if (sql == null){
        	sqlExceptionLogger.error(header, e);
        } else{
        	sqlExceptionLogger.error(header + "[ {} ]。\n异常SQL语句：{}"
        			,StringUtil.clean(e.getMessage()),sql);
        }
    }
    
    /**
     * SQL耗时日志
     */
    public void sqlTimingOccured(Spy spy, long execTime, String methodCall, String sql){
    	if( isTimingLogEnabled() && isOverTiming(execTime) )
    		sqlTimingLogger.info( "SQL执行耗时{}毫秒：{}",execTime,sql );
    }

}
