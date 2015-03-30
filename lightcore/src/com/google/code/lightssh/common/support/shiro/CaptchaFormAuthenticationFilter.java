package com.google.code.lightssh.common.support.shiro;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.config.SystemConfig;
import com.google.code.lightssh.common.util.RequestUtil;
import com.google.code.lightssh.common.util.TextFormater;
import com.google.code.lightssh.common.web.SessionKey;

/**
 * 扩展“验证码”
 * @author YangXiaojin
 *
 */
public class CaptchaFormAuthenticationFilter extends FormAuthenticationFilter{
	
	private static Logger log = LoggerFactory.getLogger(CaptchaFormAuthenticationFilter.class);
	
	/**
	 * 验证码参数名
	 */
	public static final String DEFAULT_CAPTCHA_PARAM = "captcha";
	
	protected String captchaParam = DEFAULT_CAPTCHA_PARAM;
	
	/** 系统参数 */
	protected SystemConfig systemConfig;
	
	/**
	 * 登录帐户服务
	 */
	protected UserService userService;
	
	/**
	 * 在线登录用户缓存
	 */
	protected Cache onlineUserCache; 

	public String getCaptchaParam() {
        return captchaParam;
    }

	public void setCaptchaParam(String captchaParam) {
		this.captchaParam = captchaParam;
	}

	protected String getCaptcha(ServletRequest request) {
        return WebUtils.getCleanParam(request, getCaptchaParam());
    }
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public void setOnlineUserCache(Cache cache) {
		this.onlineUserCache = cache;
	}

	protected CaptchaUsernamePasswordToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        String captcha = getCaptcha(request);
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
                
