package com.evilnotch.lib.minecraft.capability.primitive;

import com.evilnotch.lib.minecraft.capability.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapShort<T> extends CapBase<T>{
	
	public short value;
	public CapShort(String key) 
	{
		super(key);
	}
	
	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		nbt.setShort(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getShort(this.key);
	}

	@Override
	public String toString()
	{
		return this.key + ":" + this.value;
	} 
}
