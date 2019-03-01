package com.purejava.java;

import java.util.Set;

import org.json.simple.JSONObject;

import com.evilnotch.lib.minecraft.nbt.NBTPathApi;
import com.evilnotch.lib.util.line.LineArray;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args)
	{
		JSONObject json = new JSONObject();
		json.put("a", "minecraft:b");
		json.put("b", "b");
		json.put("c", 2);
		json.put("d", 40.0F);
		json.put("json", new LineArray(""));
		
		System.out.println(json.get("json").getClass());
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
