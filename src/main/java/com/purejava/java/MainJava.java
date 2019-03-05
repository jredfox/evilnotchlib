package com.purejava.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

import com.evilnotch.lib.minecraft.nbt.NBTPathApi;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args)
	{
		JSONObject json = new JSONObject();
		json.put("intArray", new Boolean[]{true,false});
		System.out.println(json.get("intArray").getClass());
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
