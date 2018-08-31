package com.EvilNotch.lib.minecraft.content.capabilites.primitive;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public class CapInt<T> extends CapBase<T>{
	
	public int value;
	
	public CapInt(String key)
	{
		super(key);
	}

	@Override
	public void writeToNBT(T object, NBTTagCompound nbt, CapContainer c) {
		nbt.setInteger(this.key, this.value);
	}

	@Override
	public void readFromNBT(T object, NBTTagCompound nbt, CapContainer c) {
		this.value = nbt.getInteger(this.key);
	}

}
