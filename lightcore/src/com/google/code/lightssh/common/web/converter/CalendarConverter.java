package com.google.code.lightssh.common.web.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import ognl.DefaultTypeConverter;

/**
 * calendar converter
 * @author YangXiaojin
 *
 */
public class CalendarConverter extends DefaultTypeConverter {
	
	public static final String SIMPLE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @SuppressWarnings("rawtypes")
	public Object convertValue( Map ctx, Object o, Class toType ){
        DateFormat df = new SimpleDateFormat(SIMPLE_FORMAT);
        if( toType == Calendar.class){
        	if( o==null )
        		return null;
            String date = ( (String[])o)[0];
            try{
            	Calendar cal = Calendar.getInstance();
            	cal.setTime( df.parse( date ) );
                return cal;
            }catch( Exception e ){
                //e.printStackTrace();
            }
        }else if( toType == String.class ){
        	Calendar cal = (Calendar)o;
            return cal==null?"":df.format( cal.getTime() ) ;
        }
        return null;
    }

}
