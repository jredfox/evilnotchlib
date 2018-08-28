package com.EvilNotch.lib.minecraft.content.capabilites;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IListener<T> {
	
	public void preSave(NBTTagCompound nbt,T object,CapContainer container);
	public void postSave(NBTTagCompound nbt,T object,CapContainer container);
	
	public void preRead(NBTTagCompound nbt,T object,CapContainer container);
	public void postRead(NBTTagCompound nbt,T object,CapContainer container);

}
