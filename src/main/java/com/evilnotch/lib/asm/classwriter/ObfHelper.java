package com.evilnotch.lib.asm.classwriter;

import com.evilnotch.lib.asm.FMLCorePlugin;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class ObfHelper
{
	public static Boolean obfuscated = null;

	/**
	 * @return Whether or not the current environment contains obfuscated Minecraft code
	 */
	public static boolean isObfuscated()
	{
		if (obfuscated == null)
		{
			obfuscated = FMLCorePlugin.isObf;
		}
		return obfuscated;
	}

	/**
	 * Deobfuscates an obfuscated class name if {@link #isObfuscated()}.
	 */
	public static String toDeobfClassName(String obfClassName)
	{
		if (!isObfuscated())
			return forceToDeobfClassName(obfClassName);
		else
			return obfClassName.replace('.', '/');
	}

	/**
	 * Obfuscates a deobfuscated class name if {@link #isObfuscated()}.
	 */
	public static String toObfClassName(String deobfClassName)
	{
		if (isObfuscated())
			return forceToObfClassName(deobfClassName);
		else
			return deobfClassName.replace('.', '/');
	}
	
	/**
	 * Deobfuscates an obfuscated class name regardless of {@link #isObfuscated()}.
	 */
	public static String forceToDeobfClassName(String obfClassName)
	{
		return FMLDeobfuscatingRemapper.INSTANCE.map(obfClassName.replace('.', '/'));
	}

	/**
	 * Obfuscates a deobfuscated class name regardless of {@link #isObfuscated()}.
	 */
	public static String forceToObfClassName(String deobfClassName)
	{
		return FMLDeobfuscatingRemapper.INSTANCE.unmap(deobfClassName.replace('.', '/'));
	}
}