        return new CaptchaUsernamePasswordToken(username, password, rememberMe, host,captcha);
    }
	
	/**
	 * 验证码认证
	 */
	protected void doCaptchaValidate( HttpServletRequest request ,CaptchaUsernamePasswordToken token ){
		if( isCheckCaptcha( request )){
			String captcha = (String)request.getSession().getAttribute(getKaptchaSessionKey());
			
			if(captcha!=null && !captcha.equalsIgnoreCase(token.getCaptcha()))
				throw new ShiroBadCaptchaException("验证码错误！");
		}
	}
	
	/**
	 * 验证码SessionKey
	 */
	protected String getKaptchaSessionKey( ){
		return SessionKey.KAPTCHA;
	}
	
	/**
	 * 业务验证
	 */
	protected void doBusinessValidate( User account ){
		if( account == null )
			return;
		
		if( !account.isEnabled() )
			throw new LockedAccountException("用户账号已禁用！");
		
		if( account.isExpired() )
			throw new ExpiredCredentialsException("用户账号已过期！");
		
		Calendar now = Calendar.getInstance();
		Calendar lockedTime = account.getLastLoginLockTime();
		if( lockedTime != null ){
			int minutes = 10;
			try{
				minutes = Integer.parseInt(systemConfig.getProperty(
					ConfigConstants.LOGIN_FAILURE_LOCK_MINUTES_KEY,"10"));
			}catch(Exception e){
				log.warn("登录锁定时间设置为"+minutes+"分钟。");
			}
			
			lockedTime.add(Calendar.MINUTE, minutes );
			int remain = lockedTime.get( Calendar.MINUTE ) - now.get( Calendar.MINUTE );
			if( now.compareTo(lockedTime) < 1 )
				throw new TimeLockedException("用户账号已被锁定，" +
						(remain>0?remain+"分钟后重试":"请稍后重试")+"！");
		}
	}
	
	/**
	 * 取用户信息
	 */
	protected User queryUser( String username ){
		if( username == null )
			return null;
		
		if( userService != null )
			return userService.getUser(username);
		else
			log.warn("无法查询用户信息，UserService.getUser()接口未实现。");
		
		return null;
	}
	
	/**
	 * 设置登录失败次数
	 */
	private void setFailedCount( HttpServletRequest request,int count ){
		request.getSession().setAttribute(SessionKey.LOGIN_FAILURE_COUNT,count);
	}
	
	/**
	 * 取得登录失败次数
	 */
	private int getFailedCount( HttpServletRequest request ){
		Integer failed_count = (Integer) request.getSession().getAttribute(
				SessionKey.LOGIN_FAILURE_COUNT);
		return (failed_count == null) ? 0 : failed_count;
	}
	
	/**
	 * 添加失败次数
	 */
	protected void incFailedCount( HttpServletRequest request){
		setFailedCount(request,getFailedCount( request )+1);
	}
	
	/**
	 * 清除失败次数
	 */
	protected void clearFailedCount( HttpServletRequest request){
		setFailedCount(request,0);
	}
	
	/**
	 * 锁定登录帐户
	 */
	protected boolean doLock( User user,int failed_count ){
		boolean locked = false;
		
		if( userService == null || user == null )
			return false;
		
		int times = 3;
		try{
			times = Integer.parseInt(systemConfig.getProperty(
				ConfigConstants.LOGIN_FAILURE_LOCK_TIMES_KEY,"3"));
		}catch(Exception e){
			//ignore
		}
		
		if( failed_count >= times ){
			try{
				locked = userService.lock(user.getUserId(),Calendar.getInstance());
			}catch( Exception e ){
				log.warn("LoginAccountService.lock调用异常：",e);
			}
		}
		
		return locked;
	}
	
	/**
	 * 是否做验证码检查
	 */
	protected boolean isCheckCaptcha( HttpServletRequest request ){
		boolean enabled = "true".equalsIgnoreCase(systemConfig.getProperty(
				ConfigConstants.CAPTCHA_ENABLED_KEY,"false"));
		int times = 0;
		try{
			times = Integer.parseInt(systemConfig.getProperty(
				ConfigConstants.CAPTCHA_LOGIN_IGNORE_TIMES_KEY,"0"));
		}catch(Exception e){
			//ignore
		}
		
		return enabled && getFailedCount( request ) >= times;
	}
	
	/**
	 * 写Cookie
	 */
	protected void writeCookie(HttpServletRequest request,HttpServletResponse response,
		String cookieName,String cookieValue ){
		if( cookieName == null )
			return ;
		
		try{
			if( cookieValue != null )
				cookieValue = URLEncoder.encode(cookieValue,"utf-8");
		}catch( Exception e ){}
		
		Cookie cookie = new Cookie(cookieName,cookieValue);
    	cookie.setPath( request.getContextPath() );
    	response.addCookie( cookie );
	}
    
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
    	CaptchaUsernamePasswordToken token = createToken(request, response);
        if (token == null) {
            String msg = "方法createToken()返回空值，登录操作必须获得非空的AuthenticationToken！";
            throw new IllegalStateException(msg);
        }
        
    	String ip = RequestUtil.getRealRemoteAddr((HttpServletRequest)request);
        
        //获取用户信息
        User account = queryUser( token.getUsername() );;
        try {
        	doCaptchaValidate( (HttpServletRequest)request,token );
        	doBusinessValidate( account ); //业务检查
        	
            Subject subject = getSubject(request, response);
            subject.login(token);
            
            clearFailedCount((HttpServletRequest)request );//清除失败次数
            
            //更换用户登录后，刷新页头
            Cookie cookie = new Cookie("REFRESH-HEADER","TRUE");
        	cookie.setPath( ((HttpServletRequest)request).getContextPath() );
        	((HttpServletResponse)response).addCookie( cookie );
            
        	//登录成功日志
        	userService.logLoginSuccess(account,Calendar.getInstance(),ip);
			
        	writeSession( account,(HttpServletRequest)request );
        	
        	if( isAjaxRequest((HttpServletRequest)request) ){
        		log.debug("AJAX认证成功!");
        		
        		String json = "{\"type\":\"login_success\",\"message\":\"登录认证成功\"}";
        		responseJson( response,json );
        		
        		return false;
        	}else{
        		return onLoginSuccess(token, subject, request, response);
        	}
        } catch (AuthenticationException e) {
        	if( !(e instanceof TimeLockedException) ){
	        	incFailedCount((HttpServletRequest)request);//登录失败次数
	        	
	        	this.userService.logLoginFailure(account,Calendar.getInstance()
	        			, ip, ((HttpServletRequest)request).getSession().getId());
	        	
	        	int failed_count = getFailedCount((HttpServletRequest)request);
	        	boolean locked = doLock( account,failed_count );
	        	if( locked ){
	        		log.warn("登录帐户[{}]在用户[{}]登录时被锁定。"
	        				,account.getUserId(),token.getUsername());
	        	}
        	}
        	
        	if( isAjaxRequest((HttpServletRequest)request) ){
        		log.debug("AJAX认证失败!");
        		
        		String json = "{\"type\":\"login_failure\",\"message\":\""+getAuthExpMessage(e)+"\"}";
        		responseJson( response,json );
        		
        		return false;
        	}else{
        		return onLoginFailure(token, e, request, response);
        	}
        } 
    }
    
    /**
     * 记录Session
     */
    protected void writeSession( User account ,HttpServletRequest request ){
    	//写session
    	request.getSession().setAttribute(SessionKey.LOGIN_ACCOUNT, account);
    	
    	//写在线缓存
    	if( this.onlineUserCache != null ){
    		String id = account.getUserName();
    		onlineUserCache.put(new Element(id,new OnlineUser(
    				id,request.getSession().getId()
    				,RequestUtil.getRealRemoteAddr(request)) ));
    	}
    }
    
    /**
     * 登录失败用于前端信息提示
     */
    protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
        request.setAttribute(getFailureKeyAttribute(), ae);
    }
    
    /**
     * 登录成功跳转地址
     */
    public String getSuccessUrl() {
        return super.getSuccessUrl();
    }
    
	/**
	 * 取登录地址
	 * 两情况，常规地址和CAS Server 地址
	 */
    public String getLoginUrl() {
    	if( systemConfig == null || !"true".equalsIgnoreCase(
    			systemConfig.getProperty( ConfigConstants.CAS_ENABLED_KEY, "false")) )
    		return super.getLoginUrl();
    		
        return systemConfig.getProperty( ConfigConstants.CAS_SERVER_URL_PREFIX_KEY )
        	+ "?service=" + systemConfig.getProperty( ConfigConstants.CAS_SERVICE_KEY );
    }
    
    /**
     * 是否AJAX请求
     */
    protected boolean isAjaxRequest(HttpServletRequest request ){
    	return "XMLHttpRequest".equals( request.getHeader("X-Requested-With") );
    }
    
    /**
     * AJAX请求响应未登录响应JSON数据
     */
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
    	if( isAjaxRequest((HttpServletRequest)request) ){
    		log.debug("AJAX请求无权限,需要认证!");
    		
    		String json = "{\"type\":\"login\",\"message\":\"需要登录认证\"}";
    		responseJson( response,json );
    	}else{
    		WebUtils.issueRedirect(request, response, getLoginUrl());
    	}
    }
    
    /**
     * 响应JSON数据
     */
    protected void responseJson(ServletResponse response,String json){
    	response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		try {
			response.getWriter().write(json);
		} catch (IOException e) {
			log.warn("响应异常:{}",e.getMessage());
		}
    }
    
    /**
     * 认证异常信息
     */
    protected String getAuthExpMessage( AuthenticationException e ){
    	String expMsg = null;
    	if(e instanceof UnknownAccountException || e instanceof IncorrectCredentialsException){
			expMsg="错误的用户账号或密码！";
		}else if( e instanceof ShiroBadCaptchaException ){
			expMsg="验证码错误！";
		}else if( e instanceof LockedAccountException ){
			expMsg= "用户账号已禁用！";
		}else if( e instanceof ExpiredCredentialsException ){
			expMsg= "用户账号已过期！";
		}else if( e instanceof TimeLockedException ){
			expMsg= e.getMessage();
		}else if( e instanceof OnlineUserExistException ){
			OnlineUser user = ((OnlineUserExistException)e).getUser();
			expMsg = "用户在重复登录，您被迫下线！";
			if( user != null )
				expMsg= TextFormater.format(user.getLoginTime(),"HH点mm分")
					+"，用户已在["+user.getIp()+"]登录上线！";
		}else{
			expMsg="登录异常:"+e.getMessage() ;
		}
    	
    	return expMsg;
    }

}
