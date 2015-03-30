package com.google.code.lightssh.common.model;

import java.io.Serializable;

/**
 * Cron 表达式
 * @author YangXiaojin
 *
 */
public class CronExpression implements Serializable{

	private static final long serialVersionUID = -6566529039856227605L;
	
	public static final String FIELD_DELIMITER = " ";
	
	private static final String[] DIGITAL_0_59 =new String[]{
		"0","1","2","3","4","5","6","7","8","9","10"
		,"11","12","13","14","15","16","17","18","19"
		,"20","21","22","23","24","25","26","27","28"
		,"29","30","31","32","33","34","35","36","37"
		,"38","39","40","41","42","43","44","45","46"
		,"47","48","49","50","51","52","53","54","55"
		,"56","57","58","59"};
	
	private static final String[] DIGITAL_0_23 =new String[]{
		"0","1","2","3","4","5","6","7","8","9","10"
		,"11","12","13","14","15","16","17","18","19"
		,"20","21","22","23",};
	
	private static final String[] DIGITAL_1_31 =new String[]{
		"1","2","3","4","5","6","7","8","9","10"
		,"11","12","13","14","15","16","17","18","19","20"
		,"21","22","23","24","25","26","27","28","29","30","31"
		};
	
	private static final String[] DIGITAL_1_12_JAN_DEC =new String[]{
		"1","2","3","4","5","6","7","8","9","10","11","12"
		,"JAN","FEB","MAR","API","MAY","JUL","JUN","AUG","SEP","OCT","NOV","DEC",
	};
	
	private static final String[] DIGITAL_1_7_MON_SUN =new String[]{
		"1","2","3","4","5","6","7"
		,"MON","TUE","WED","THU","FRI","SAT","SUN"
	};
	
	private static final String[] DIGITAL_1970_2099 =new String[]{
		"1970","1971","1972","1973","1974","1975","1976","1977","1978","1979","1980"
		,"1981","1982","1983","1984","1985","1986","1987","1988","1989","1990","1991"
		,"1992","1993","1994","1995","1996","1997","1998","1999","2000","2001","2002"
		,"2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013"
		,"2014","2015","2016","2017","2018","2019","2020","2021","2022","2023","2024"
		,"2025","2026","2027","2028","2029","2030","2031","2032","2033","2034","2035"
		,"2036","2037","2038","2039","2040","2041","2042","2043","2044","2045","2046"
		,"2047","2048","2049","2050","2051","2052","2053","2054","2055","2056","2057"
		,"2058","2059","2060","2061","2062","2063","2064","2065","2066","2067","2068"
		,"2069","2070","2071","2072","2073","2074","2075","2076","2077","2078","2079"
		,"2080","2081","2082","2083","2084","2085","2086","2087","2088","2089","2090"
		,"2091","2092","2093","2094","2095","2096","2097","2098","2099"
	};
	
	/** 秒 */
	public static final Field SECONDS = new Field(true,DIGITAL_0_59,"0-59"
			,new Symbol[]{Symbol.ALL,Symbol.RANGE,Symbol.INCREMENT,Symbol.ADDITIONAL});
	
	/** 分钟 */
	public static final Field MINUTES = new Field(true,DIGITAL_0_59,"0-59"
			,new Symbol[]{Symbol.ALL,Symbol.RANGE,Symbol.INCREMENT,Symbol.ADDITIONAL});
	
	/** 小时 */
	public static final Field HOURS = new Field(true,DIGITAL_0_23,"0-23"
			,new Symbol[]{Symbol.ALL,Symbol.RANGE,Symbol.INCREMENT,Symbol.ADDITIONAL});
	
	/** 天  */
	public static final Field DAY_OF_MONTH = new Field(true,DIGITAL_1_31,"1-31"
			,new Symbol[]{Symbol.ALL,Symbol.RANGE,Symbol.INCREMENT
				,Symbol.ADDITIONAL,Symbol.NO_SPECIFIC,Symbol.LAST,Symbol.WEEKDAY});
	
	/** 月  */
	public static final Field MONTH = new Field(true,DIGITAL_1_12_JAN_DEC,"1-12,JAN-DEC"
			,new Symbol[]{Symbol.ALL,Symbol.RANGE,Symbol.INCREMENT,Symbol.ADDITIONAL});
	
