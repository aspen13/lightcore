package com.google.code.lightssh.common.web.action;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.web.SessionKey;
import com.opensymphony.xwork2.ActionSupport;

/**
* Base Action
*/
@SuppressWarnings("serial")
public class BaseAction extends ActionSupport
    implements ServletRequestAware{
	
	private static Logger log = LoggerFactory.getLogger(CrudAction.class);

    /**
    * Request 
    */
    protected HttpServletRequest request;
   
    //-- constructors ----------------------------------------------------------
       
    /**
    * default constructor
    */
    public BaseAction( ){
    }
   
    //-- util methods ----------------------------------------------------------

    public void setServletRequest(HttpServletRequest request) {
        this.request= request;       
    }
   
    /**
    * http session
    */
    protected HttpSession getSession() {
        return request.getSession( );
    }
    
   
    /**
     * 添加提示信息到Session用于页面显示
     * @param msg
     */
     @SuppressWarnings("unchecked")
     protected void saveMessage( String sessionKey, String msg) {
         List<String> messages = (List<String>) getSession().getAttribute( sessionKey );
         if (messages == null) {
             messages = new ArrayList<String>();
         }
         messages.add( msg );
         getSession().setAttribute( sessionKey, messages);
     }
     
     protected void saveSuccessMessage(String msg) {
         saveMessage( SessionKey.SUCCESS_MESSAGES, msg );
     }
     
     protected void saveErrorMessage(String msg) {
     	saveMessage( SessionKey.ERROR_MESSAGES, msg );
     }
    
    protected boolean isPost( ){
    	return "POST".equalsIgnoreCase( request.getMethod() );
    }
    
    protected boolean isGet( ){
    	return "GET".equalsIgnoreCase( request.getMethod() );
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
		}catch( Exception e ){
			log.error( e.getMessage() );
		}
		
		Cookie cookie = new Cookie(cookieName,cookieValue);
    	cookie.setPath( request.getContextPath() );
    	response.addCookie( cookie );
	}
	
	/**
	 * 真实IP地址
	 */
	protected String getIpAddr( ) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
    
    /**
     * default execute method
     */
    public String execute( ){
    	return SUCCESS;
    }

}