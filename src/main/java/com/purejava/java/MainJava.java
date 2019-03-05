package com.purejava.java;

import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.evilnotch.lib.minecraft.nbt.NBTPathApi;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args)
	{
		String str = "\\";
		JSONArray arr = new JSONArray();
		arr.add(22L);
		arr.add(22);
		arr.add((short)22);
		arr.add((byte)22);
		arr.add(22.0F);
		arr.add(22.0D);
		arr.add(true);
		arr.add("String obj");
		arr.add(new JSONObject());
		arr.add(new Integer[]{0,1,2,3,4});
		for(Object obj : arr)
			System.out.print("," + obj.getClass().getSimpleName());
		System.out.println();
		System.out.println("Done:" + arr);
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
