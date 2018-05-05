package com.example.examplemod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.gui.GuiScreen;

public class ReflectionUtil {
	
	public static Object getObject(Object instance,Class clazz,String str)
	{
		try{
			return ReflectionHelper.findField(clazz, str).get(instance);
		}catch(Exception e){e.printStackTrace(); return null;}
	}
	
	public static void setObject(Object instance,Object toset,Class clazz,String str)
	{
		try{
			ReflectionHelper.findField(clazz, str).set(instance,toset);
		}catch(Exception e){e.printStackTrace();}
	}
	/**
	 * Warning use at your own risk this is getting dangerous make sure you know what you are doing
	 */
	public static void setFinalObject(Object instance,Object toset,Class clazz,String strfeild)
	{
		try{
		Field field = ReflectionHelper.findField(clazz,strfeild);
		field.setAccessible(true);

	    Field modifiersField = Field.class.getDeclaredField("modifiers");
	    modifiersField.setAccessible(true);
	    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	    field.set(instance, toset);
		}catch(Throwable t){t.printStackTrace();}
	}

	public static void invokeMethod(Object instance, Class<? extends GuiScreen> clazz, String mName,Object[] parameterobjs,Class[] param) 
	{
		try
		{
			Method m = clazz.getMethod(mName, param);
			m.invoke(instance, parameterobjs);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
	}

}
