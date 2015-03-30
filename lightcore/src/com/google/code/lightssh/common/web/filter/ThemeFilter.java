package com.google.code.lightssh.common.web.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.google.code.lightssh.common.util.StringUtil;
import com.google.code.lightssh.common.web.SessionKey;

/**
 * theme filter
 * 用于改变样式
 * @author YangXiaojin
 *
 */
public class ThemeFilter extends OncePerRequestFilter{
	
	public static final String THEME_PARAM_NAME = "theme";
	public static final String INIT_PARAM_NAME = "allowed";
	public static final Set<String> ALLOWED_THEME_SET = new HashSet<String>();
	static{
		ALLOWED_THEME_SET.add( "default" ); 
	}
	
	@Override
	protected void initFilterBean() throws ServletException {
		String allowed = getFilterConfig().getInitParameter( INIT_PARAM_NAME );
		if( StringUtil.clean( allowed ) == null )
			return;
		
		for( String theme:allowed.split(","))
			if( StringUtil.clean(theme) != null )
				ALLOWED_THEME_SET.add(StringUtil.clean(theme));
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		String preferred = request.getParameter( THEME_PARAM_NAME );
		preferred = StringUtil.clean(preferred);
		
        if ( preferred != null && ALLOWED_THEME_SET.contains(preferred)) {
        	response.addCookie( new Cookie(THEME_PARAM_NAME,preferred));
        	request.getSession().setAttribute(SessionKey.PREFERRED_THEME, preferred);
        }
        
        chain.doFilter(request, response );
	}

}
