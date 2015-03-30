package com.google.code.lightssh.common.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 数据格式化
 */
public class TextFormater {
    public static final String NULL_DATA_VALUE = "null";
    public static final String EMPTY_DATA_VALUE = "";
    
    /** 大写数字 */
    private static final String[] CN_NUMBERS = { "零", "壹", "贰", "叁", "肆", "伍", "陆","柒", "捌", "玖" };
    /** 整数部分的单位 */
    private static final String[] CN_INTEGER_UNIT = { "元", "拾", "佰", "仟", "万", "拾", "佰","仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟" };
    /** 小数部分的单位 */
    private static final String[] CN_DECIMAL_UNIT = { "角", "分", "厘" };


    /**
     * 时间格式化
     * @param date
     * @param pattern
     * @return
     */
    public static String format( Date date ,String pattern ){
        if( date == null )
            return EMPTY_DATA_VALUE;

        if( pattern == null || "".equals( pattern ) )
            return date.toString();

        try{
            DateFormat df = new SimpleDateFormat( pattern );
            return df.format( date );
        }catch( Exception e ){
            return date.toString();
        }
    }

    /**
     * 时间格式化
     * @param date
     * @return
     */
     public static String format( Date date ){
         return format( date , "yyyy-MM-dd" );
     }
     
     /**
      * 字符串截取
      * @param val 字符串
      * @param maxLen 最大字符
      * @return String
      */
     public static String format( String val, int maxLen , boolean ellipsis ){
    	 if( val == null || "".equals( val ) || val.trim() == null )
    		 return val;
    	 
    	 val = val.trim();
    	 boolean trim = maxLen > 0 && (val.length() > maxLen) ;    	 
    	 if( trim ){
    		 val = val.substring(0, Math.min(maxLen, val.length()) );
    		 if( ellipsis ) val += "...";
    	 }
 
    	 return val;
     }
     
     /**
      * 字符串截取
      * @param val 字符串
      * @param maxLen 最大字符
      * @return String
      */
     public static String format( String val, int maxLen  ){
    	 return format( val, maxLen ,true );
     }
     
     /**
      * 格式化
      * @param date Date
      * @param field 年月日类型
      * @return
      */
     public static String formatMonth( Date date ){
    	 Calendar cal = Calendar.getInstance();
    	 cal.setTime(date);
    	 int month = cal.get( Calendar.MONTH );
    	 String[] chineseMonths = {"一","二","三","四","五","六","七","八","九","十","十一","十二" };
    	 return chineseMonths[month] + "月";
     }
     
     /**
      * 数字格式化
      * @param val
      * @param pattern
      * @return
      */
     public static String format( int val ,String pattern ){
    	 DecimalFormat df = new DecimalFormat( pattern );
    	 return df.format(val);
     }

     /**
      * 数字格式化
      * @param val
      * @param pattern
      * @return
      */
     public static String format( double val ,String pattern ){
    	 DecimalFormat df = new DecimalFormat( pattern );
    	 return df.format(val);
     }
     
     /**
      * 格式化显示大写人民币
      * @param number 数字
      * @return
      */
     public static String formatCNY( Number number ){
    	 String numberTxt = BigDecimal.valueOf(number.doubleValue()).toPlainString();
    	 String integerPart;//整数部分数字
    	 String decimalPart;//小数部分数字

		if (numberTxt.indexOf(".") > 0) {
			integerPart = numberTxt.substring(0, numberTxt.indexOf("."));
			decimalPart = numberTxt.substring(numberTxt.indexOf(".") + 1);
		} else if (numberTxt.indexOf(".") == 0) {
			integerPart = "";
			decimalPart = numberTxt.substring(1);
		} else {
			integerPart = numberTxt;
			decimalPart = "";
		}
		
		//去掉整数前面零
		integerPart = integerPart.replaceAll("^[0]*",""); 
		//去掉小数最后零
		decimalPart = decimalPart.replaceAll("[0]*$",""); 
		
		if( integerPart.length() > CN_INTEGER_UNIT.length )
			return numberTxt;
    	    
		StringBuffer cny = new StringBuffer("");
		//整数部分
		int int_len = integerPart.length();
		for( int i = 0; i < int_len; i++ ){
			String key = "";
			int num = Integer.valueOf(integerPart.substring(i,i+1));
			boolean isZero = num==0;
			boolean isMust5= (int_len>4)&& Integer.parseInt(integerPart.substring(
					(int_len>8?int_len-8:0),int_len-4)) > 0;
			if( isZero ){
				if((int_len-i)==13)// 万(亿)(必填)
					key = CN_INTEGER_UNIT[4];
				else if ((int_len - i) == 9)// 亿(必填)
					key = CN_INTEGER_UNIT[8];
				else if ((int_len - i) == 5 && isMust5)// 万(不必填)
					key = CN_INTEGER_UNIT[4];
				else if ((int_len - i) == 1)// 元(必填)
					key = CN_INTEGER_UNIT[0];
				
				// 0遇非0时补零，不包含最后一位
				if ((int_len - i) > 1 
						&& Integer.valueOf(integerPart.substring(i+1,i+2)) != 0)
					key += CN_NUMBERS[0];
			}
			cny.append(isZero?key:(CN_NUMBERS[num]+CN_INTEGER_UNIT[int_len - i - 1]));
		}
		
		//小数整数
		int dec_len = decimalPart.length();
		for( int i = 0; i < dec_len; i++ ){
			if( i==3)
				break;
			int num = Integer.valueOf(decimalPart.substring(i,i+1));
			cny.append(num==0?"":(CN_NUMBERS[num] + CN_DECIMAL_UNIT[i]));
		}
		
		if( cny.length() == 0 )
			cny.append(CN_NUMBERS[0]+CN_INTEGER_UNIT[0]);
		
		return cny.toString();
     }
     
 	/**
 	 * 格式化时间
 	 */
 	public static String timeFormat( long timeMillis ){
 		if( timeMillis <= 0 )
 			return "00:00:00.000";
 		
 		long hour = (int)(timeMillis/(60*60*1000));
 		long minute = (int)((timeMillis - hour*60*60*1000 )/(60*1000));
 		long secound = (int)((timeMillis - hour*60*60*1000 - minute*60*1000)/1000);
 		long millis = (timeMillis % 1000);
 		
 		return TextFormater.format(hour, "00") 
 			+ ":" + TextFormater.format(minute, "00")
 			+ ":" + TextFormater.format(secound, "00")
 			+ "." + TextFormater.format(millis, "000");
 	}
}
