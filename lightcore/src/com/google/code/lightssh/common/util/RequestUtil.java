package com.google.code.lightssh.common.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Aspen
 * @date 2013-3-28
 * 
 */
public class RequestUtil {
    
	/**
	 * 真实IP地址
	 */
	public static String getRealRemoteAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("Proxy-Client-IP");
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getHeader("WL-Proxy-Client-IP");
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			ip = request.getRemoteAddr();

		return ip;
	}

}
