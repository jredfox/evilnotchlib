package com.evilnotch.lib.minecraft.content.capability;

import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;

import net.minecraft.nbt.NBTTagCompound;

public interface IListener<T> {
	
	public void preSave(NBTTagCompound nbt,T object,CapContainer c);
	public void postSave(NBTTagCompound nbt,T object,CapContainer c);
	
	public void preRead(NBTTagCompound nbt,T object,CapContainer c);
	public void postRead(NBTTagCompound nbt,T object,CapContainer c);

}
