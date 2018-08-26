package com.EvilNotch.lib.minecraft.content.capabilites;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;
import com.EvilNotch.lib.minecraft.content.pcapabilites.PCapabilityContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IListener {
	
	public void preSave(NBTTagCompound nbt,ICapProvider player,CapContainer container);
	public void postSave(NBTTagCompound nbt,ICapProvider player,CapContainer container);
	
	public void preRead(NBTTagCompound nbt,ICapProvider player,CapContainer container);
	public void postRead(NBTTagCompound nbt,ICapProvider player,CapContainer container);

}
