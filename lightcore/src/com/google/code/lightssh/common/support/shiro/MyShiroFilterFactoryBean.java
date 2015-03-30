package com.google.code.lightssh.common.support.shiro;

import java.util.Map;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;

public class MyShiroFilterFactoryBean extends ShiroFilterFactoryBean{
	
	private static transient final Logger log = LoggerFactory.getLogger(MyShiroFilterFactoryBean.class);
	
	public static final String URLS_SECTION_TYPE_INI = "INI";
	public static final String URLS_SECTION_TYPE_DB = "DB";
	public static final String URLS_SECTION_TYPE_INIANDDB = "INI_DB";
	
	/**
	 * 正则表达式路径匹配
	 */
	protected boolean regexPathMatcher = false;
	
	/**
	 * URLs Section类型
	 */
	protected String urlsSectionType = URLS_SECTION_TYPE_INI;
	
	private UrlsSectionService urlsSectionService;
	
    public boolean isRegexPathMatcher() {
		return regexPathMatcher;
	}

	public void setRegexPathMatcher(boolean regexPathMatcher) {
		this.regexPathMatcher = regexPathMatcher;
	}

	public String getUrlsSectionType() {
		return urlsSectionType;
	}

	public void setUrlsSectionType(String urlsSectionType) {
		this.urlsSectionType = urlsSectionType;
	}

	public void setUrlsSectionService(UrlsSectionService urlsSectionService) {
		this.urlsSectionService = urlsSectionService;
	}

	protected AbstractShiroFilter createInstance() throws Exception {

        log.debug("Creating Shiro Filter instance.");

        SecurityManager securityManager = getSecurityManager();
        if (securityManager == null) {
            String msg = "SecurityManager property must be set.";
            throw new BeanInitializationException(msg);
        }

        if (!(securityManager instanceof WebSecurityManager)) {
            String msg = "The security manager does not implement the WebSecurityManager interface.";
            throw new BeanInitializationException(msg);
        }

        FilterChainManager manager = createFilterChainManager();

        //路径匹配
        MyPathMatchingFilterChainResolver chainResolver = new MyPathMatchingFilterChainResolver(regexPathMatcher);
        chainResolver.setFilterChainManager(manager);

        return new SpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
    }
	
	/**
	 * 扩展加载其它Urls Section
	 */
    public void setFilterChainDefinitions(String definitions) {
        super.setFilterChainDefinitions(definitions);
        addUrlsSection();
    }
	
    /**
     * 加载其它Urls Sectiono数据
     */
    public void addUrlsSection( ){
    	if( URLS_SECTION_TYPE_DB.equals( urlsSectionType ) 
    			|| URLS_SECTION_TYPE_INIANDDB.equals( urlsSectionType ) ){
    		if( urlsSectionService != null ){
    			Map<String,String> section = getFilterChainDefinitionMap();
    			Map<String,String> loadSec = urlsSectionService.loadUrlsSection();
    			if( loadSec == null || loadSec.isEmpty() )
    				log.info("Shiro Urls Section 无数据加载！");
    			else{
    				if( URLS_SECTION_TYPE_DB.equals( urlsSectionType ) )
    					section.clear();
    				
    				section.putAll( loadSec );
    				log.info("加载Shiro Urls Section[{}]条，当前结果[{}]条！",loadSec.size(),section.size() );
    			}
        	}else{
        		log.warn("urlsSectionType=[{}],服务接口[{}]未实现注入！",
        				urlsSectionType,"UrlsSectionService");
        	}
    	}
    }
    
    private static final class SpringShiroFilter extends AbstractShiroFilter {

        protected SpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
            super();
            if (webSecurityManager == null) {
                throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
            }
            setSecurityManager(webSecurityManager);
            if (resolver != null) {
                setFilterChainResolver(resolver);
            }
        }
    }

}
