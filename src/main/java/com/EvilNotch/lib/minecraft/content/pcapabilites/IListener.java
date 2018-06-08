package com.EvilNotch.lib.minecraft.content.pcapabilites;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface IListener {
	
	public void preSave(NBTTagCompound nbt,EntityPlayer player,CapabilityContainer container);
	public void postSave(NBTTagCompound nbt,EntityPlayer player,CapabilityContainer container);
	
	public void preRead(NBTTagCompound nbt,EntityPlayer player,CapabilityContainer container);
	public void postRead(NBTTagCompound nbt,EntityPlayer player,CapabilityContainer container);
}
