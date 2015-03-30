package com.google.code.lightssh.common.web.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpRequestWrapper overriding methods getLocale(), getLocales() to include
 * the user's preferred locale.
 */
public class LocaleRequestWrapper extends HttpServletRequestWrapper {
    private static Logger log = LoggerFactory.getLogger(LocaleRequestWrapper.class);
    
    private final Locale preferredLocale;

    /**
     * Sets preferred local to user's locale
     * @param decorated the current decorated request
     * @param userLocale the user's locale
     */
    public LocaleRequestWrapper(final HttpServletRequest decorated, final Locale userLocale) {
        super(decorated);
        preferredLocale = userLocale;
        if (null == preferredLocale) {
            log.error("preferred locale = null, it is an unexpected value!");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Locale getLocale() {
        if (null != preferredLocale) {
            return preferredLocale;
        } else {
            return super.getLocale();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration<Locale> getLocales() {
        if (null != preferredLocale) {
            List<Locale> l = Collections.list(super.getLocales());
            if (l.contains(preferredLocale)) {
                l.remove(preferredLocale);
            }
            l.add(0, preferredLocale);
            return Collections.enumeration(l);
        } else {
            return super.getLocales();
        }
    }

}
