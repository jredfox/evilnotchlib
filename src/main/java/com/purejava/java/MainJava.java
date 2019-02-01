package com.purejava.java;

import java.util.Set;

import com.evilnotch.lib.minecraft.nbt.NBTPathApi;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args)
	{
		System.out.println(JavaUtil.isStringInt("-1.23"));
	}
	public static boolean compare(NBTTagCompound nbt, NBTTagCompound other,NBTPathApi.CompareType type)
	{
		Set<String> olist = other.getKeySet();
		for(String key : nbt.getKeySet())
		{
			NBTBase base = nbt.getTag(key);
			NBTBase base2 = other.getTag(key);
			//has no tags
			if(base2 == null)
			{
				return false;
			}
		}
		return false;
	}
	private static NBTTagCompound getNBTFromString(String string) 
	{
		try 
		{
			return JsonToNBT.getTagFromJson(string);
		} 
		catch (NBTException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

}
