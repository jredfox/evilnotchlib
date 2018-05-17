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
	
	public static void registerCapProvider(ICapabilityProvider toReg)
	{
		reg.add(toReg);
	}
	
	public static void registerCapability(EntityPlayer p,ResourceLocation loc,ICapability cap)
	{
		CapabilityContainer container = capabilities.get(p.getName());
		container.capabilities.put(loc, cap);
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
		CapabilityContainer caps = capabilities.get(p.getName() );
		Iterator<Map.Entry<ResourceLocation,ICapability> > it = caps.capabilities.entrySet().iterator();
		while(it.hasNext())
		{
			it.next().getValue().readFromNBT(nbt);
		}
	}
	
	public static void save(EntityPlayer p, NBTTagCompound nbt) 
	{
		CapabilityContainer caps = capabilities.get(p.getName() );
		if(caps == null)
			return;
		Iterator<Map.Entry<ResourceLocation,ICapability> > it = caps.capabilities.entrySet().iterator();
		while(it.hasNext())
		{
			it.next().getValue().writeToNBT(nbt);
		}
	}
	
	public static void registerEntity(EntityPlayer p) 
	{
		if(!capabilities.containsKey(p.getName() ))
			CapabilityReg.capabilities.put(p.getName(), new CapabilityContainer() );
		for(ICapabilityProvider provider : reg)
		{
			provider.register(p);
		}
	}

}
