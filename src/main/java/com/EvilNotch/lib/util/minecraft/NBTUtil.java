package com.EvilNotch.lib.util.minecraft;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUtil {
	
	public static NBTTagCompound getNBTFromString(String str){
		try{
			return JsonToNBT.getTagFromJson(str);
		}catch(Exception e){}
		return null;
	}

}
