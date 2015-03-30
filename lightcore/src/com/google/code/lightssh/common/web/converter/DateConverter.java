package com.google.code.lightssh.common.web.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import ognl.DefaultTypeConverter;

/**
 * date converter
 * @author YangXiaojin
 *
 */
public class DateConverter extends DefaultTypeConverter {
	
	public static final String SIMPLE_FORMAT = "yyyy-MM-dd";

    @SuppressWarnings("rawtypes")
	public Object convertValue( Map ctx, Object o, Class toType ){
        DateFormat df = new SimpleDateFormat(SIMPLE_FORMAT);
        if( toType == Date.class){
        	if( o==null )
        		return null;
            String date = ( (String[])o)[0];
            try{
                return df.parse( date );
            }catch( Exception e ){
                //e.printStackTrace();
            }
        }else if( toType == String.class ){
            Date date = (Date)o;
            return df.format( date ) ;
        }
        return null;
    }

}
