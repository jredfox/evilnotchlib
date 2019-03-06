package com.purejava.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONArrayList;
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
		JSONObject json = new JSONObject();
		List<Object> objs = new JSONArray();
		objs.add(new Integer(0));
		objs.add(new Integer(1));
		json.put("intArray", objs);
		System.out.println(json.get("intArray").getClass());
	}

}
