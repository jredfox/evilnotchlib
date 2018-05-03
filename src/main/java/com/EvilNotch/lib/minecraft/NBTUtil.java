package com.EvilNotch.lib.minecraft;

import java.io.File;
import java.io.FileOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUtil {
	
	public static NBTTagCompound getNBTFromString(String str){
		try{
			return JsonToNBT.getTagFromJson(str);
		}catch(Exception e){}
		return null;
	}
	public static void updateNBTFile(File file, NBTTagCompound nbt) 
	{
		try{
			FileOutputStream outputstream = new FileOutputStream(file);
			CompressedStreamTools.writeCompressed(nbt,outputstream);
			outputstream.close();
		}catch(Exception e){e.printStackTrace();}
	}

}
