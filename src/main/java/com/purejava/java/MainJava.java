package com.purejava.java;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import com.evilnotch.lib.api.mcp.MCPMappings;
import com.evilnotch.lib.minecraft.nbt.NBTPathApi;
import com.evilnotch.lib.minecraft.util.NBTUtil;
import com.evilnotch.lib.util.simple.DummyMap;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args)
	{
		
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
