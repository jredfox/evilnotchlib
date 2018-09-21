package com.evilnotch.lib.minecraft.content.capabilites.primitive;

import com.evilnotch.lib.minecraft.content.capabilites.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapFloat<T> extends CapBase<T>{

	public float value;
	public CapFloat(String key) {
		super(key);
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) {
		nbt.setFloat(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) 
	{
		this.value = nbt.getFloat(this.key);
	}
	
}
