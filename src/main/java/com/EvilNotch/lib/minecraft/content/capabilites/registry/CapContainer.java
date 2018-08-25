package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapTick;
import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CapContainer<T> {
	
	public HashMap<ResourceLocation,ICapability> caps = new HashMap();
	public HashMap<ResourceLocation,ICapTick> ticks = new HashMap();
	
	public void registerCapability(ResourceLocation loc,ICapability cap)
	{
		if(cap instanceof ICapTick)
			ticks.put(loc,(ICapTick) cap);
		caps.put(loc, cap);
	}
	public void removeCapability(ResourceLocation loc)
	{
		caps.remove(loc);
		ticks.remove(loc);
	}
	public void tick(T object)
	{
		for(ICapTick cap : this.ticks.values())
			cap.tick(object,this);
	}
	public void writeToNBT(T object,NBTTagCompound nbt)
	{
		for(ICapability cap : this.caps.values())
			cap.writeToNBT(object,nbt,this);
	}
	public void readFromNBT(T object,NBTTagCompound nbt)
	{
		for(ICapability cap : this.caps.values())
			cap.readFromNBT(object,nbt,this);
	}

}
