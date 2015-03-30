package com.google.code.lightssh.common.web.converter;

import java.math.BigDecimal;
import java.util.Map;

import ognl.DefaultTypeConverter;

/**
 * BigDecimal Converter
 * @author YangXiaojin
 *
 */
public class BigDecimalConverter extends DefaultTypeConverter {
	
    @SuppressWarnings("rawtypes")
	public Object convertValue( Map ctx, Object o, Class toType ){
        if( toType == BigDecimal.class){
        	if( o==null )
        		return null;
            String val = ( (String[])o)[0];
            try{
                return new BigDecimal( val );
            }catch( Exception e ){
                //e.printStackTrace();
            }
        }else if( toType == String.class ){
        	BigDecimal value = (BigDecimal)o;
            return value.toPlainString();
        }
        return null;
    }

}
