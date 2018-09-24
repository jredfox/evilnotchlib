package com.evilnotch.lib.minecraft.content.capability.primitive;

import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapLong<T> extends CapBase<T>{

	public long value;
	public CapLong(String key) {
		super(key);
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) {
		nbt.setLong(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getLong(this.key);
	}
}
