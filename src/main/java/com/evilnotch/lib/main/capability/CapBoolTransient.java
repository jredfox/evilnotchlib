package com.evilnotch.lib.main.capability;

import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.primitive.CapBoolean;

import net.minecraft.nbt.NBTTagCompound;

public class CapBoolTransient<T> extends CapBoolean<T>{

	public CapBoolTransient(String key) 
	{
		super(key);
	}
	
	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		
	}
	
	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		
	}

}
