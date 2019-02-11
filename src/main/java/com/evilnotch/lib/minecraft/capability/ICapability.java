package com.evilnotch.lib.minecraft.capability;

import net.minecraft.nbt.NBTTagCompound;

public interface ICapability<T> {
	
	public void writeToNBT(T object,NBTTagCompound nbt,CapContainer c);
	public void readFromNBT(T object,NBTTagCompound nbt,CapContainer c);

}
