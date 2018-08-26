package com.EvilNotch.lib.minecraft.content.pcapabilites;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public interface IPListener {
	
	public void preSave(NBTTagCompound nbt,EntityPlayer player,PCapabilityContainer container);
	public void postSave(NBTTagCompound nbt,EntityPlayer player,PCapabilityContainer container);
	
	public void preRead(NBTTagCompound nbt,EntityPlayer player,PCapabilityContainer container);
	public void postRead(NBTTagCompound nbt,EntityPlayer player,PCapabilityContainer container);
}
