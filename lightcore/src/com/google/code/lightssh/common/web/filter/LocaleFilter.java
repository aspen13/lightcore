package com.google.code.lightssh.common.web.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.OncePerRequestFilter;

import com.google.code.lightssh.common.web.SessionKey;


/**
 * 国际化切换
 * @author YangXiaojin
 *
 */
public class LocaleFilter extends OncePerRequestFilter{
	 
	public static final String LOCALE_PARAM_NAME = "locale";

	@Override
	public void destroy() {
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		String locale = request.getParameter( LOCALE_PARAM_NAME );
        Locale preferredLocale = null;

        if (locale != null) {
            int indexOfUnderscore = locale.indexOf("_");
            if (indexOfUnderscore != -1) {
                String language = locale.substring(0, indexOfUnderscore);
                String country = locale.substring(indexOfUnderscore + 1);
                preferredLocale = new Locale(language, country);
            } else {
                preferredLocale = new Locale(locale);
            }
        }
		
        HttpSession session = request.getSession(false);

        if (session != null) {
            if (preferredLocale == null) {
                preferredLocale = (Locale) session.getAttribute(SessionKey.PREFERRED_LOCALE);
            } else {
                session.setAttribute(SessionKey.PREFERRED_LOCALE, preferredLocale);
            }

            if (preferredLocale != null && !(request instanceof LocaleRequestWrapper)) {
                request = new LocaleRequestWrapper(request, preferredLocale);
                //LocaleContextHolder.setLocale(preferredLocale);
            }
        }
        
        chain.doFilter(request, response );
	}

}
