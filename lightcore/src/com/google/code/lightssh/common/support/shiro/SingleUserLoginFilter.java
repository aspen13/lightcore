package com.google.code.lightssh.common.support.shiro;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.support.shiro.OnlineUser;
import com.google.code.lightssh.common.web.SessionKey;

/**
 * 单一用户登录
 * @author YangXiaojin
 *
 */
public class SingleUserLoginFilter extends AccessControlFilter{
	
	private static Logger log = LoggerFactory.getLogger(SingleUserLoginFilter.class);
	
    public static final String DEFAULT_LOGOUT_URL = "/logout.do";
    
    private String logoutUrl = DEFAULT_LOGOUT_URL;
	
	/**
	 * 缓存
	 */
	private Cache onlineUserCache; 

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	@Resource(name="onlineUserCache")
	public void setOnlineUserCache(Cache cache) {
		this.onlineUserCache = cache;
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		if( onlineUserCache == null )
			return true;
		
		if( !(request instanceof HttpServletRequest) || 
				((HttpServletRequest)request).getSession() == null )
			return true;
		
		Subject subject = getSubject(request, response);
		if( subject == null )
			return true;
		
        String username = subject.getPrincipal().toString();
        if( StringUtils.isEmpty(username) )
        	return true;
        
        String sessionId = ((HttpServletRequest)request).getSession().getId();
      
        if( onlineUserCache.get(username) != null 
        		&& onlineUserCache.get(username).getValue() != null ){
        	OnlineUser online = (OnlineUser)onlineUserCache.get(username).getValue();
        	
			if( !sessionId.equals(online.getSessionid()) ){
				/*
				String msg = "时间["+SDF.format(online.getLoginTime())+"]，用户["
					+online.getUsername()+"]在IP["+online.getIp()+"]地址已经登录！";
				
				request.setAttribute(FormAuthenticationFilter
						.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, ouee);
				*/
				
				OnlineUserExistException ouee = new OnlineUserExistException(online);
				((HttpServletRequest)request).getSession().setAttribute(
						SessionKey.EXCEPTION_OBJECT,ouee);
				
				log.info("登录用户[{}],SESSION ID[{}]由于重复登录下线。",username,sessionId);
			 	
			 	return false;
			}
        }
        
        return true;
	}
	
    protected void redirectToLogout(ServletRequest request, ServletResponse response) throws IOException {
    	WebUtils.issueRedirect(request, response,getLogoutUrl());
    }

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		
		redirectToLogout(request,response);
		
		return false;
	}

}
