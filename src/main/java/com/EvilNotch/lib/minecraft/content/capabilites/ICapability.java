package com.EvilNotch.lib.minecraft.content.capabilites;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public interface ICapability<T> {
	
	public void writeToNBT(T object,NBTTagCompound nbt,CapContainer c);
	public void readFromNBT(T object,NBTTagCompound nbt,CapContainer c);

}
