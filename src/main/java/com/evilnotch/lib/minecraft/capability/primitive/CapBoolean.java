package com.evilnotch.lib.minecraft.capability.primitive;

import com.evilnotch.lib.minecraft.capability.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapBoolean<T> extends CapBase<T>{
	
	public boolean value = false;
	public CapBoolean(String key)
	{
		super(key);
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		nbt.setBoolean(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getBoolean(this.key);
	}
	
	@Override
	public String toString()
	{
		return this.key + ":" + this.value;
	} 

}
