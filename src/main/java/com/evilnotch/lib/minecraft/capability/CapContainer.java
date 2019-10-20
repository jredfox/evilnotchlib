package com.evilnotch.lib.minecraft.capability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CapContainer<T> {
	
	public HashMap<ResourceLocation,ICapability<T>> caps = new HashMap();
	public HashMap<ResourceLocation,ICapabilityTick<T>> ticks = new HashMap();
	public Set<IListener> listeners = new HashSet<IListener>();
	
	public void registerCapability(ResourceLocation loc, ICapability cap)
	{
		if(cap instanceof ICapabilityTick)
			ticks.put(loc, (ICapabilityTick) cap);
		caps.put(loc, cap);
	}
	
	public void removeCapability(ResourceLocation loc)
	{
		caps.remove(loc);
		ticks.remove(loc);
	}
	
	public void tick(T object)
	{
		for(ICapabilityTick cap : this.ticks.values())
			cap.tick(object, this);
	}
	
	public void writeToNBT(T object, NBTTagCompound nbt)
	{
		if(nbt == null && object instanceof ItemStack)
			return;
		this.preSave(nbt, object);
		for(ICapability cap : this.caps.values())
		{
			cap.writeToNBT(object, nbt, this);
		}
		this.postSave(nbt, object);
	}

	public void readFromNBT(T object, NBTTagCompound nbt)
	{
		if(nbt == null && object instanceof ItemStack)
			return;
		this.preRead(nbt, object);
		for(ICapability cap : this.caps.values())
			cap.readFromNBT(object, nbt, this);
		this.postRead(nbt, object);
	}
	
	/**
	 * get any capability in this container
	 */
	public ICapability getCapability(ResourceLocation loc)
	{
		return this.caps.get(loc);
	}
	
	/**
	 * get a tickable capability from the ticking map as it's faster if your going to call this on tick
	 */
	public ICapabilityTick getTickableCapability(ResourceLocation loc)
	{
		return (ICapabilityTick) this.ticks.get(loc);
	}
	
	public void preSave(NBTTagCompound nbt, T obj)
	{
		for(IListener li : this.listeners)
			li.preSave(nbt, (ICapabilityProvider)obj, this);
	}
	
	public void postSave(NBTTagCompound nbt, T obj)
	{
		for(IListener li : this.listeners)
			li.postSave(nbt, (ICapabilityProvider)obj, this);
	}
	
	public void preRead(NBTTagCompound nbt, T obj)
	{
		for(IListener li : this.listeners)
			li.preRead(nbt, (ICapabilityProvider)obj, this);
	}
	
	public void postRead(NBTTagCompound nbt, T obj)
	{
		for(IListener li : this.listeners)
			li.postRead(nbt, (ICapabilityProvider)obj, this);
	}
}
