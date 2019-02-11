package com.evilnotch.lib.minecraft.capability;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldSavedData;

public class WorldStorageCap extends WorldSavedData{

	public CapContainer capContainer;
	public World world;
	public ISaveHandler saveHandler;

	/**
	 * a hackish way to hook into the world class for serialization
	 */
	public WorldStorageCap(String name,CapContainer cap,World w,ISaveHandler handler) 
	{
		super(name);
		this.capContainer = cap;
		this.world = w;
		this.saveHandler = handler;
		try 
		{
			this.loadData();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		this.markDirty();
	}
	
	/**
	 * manual parse the file to memory
	 */
	public void loadData() throws IOException 
	{
		if(this.saveHandler == null)
		{
			return;
		}
		 File file1 = this.saveHandler.getMapFileFromName(this.mapName);
		 if(file1 != null && file1.exists())
		 {
			 FileInputStream fileinputstream = new FileInputStream(file1);
			 NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
			 fileinputstream.close();
			 this.readFromNBT(nbttagcompound.getCompoundTag("data"));
		 }
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		this.capContainer.readFromNBT(this.world, nbt);
		System.out.println("Parsing:" + nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) 
	{
		this.capContainer.writeToNBT(this.world, nbt);
		System.out.println("Saving:" + nbt);
		return nbt;
	}

}
