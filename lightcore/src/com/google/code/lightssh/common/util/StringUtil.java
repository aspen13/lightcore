package com.google.code.lightssh.common.util;


/**
 * 字符串实用类
 *
 */
public class StringUtil {
	
    /** Constant representing the empty string, equal to &quot;&quot; */
    public static final String EMPTY_STRING = "";

    /** Constant representing the default delimiter character (comma), equal to <code>','</code> */
    public static final char DEFAULT_DELIMITER_CHAR = ',';

    /** Constant representing the default quote character (double quote), equal to '&quot;'</code> */
    public static final char DEFAULT_QUOTE_CHAR = '"';
	
    /**
     * 清除空格
     */
    public static String clean(String in) {
        String out = in;

        if (in != null) {
            out = in.trim();
            if (out.equals(EMPTY_STRING)) {
                out = null;
            }
        }

        return out;
    }
    
    /**
     * 是否存在字符
     */
    public static boolean hasText( String in ){
    	return clean( in ) != null;
    }

}
