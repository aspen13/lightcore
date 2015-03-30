package com.google.code.lightssh.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射实用类
 * @author Aspen
 *
 */
public class ReflectionUtil {
	
    public static final String GETTER_PREFIX = "get";
    public static final String GETTER_PREFIX2 = "is";
    public static final String SETTER_PREFIX = "set";

    /**
     * 通过getter和setter方法取对象属性
     */
	public static Map<String,String> propertiesByGetterAndSetter( Object obj ){
		if( obj == null )
			return null;
		
		Method[] methods = obj.getClass().getMethods();
		Map<String,Method> setterMap = new HashMap<String,Method>();
		Map<String,Method> getterMap = new HashMap<String,Method>();
        for( Method item:methods ){
        	String property = item.getName();
    		if( property.indexOf( SETTER_PREFIX ) != -1){
    			setterMap.put(property.substring(3), item);
    		}else if(  property.indexOf( GETTER_PREFIX ) != -1){
    			getterMap.put(property.substring(3), item);
    		}else if(  property.indexOf( GETTER_PREFIX2 ) != -1){
    			getterMap.put(property.substring(2), item);
    		}
        }
        
        Map<String,String> result = new HashMap<String,String>( );
        for( String key:setterMap.keySet()){
        	if( getterMap.get(key) != null ){
        		key = key.replaceFirst(key.substring(0,1), key.substring(0,1).toLowerCase());
        		result.put( key ,key);
        	}
        }
        
        return result;
	}
    
	/**
	 * 对象属性赋值
	 * @param from 原对象
	 * @param to 目标对象
	 */
	public static void assign( Object from,Object to ){
		if( from == null || to == null )
			return;
		Method[] methods = from.getClass().getMethods();
		Map<String,Method> setterMap = new HashMap<String,Method>();
		Map<String,Method> getterMap = new HashMap<String,Method>();
        for( Method item:methods ){
        	String property = item.getName();
    		if( property.indexOf( SETTER_PREFIX ) != -1){
    			setterMap.put(property.substring(3), item);
    		}else if(  property.indexOf( GETTER_PREFIX ) != -1){
    			getterMap.put(property.substring(3), item);
    		}else if(  property.indexOf( GETTER_PREFIX2 ) != -1){
    			getterMap.put(property.substring(2), item);
    		}
        }
        
        if( setterMap.isEmpty() )
        	return;
        
        for( String key:setterMap.keySet()){
        	Method setter = setterMap.get(key);
        	Method getter = getterMap.get(key);
        	if( setter != null && getter != null )
				try {
					setter.invoke(to, getter.invoke(from));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
        }
	}
	
    /**
     * 取对象中的属性值
     */
    @SuppressWarnings("rawtypes")
	public static Object reflectGetValue( Object model, String property ){
        Class clazz = model.getClass();
        Method[] methods = clazz.getMethods();
        Object result = null;

        for( Method item:methods ){
            try {
                if( item.getName().equalsIgnoreCase( GETTER_PREFIX + property ) ){
                    result = item.invoke( model );
                    break;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 设置对象的属性值
     */
    @SuppressWarnings("rawtypes")
	public static void reflectSetValue( Object model, String property, Object value ){
        Class clazz = model.getClass();
        Method[] methods = clazz.getMethods();

        for( Method item:methods ){
            try {
                if( item.getName().equalsIgnoreCase( SETTER_PREFIX + property ) ){
                    item.invoke( model , value );
                    return;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }


}
