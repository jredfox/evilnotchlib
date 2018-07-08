package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.ArrayList;

import com.EvilNotch.lib.util.ICopy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;

public interface ICapability{
	
	public void readFromNBT(NBTTagCompound nbt,EntityPlayer player,CapabilityContainer container);
	public void writeToNBT(NBTTagCompound nbt,EntityPlayer player,CapabilityContainer container);
}
