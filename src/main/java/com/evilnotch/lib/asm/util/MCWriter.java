package com.evilnotch.lib.asm.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import net.minecraft.launchwrapper.Launch;

public class MCWriter extends ClassWriter
{
	public MCWriter(final int flags) 
	{
		super(flags); 
	}
	
	public MCWriter(final ClassReader classReader, final int flags) 
	{
	  super(classReader,flags);
	}
	  
	@Override
	protected String getCommonSuperClass(final String type1, final String type2)
	{
		Class<?> c, d;
		ClassLoader classLoader = Launch.classLoader;
		try
		{
			c = Class.forName(ObfHelper.toDeobfClassName(type1.replace('/', '.')), false, classLoader);
			d = Class.forName(ObfHelper.toDeobfClassName(type2.replace('/', '.')), false, classLoader);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		if (c.isAssignableFrom(d))
		{
			return type1;
		}
		if (d.isAssignableFrom(c))
		{
			return type2;
		}
		if (c.isInterface() || d.isInterface())
		{
			return "java/lang/Object";
		}
		else
		{
			do
			{
				c = c.getSuperclass();
			}
			while (!c.isAssignableFrom(d));
			return ObfHelper.toObfClassName(c.getName()).replace('.', '/');
		}
	}
}