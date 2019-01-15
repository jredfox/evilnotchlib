package com.purejava.java;

import com.evilnotch.lib.util.line.LineArray;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args)
	{
		
	}

	private static NBTTagCompound getNBTFromString(String string) 
	{
		try {
			return JsonToNBT.getTagFromJson(string);
		} catch (NBTException e) {
			e.printStackTrace();
		}
		return null;
	}

}
