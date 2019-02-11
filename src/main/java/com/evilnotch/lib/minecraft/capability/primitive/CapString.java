package com.evilnotch.lib.minecraft.capability.primitive;

import com.evilnotch.lib.minecraft.capability.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapString<T> extends CapBase<T>{
	
	public String value = "";
	public CapString(String key)
	{
		super(key);
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		nbt.setString(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getString(this.key);
	}
	
	@Override
	public String toString()
	{
		return this.key + ":" + this.value;
	}

}
