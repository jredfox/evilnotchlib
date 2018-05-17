package com.EvilNotch.lib.minecraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {
	
	public static NBTTagCompound getNBTFromString(String str){
		try{
			return JsonToNBT.getTagFromJson(str);
		}catch(Exception e){}
		return null;
	}
	/**
	 * update a nbt file with no checks if file exists
	 */
	public static void updateNBTFile(File file, NBTTagCompound nbt) 
	{
		FileOutputStream outputstream = null;
		try
		{
			outputstream = new FileOutputStream(file);
			CompressedStreamTools.writeCompressed(nbt,outputstream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(outputstream != null)
			{
				try 
				{
					outputstream.close();
				} 
				catch (IOException e) 
				{
					System.out.println("unable to close output stream for:" + file);
				}
			}
		}
	}
	/**
	 * get nbt from a file with no checks
	 */
	public static NBTTagCompound getFileNBT(File file) 
	{
		FileInputStream stream = null;
		NBTTagCompound nbt = null;
		try
		{
			stream = new FileInputStream(file);
			nbt = CompressedStreamTools.readCompressed(stream);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			nbt = null;
		}
		finally
		{
			if(stream != null)
			{
				try 
				{
					stream.close();
				} 
				catch (IOException e) 
				{
					System.out.println("unable to close input stream for:" + file);
				}
			}
		}
		return nbt;
	}
	public static float getRotationYaw(NBTTagCompound nbt)
	{
		NBTTagList nbttaglist3 = nbt.getTagList("Rotation", 5);
		return nbttaglist3.getFloatAt(0);
	}
	public static float getRotationPitch(NBTTagCompound nbt)
	{
		NBTTagList nbttaglist3 = nbt.getTagList("Rotation", 5);
		return nbttaglist3.getFloatAt(1);
	}
	
	/**
	 * update nbt file checks
	 */
	public static void updateNBTFileSafley(File file, NBTTagCompound nbt) 
	{
		if(!file.exists())
		{
			try {
				
				File parent = file.getParentFile();
				if(!parent.exists())
					parent.mkdirs();
				file.createNewFile();
			} catch (Throwable e) {
				e.printStackTrace();
				return;
			}
		}
		updateNBTFile(file,nbt);
	}
	public static NBTTagCompound getFileNBTSafley(File file) 
	{
		if(!file.exists())
		{
			try
			{
				File parent = file.getParentFile();
				if(!parent.exists())
					parent.mkdirs();
				file.createNewFile();
				updateNBTFile(file, new NBTTagCompound());
				return new NBTTagCompound();//optimized way
			}
			catch(Throwable t)
			{
				t.printStackTrace();
				return null;
			}
		}
		return getFileNBT(file);
	}

}
