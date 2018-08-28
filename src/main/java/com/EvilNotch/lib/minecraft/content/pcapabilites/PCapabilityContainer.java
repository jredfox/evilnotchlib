package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class PCapabilityContainer {
	
	public HashMap<ResourceLocation,IPCapability> capabilities = null;
	public ArrayList<IPListener> listeners;
	public ArrayList<ITick> ticks;
	
	public PCapabilityContainer(){
		this.capabilities = new HashMap();
		this.listeners = new ArrayList();
		this.ticks = new ArrayList();
	}
	
	public IPCapability getCapability(ResourceLocation loc)
	{
		return this.capabilities.get(loc);
	}
	
	public void writeToNBT(NBTTagCompound nbt,EntityPlayer p)
	{
		this.preSave(nbt, p);
		Iterator<Map.Entry<ResourceLocation,IPCapability> > it = this.capabilities.entrySet().iterator();
		while(it.hasNext())
		{
			it.next().getValue().writeToNBT(nbt,p,this);
		}
		this.postSave(nbt, p);
	}
	
	public void readFromNBT(NBTTagCompound nbt,EntityPlayer p)
	{
		this.preRead(nbt, p);
		Iterator<Map.Entry<ResourceLocation,IPCapability> > it = this.capabilities.entrySet().iterator();
		while(it.hasNext())
		{
			it.next().getValue().readFromNBT(nbt,p,this);
		}
		this.postRead(nbt, p);
	}
	
	public void preSave(NBTTagCompound nbt, EntityPlayer player){
		for(IPListener li : this.listeners)
			li.preSave(nbt,player,this);
	}
	public void postSave(NBTTagCompound nbt, EntityPlayer player){
		for(IPListener li : this.listeners)
			li.postSave(nbt,player,this);
	}
	
	public void preRead(NBTTagCompound nbt, EntityPlayer player){
		for(IPListener li : this.listeners)
			li.preRead(nbt,player,this);
	}
	public void postRead(NBTTagCompound nbt, EntityPlayer player){
		for(IPListener li : this.listeners)
			li.postRead(nbt,player,this);
	}
	
	public void tick(EntityPlayer player){
		for(ITick tick : this.ticks)
			tick.tick(player, this);
	}
	
	public void registerCapability(ResourceLocation loc,IPCapability cap){
		this.capabilities.put(loc, cap);
		if(cap instanceof ITick)
			this.ticks.add((ITick) cap);
	}

}
