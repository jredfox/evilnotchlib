package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.evilnotch.lib.minecraft.nbt.NBTPathApi;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {
	
	public static NBTTagCompound getNBTFromString(String str)
	{
		if(JavaUtil.isStringNullOrEmpty(str))
			return null;
		try
		{
			return JsonToNBT.getTagFromJson(str);
		}
		catch(Exception e)
		{
			
		}
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
					System.err.println("unable to close output stream for:" + file);
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
					System.err.println("unable to close input stream for:" + file);
				}
			}
		}
		return nbt;
	}
	
	/**
	 * update nbt file checks
	 */
	public static void updateNBTFileSafley(File file, NBTTagCompound nbt) 
	{
		if(!file.exists())
		{
			try 
			{
				
				File parent = file.getParentFile();
				if(!parent.exists())
					parent.mkdirs();
				file.createNewFile();
			} 
			catch (Throwable e) 
			{
				e.printStackTrace();
				return;
			}
		}
		updateNBTFile(file,nbt);
	}
	
	public static NBTTagCompound getFileNBTSafley(File file) 
	{	
		return file.exists() ? getFileNBT(file) : null;
	}
	
	public static NBTTagList getNBTTagListSafley(NBTTagCompound tag, String name, int type) 
	{
		if(tag == null)
			return new NBTTagList();
		return tag.getTagList(name, type);
	}
	
	/**
	 * this process is heavy don't use on tick cache at least one nbtpath api and both if at all possible
	 * @return
	 */
	public static boolean equalsLogic(NBTPathApi.CompareType type, NBTTagCompound base,NBTTagCompound toCompare)
	{
		//no need to decompile it if all they are comparing is equals
		if(type == NBTPathApi.CompareType.equals)
			return base.equals(toCompare);
		
		NBTPathApi apiBase = new NBTPathApi(base);
		NBTPathApi apiCompare = new NBTPathApi(toCompare);
		return apiBase.equalsLogic(type, apiCompare);
	}
	
	/**
	 * merge nbt at a deep level unlike vanilla is an actual merge and will keep tags from both sides if they are tagcompounds and conflict with names
	 * if you need specific tags removed you need to implement this yourself this is simply a deep merging
	 */
	public static void merge(NBTTagCompound base,NBTTagCompound toCompare)
	{
		NBTPathApi apiBase = new NBTPathApi(base);
		NBTPathApi apiCompare = new NBTPathApi(toCompare);
		apiBase.merge(apiCompare);
	}
	
	/**
	 * unlike merge doesn't override the other tags compounds if they have the path only if non existent at a deep level of comparison
	 */
	public static void copySafley(NBTTagCompound base,NBTTagCompound toCompare)
	{
		NBTPathApi apiBase = new NBTPathApi(base);
		NBTPathApi apiCompare = new NBTPathApi(toCompare);
		apiBase.copySafley(apiCompare);
	}

	public static NBTTagCompound getOrCreateNBT(ItemStack stack) 
	{
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt == null ? new NBTTagCompound() : nbt;
	}

	public static NBTTagCompound copyNBT(NBTTagCompound nbt) 
	{
		return nbt != null ? nbt.copy() : null;
	}

}
