package com.evilnotch.lib.minecraft.capability.primitive;

import com.evilnotch.lib.minecraft.capability.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapDouble<T> extends CapBase<T> {

	public double value;
	public CapDouble(String key) 
	{
		super(key);
	}
	
	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		nbt.setDouble(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getDouble(this.key);
	}
	
	@Override
	public String toString()
	{
		return this.key + ":" + this.value;
	} 

}
