package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CapabilityContainer {
	
	public HashMap<ResourceLocation,ICapability> capabilities = null;
	public ArrayList<IListener> listeners;
	public ArrayList<ITick> ticks;
	
	public CapabilityContainer(){
		this.capabilities = new HashMap();
		this.listeners = new ArrayList();
		this.ticks = new ArrayList();
	}
	
	public ICapability getCapability(ResourceLocation loc)
	{
		return this.capabilities.get(loc);
	}
	
	
	public void preSave(NBTTagCompound nbt, EntityPlayer player, CapabilityContainer c){
		for(IListener li : this.listeners)
			li.preSave(nbt,player,c);
	}
	public void postSave(NBTTagCompound nbt, EntityPlayer player, CapabilityContainer c){
		for(IListener li : this.listeners)
			li.postSave(nbt,player,c);
	}
	
	public void preRead(NBTTagCompound nbt, EntityPlayer player, CapabilityContainer c){
		for(IListener li : this.listeners)
			li.preRead(nbt,player,c);
	}
	public void postRead(NBTTagCompound nbt, EntityPlayer player, CapabilityContainer c){
		for(IListener li : this.listeners)
			li.postRead(nbt,player,c);
	}
	
	public void tick(EntityPlayer player){
		for(ITick tick : this.ticks)
			tick.tick(player, this);
	}

}