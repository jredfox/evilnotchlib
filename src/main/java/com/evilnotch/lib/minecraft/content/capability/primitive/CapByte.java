package com.evilnotch.lib.minecraft.content.capability.primitive;

import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapByte<T> extends CapBase<T>{

	public byte value;
	public CapByte(String key) {
		super(key);
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) {
		nbt.setByte(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getByte(this.key);
	}

}