	/** 星期  */
	public static final Field DAY_OF_WEEK = new Field(true,DIGITAL_1_7_MON_SUN,"1-7,MON-SUN"
			,new Symbol[]{Symbol.ALL,Symbol.RANGE,Symbol.INCREMENT
			,Symbol.ADDITIONAL,Symbol.NO_SPECIFIC,Symbol.LAST,Symbol.NTH});
	
	/** 年 */
	public static final Field YEAR = new Field(false,DIGITAL_1970_2099,"1970-2099"
			,new Symbol[]{Symbol.ALL,Symbol.RANGE,Symbol.INCREMENT,Symbol.ADDITIONAL});
	
	/**
	 * 域
	 */
	public static class Field{
		
		/**
		 * 必填
		 */
		private boolean mandatory;
		
		/**
		 * 允许的值
		 */
		private String[] allowedValues;
		
		/**
		 * 允许的值提示
		 */
		private String allowedValueHint;
		
		/**
		 * 允许的符号 
		 */
		private Symbol[] allowedSymbols;

		public Field(boolean mandatory,String[] allowedValues,String allowedValueHint,
				Symbol[] allowedSymbols) {
			super();
			this.mandatory = mandatory;
			this.allowedValues = allowedValues;
			this.allowedValueHint = allowedValueHint;
			this.allowedSymbols = allowedSymbols;
		}
		
		/**
		 * 校验值
		 */
		public String validate(String value ){
			if( mandatory && (value==null||value.trim()==null))
				throw new IllegalArgumentException("参数不能为空！");
			
			if( !(isAllowedValue(value)||isAllowedSymbol(value)) )
				throw new IllegalArgumentException("参数格式错误，只允许如下符号："
						+allowedValueHint + getAllowedSymbolHint());
			
			return value.trim();
		}
		
		/**
		 * 允许的值
		 */
		public boolean isAllowedValue(String value ){
			for(String item:allowedValues )
				if( item.equals(value) )
					return true;
			
			return false;
		}
		
		/**
		 * 允许的符号
		 */
		public boolean isAllowedSymbol(Symbol symbol){
			for(Symbol item:allowedSymbols)
				if( item.equals(symbol) )
					return true;
			
			return false;
		}
		
		/**
		 * 允许的符号
		 */
		public boolean isAllowedSymbol(String symbol){
			for(Symbol item:allowedSymbols)
				if( item.getValue().equals(symbol) )
					return true;
			
			return false;
		}
		
		/**
		 * 允许的符号提示
		 */
		public String getAllowedSymbolHint(){
			if( this.allowedSymbols == null )
				return "";
			
			StringBuffer sb = new StringBuffer();
			for(Symbol item:allowedSymbols )
				sb.append(FIELD_DELIMITER+item.getValue());
			
			return sb.toString();
		}
		
	}
	
	/**
	 * 符号 
	 */
	public enum Symbol{
		ALL("*"),
		NO_SPECIFIC("?"),
		RANGE("-"),
		ADDITIONAL(","),
		INCREMENT("/"),
		LAST("L"),
		WEEKDAY("W"),
		NTH("#"),
		;
		
		private String value;
		
		Symbol( String value ){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public String toString(){
			return this.value;
		}
	}

	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year;

	public CronExpression(String seconds,String minutes,String hours,
			String dayOfMonth,String month,String dayOfWeek,String year) {
		super();
		this.seconds = SECONDS.validate(seconds);
		this.minutes = MINUTES.validate(minutes);
		this.hours = HOURS.validate(hours);
		this.dayOfMonth = DAY_OF_MONTH.validate(dayOfMonth);
		this.month = MONTH.validate(month);
		this.dayOfWeek = DAY_OF_WEEK.validate(dayOfWeek);
		if( year != null )
			this.year = YEAR.validate(year);
	}
	
	public CronExpression(String seconds, String minutes, String hours,
			String dayOfMonth, String month, String dayOfWeek) {
		this(seconds,minutes,hours,dayOfMonth,month,dayOfWeek,null);
	}

	@Override
	public String toString() {
		return seconds + FIELD_DELIMITER
			+ minutes + FIELD_DELIMITER
			+ hours + FIELD_DELIMITER
			+ dayOfMonth + FIELD_DELIMITER
			+ month + FIELD_DELIMITER
			+ dayOfWeek + FIELD_DELIMITER
			+ ((year==null||year.trim()==null)?"":year )
		;
	}
}
