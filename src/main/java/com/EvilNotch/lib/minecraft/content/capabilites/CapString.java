package com.EvilNotch.lib.minecraft.content.capabilites;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapString<T> implements ICapability<T>{
	
	public String str = null;
	
	public CapString(String s)
	{
		this.str = s;
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) {
		
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		
	}
	@Override
	public String toString(){return this.str;}

}
