package com.EvilNotch.lib.Api;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

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

}
