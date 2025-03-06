package com.evilnotch.lib.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtil {
	
	public static Map<Class, Map<String, Field>> f = new ConcurrentHashMap();
	
	public static Object getObject(Object instance,Class clazz,String str)
	{
		try
		{
			return getField(clazz, str).get(instance);
		}
		catch(NullPointerException p)
		{
			return null;
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
			return null;
		}
	}

	public static void setObject(Object instance,Object toset,Class clazz,String str)
	{
		try
		{
			getField(clazz, str).set(instance,toset);
		}
		catch(NullPointerException p)
		{
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Warning use at your own risk this is getting dangerous make sure you know what you are doing
	 */
	public static void setFinalObject(Object instance,Object toset,Class clazz,String strfeild)
	{
		try
		{
			Field field = getField(clazz,strfeild);
			field.setAccessible(true);
		
			Field modifiersField = Field.class.getDeclaredField("modifiers");
	    	modifiersField.setAccessible(true);
	    	modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	    	field.set(instance, toset);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	public static Field getField(Class clazz, String field)
	{
		if(clazz == null)
			return null;
		
		Map<String, Field> fields = f.get(clazz);
		if(fields == null)
		{
			fields = new HashMap();
			f.put(clazz, fields);
		}
		
		Field f = fields.get(field);
		if(f == null)
		{
			try
			{
				f = clazz.getDeclaredField(field);
				f.setAccessible(true);
			}
			catch(NoSuchFieldException e)
			{
				
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
			fields.put(field, f);//put field into cache even if it doesn't exist to prevent lag calls on tick
		}
		return f;
	}

	/**
	 * Use {@link #invoke(Method, Object, Object...)} instead get the method by using {@link #getMethod(Class, String, Class...)}
	 */
	@Deprecated
	public static void invokeMethod(Object instance, Class clazz, String mName,Object[] parameterobjs,Class[] param) 
	{
		try
		{
			Method m = clazz.getDeclaredMethod(mName, param);
			m.setAccessible(true);
			m.invoke(instance, parameterobjs);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	/**
	 * get a method safley from a class
	 * @return
	 */
	public static Method getMethod(Class clazz,String name, Class...params) 
	{
		try 
		{
			Method m = clazz.getDeclaredMethod(name, params);
			m.setAccessible(true);
			return m;
		} 
		catch (Throwable e) 
		{
		}
		return null;
	}
    public static <T> T invoke(Method method, Object instance, Object... params)
    {
    	try
    	{
    		return (T) method.invoke(instance, params);
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
	/**
	 * get a class from string safley
	 */
	public static Class classForName(String name) 
	{
		try
		{
			return Class.forName(name);
		}
		catch(Throwable t)
		{
			
		}
		return null;
	}

}
