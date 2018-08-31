package com.EvilNotch.lib.minecraft.content.capabilites.primitive;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapDouble<T> extends CapBase<T> {

	public double value;
	public CapDouble(String key) {
		super(key);
	}
	
	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) {
		nbt.setDouble(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getDouble(this.key);
	}

}
