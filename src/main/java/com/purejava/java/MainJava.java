package com.purejava.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONArrayList;
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
		json.putStaticArray("ints", new int[]{0,1,2,3,4,5,6,7,8,9,10});
	}

}
