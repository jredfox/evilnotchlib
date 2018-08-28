package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.EvilNotch.lib.main.eventhandlers.LibEvents;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CapabilityReg {
	
	public static HashMap<String,CapContainer<EntityPlayerMP>> capabilities = new HashMap();
	public static ArrayList<IPCapabilityReg> reg = new ArrayList();
	
	/**
	 * call this in pre-init this is a registered for all your capabilities
	 */
	public static void registerCapProvider(IPCapabilityReg toReg)
	{
		reg.add(toReg);
	}
	
	/**
	 * may return null
	 */
	public static CapContainer getCapabilityConatainer(EntityPlayer p)
	{
		return capabilities.get(getUsername(p) );
	}
	
	/**
	 * parse capabilities from a file
	 */
	public static void readFromFile(EntityPlayer p)
	{
		File caps = new File(LibEvents.playerDataDir,"caps/" + p.getUniqueID().toString() + ".dat");
		NBTTagCompound nbt = NBTUtil.getFileNBTSafley(caps);
		if(nbt == null)
		{
			nbt = new NBTTagCompound();
			System.out.println("Unable to get nbt tag data creating blank tag data will wipe");
		}
		CapContainer c = getCapabilityConatainer(p);
		c.readFromNBT(p,nbt);
	}
	/**
	 * save capabilities to a file
	 */
	public static void saveToFile(EntityPlayer p) 
	{
		NBTTagCompound nbt = new NBTTagCompound();
		CapContainer c = getCapabilityConatainer(p);
		if(c == null)
		{
			System.out.println("player already saved?:" + p.getName());
			return;
		}
		c.writeToNBT(p,nbt);
		File f = new File(LibEvents.playerDataDir,"caps/" + p.getUniqueID().toString() + ".dat");
		NBTUtil.updateNBTFileSafley(f, nbt);
		System.out.print("saved player:" + p.getName() + " toFile:" + f.getName() + ".dat\n");
	}
	
	public static void registerEntity(EntityPlayer p) 
	{	
		String name = getUsername(p);
		if(!capabilities.containsKey(name))
			CapabilityReg.capabilities.put(name, new CapContainer<EntityPlayerMP>() );
		
		CapContainer<EntityPlayer> container = getCapabilityConatainer(p);
		for(IPCapabilityReg registry : reg)
		{
			registry.register((EntityPlayerMP) p,container);
		}
	}

	/**
	 * may return null get capability from player name and resoruce location
	 */
	public static IPCapability getCapability(EntityPlayer p, ResourceLocation loc) 
	{
		if(p == null || loc == null)
			return null;
		
		CapContainer<EntityPlayer> container = getCapabilityConatainer(p);
		if(container == null)
			return null;
		return (IPCapability) container.getCapability(loc);
	}

	public static String getUsername(EntityPlayer p) {
		return p.getGameProfile().getName();
	}

	/**
	 * only use if your know what your doing USE PLAYER.GETPROFILE.GETNAME() not player.getName()
	 */
	@Deprecated
	public static IPCapability getCapability(String username, ResourceLocation loc) 
	{
		if(Strings.isNullOrEmpty(username) || loc == null)
			return null;
		CapContainer c = capabilities.get(username);
		if(c == null)
			return null;
		return (IPCapability) c.getCapability(loc);
	}

	public static void removeCapailityContainer(EntityPlayer player) {
		capabilities.remove(getUsername(player));
	}

}
