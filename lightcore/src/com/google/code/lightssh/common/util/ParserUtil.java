package com.google.code.lightssh.common.util;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 类型转换
 * @author YangXiaojin
 *
 */
public class ParserUtil {
	
	/**
	 * clob parse to string
	 * @param clob java.sql.Clob
	 * @return
	 */
    public static String parseToString( Clob clob ){
        return parseToString( clob,null );
    }
	
	/**
	 * clob parse to string
	 * @param clob java.sql.Clob
	 * @return String
	 */
	public static String parseToString( Clob clob , String line_end ){
		if( clob == null )
			return null;
		
		StringBuffer sb = new StringBuffer();
		Reader is = null;
		BufferedReader br = null;
		try{
			is = clob.getCharacterStream();
			br = new BufferedReader(is);
			String line = null;
			while ( (line = br.readLine()) != null) {
				sb.append( line + (line_end==null?"":line_end));
			}
		}catch( Exception e){
			//ignore;
		}finally{
			try{ br.close(); }catch( Exception e ){}
			try{ is.close(); }catch( Exception e ){}
		}
		
		return sb.toString(); 
	}
	
	/**
	 * blob to byte array
	 * @param blob java.sql.Blob
	 * @return byte[]
	 */
    public static byte[] blobToBytes( Blob blob ){
        byte[] bytes = null;
        if( blob == null ) return bytes;

        try{
            bytes = blob.getBytes(1,(int)blob.length() );
        }catch( SQLException e ){
            e.printStackTrace();
        }

        return bytes;
    }
    
	/**
	 * parse long
	 * @param val
	 * @return
	 */
	public static Long parseLong( Object val ){
		if( val == null )
			return null;
		
		if( val instanceof Long )
			return (Long)val;
		else if( val instanceof Integer ){
			return Long.valueOf( ((Integer)val).longValue() );
		}else if( val instanceof Double ){
			return Long.valueOf( ((Double)val).longValue() );
		}else if( val instanceof BigDecimal ){
			return ((BigDecimal)val).longValue();
		}else if( val instanceof BigInteger ){
			return ((BigInteger)val).longValue();
		}else{
			try{
				return Long.valueOf( val.toString() );
			}catch(NumberFormatException e ){
				;
			}
		}
		
		return null;
	}
	
	public static int parseInt( Object val ){
		return parseInteger(val)==null?0:parseInteger(val); 
	}
	
	/**
	 * parse integer
	 * @param val
	 * @return
	 */
	public static Integer parseInteger( Object val ){
		if( val == null )
			return null;
		
		if( val instanceof Integer )
			return (Integer)val;
		else if( val instanceof Long ){
			return Integer.valueOf( ((Long) val).intValue() );
		}else if( val instanceof Double ){
			Integer.valueOf( ((Double)val).intValue() );
		}else if( val instanceof BigDecimal ){
			return ((BigDecimal)val).intValue();
		}else if( val instanceof BigInteger ){
			return ((BigInteger)val).intValue();
		}else{
			try{
				return Integer.valueOf( val.toString() );
			}catch(NumberFormatException e ){
				;
			}
		}
		
		return null;
	}
	
	/**
	 * parse to date 
	 * @param val
	 * @return
	 */
	public static Date parseDate( Object val ){
		if( val == null )
			return null;
		
		if( val instanceof Date ){
			return (Date)val;
		}else if( val instanceof Calendar ){
			return ((Calendar)val).getTime();
		}else if( val instanceof Long ){
			return new Date( ((Long)val).longValue() );
		}
		
		return null;
	}
	
	/**
	 * parse to date 
	 * @param val
	 * @return
	 */
	public static Date parseDate( String val, String format ){
		DateFormat df = new SimpleDateFormat( format );
		try{
			return df.parse( val );
		}catch( ParseException e ){
			return null;
		}
	}
	
	/**
	 * 转成字符
	 * @param val
	 * @return
	 */
	public static char parseChar( int val ){
		return (char)val;
	}

}
