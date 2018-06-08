package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CapabilityReg {
	
	public static HashMap<String,CapabilityContainer> capabilities = new HashMap();
	public static ArrayList<ICapabilityProvider> reg = new ArrayList();
	
	/**
	 * call this in pre-init this is a registerer for all your capabilities
	 */
	public static void registerCapProvider(ICapabilityProvider toReg)
	{
		reg.add(toReg);
	}
	
	public static void registerCapability(EntityPlayer p,ResourceLocation loc,ICapability cap,CapabilityContainer container)
	{
		container.capabilities.put(loc, cap);
		if(cap instanceof ITick)
			container.ticks.add((ITick) cap);
	}
	/**
	 * may return null
	 */
	public static CapabilityContainer getCapabilityConatainer(EntityPlayer p)
	{
		return capabilities.get(p.getName() );
	}
	
	public static void read(EntityPlayer p, NBTTagCompound nbt) 
	{
		CapabilityContainer caps = getCapabilityConatainer(p);
		caps.preRead(nbt, p,caps);
		Iterator<Map.Entry<ResourceLocation,ICapability> > it = caps.capabilities.entrySet().iterator();
		while(it.hasNext())
		{
			it.next().getValue().readFromNBT(nbt,p,caps);
		}
		caps.postRead(nbt, p,caps);
	}
	
	public static void save(EntityPlayer p, NBTTagCompound nbt) 
	{
		CapabilityContainer caps = getCapabilityConatainer(p);
		caps.preSave(nbt, p,caps);
		Iterator<Map.Entry<ResourceLocation,ICapability> > it = caps.capabilities.entrySet().iterator();
		while(it.hasNext())
		{
			it.next().getValue().writeToNBT(nbt,p,caps);
		}
		caps.postSave(nbt, p,caps);
	}
	
	public static void registerEntity(EntityPlayer p) 
	{
		if(!capabilities.containsKey(p.getName() ))
			CapabilityReg.capabilities.put(p.getName(), new CapabilityContainer() );
		for(ICapabilityProvider provider : reg)
		{
			provider.register(p,getCapabilityConatainer(p));
		}
	}

}
