package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
	
	/**
	 * may return null
	 */
	public static CapabilityContainer getCapabilityConatainer(EntityPlayer p)
	{
		return capabilities.get(getUsername(p) );
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
		String name = getUsername(p);
		if(!capabilities.containsKey(name))
			CapabilityReg.capabilities.put(name, new CapabilityContainer() );
		
		CapabilityContainer container = getCapabilityConatainer(p);
		for(ICapabilityProvider provider : reg)
		{
			provider.register(p,container);
		}
	}

	/**
	 * may return null get capability from player name and resoruce location
	 */
	public static ICapability getCapability(EntityPlayer p, ResourceLocation loc) 
	{
		if(p == null || loc == null)
			return null;
		
		CapabilityContainer container = getCapabilityConatainer(p);
		if(container == null)
			return null;
		return container.getCapability(loc);
	}

	public static String getUsername(EntityPlayer p) {
		return p.getGameProfile().getName();
	}

	/**
	 * only use if your know what your doing USE PLAYER.GETPROFILE.GETNAME() not player.getName()
	 */
	@Deprecated
	public static ICapability getCapability(String username, ResourceLocation loc) 
	{
		if(Strings.isNullOrEmpty(username) || loc == null)
			return null;
		CapabilityContainer c = capabilities.get(username);
		if(c == null)
			return null;
		return c.getCapability(loc);
	}

}
