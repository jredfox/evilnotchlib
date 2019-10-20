package com.evilnotch.lib.minecraft.capability.registry;

import java.util.HashSet;
import java.util.Set;

import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.ICapability;
import com.evilnotch.lib.minecraft.capability.ICapabilityProvider;
import com.evilnotch.lib.minecraft.capability.ICapabilityTick;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CapabilityRegistry {
	
	public static Set<ICapabilityRegistry> regs = new HashSet();
	
	public static void registerRegistry(ICapabilityRegistry reg)
	{
		regs.add(reg);
	}
	
	/**
	 * this attaches your caps to the object on construction
	 */
	public static void registerCapsToObj(Object o)
	{
		ICapabilityProvider obj = (ICapabilityProvider)o;
		obj.setCapContainer(new CapContainer());//reset the data each time the registry event fires
		for(ICapabilityRegistry reg : regs)
		{
			if(JavaUtil.isClassExtending(reg.getObjectClass(),obj.getClass()))
			{
				reg.register(obj,obj.getCapContainer());
			}
		}
	}
	
	/**
	 * get the entire capability container form the object
	 */
	public static CapContainer getCapContainer(Object obj)
	{
		return ((ICapabilityProvider)obj).getCapContainer();
	}
	
	/**
	 * get a ICapability from the ICapProvider which get's it directly from it's container 
	 */
	public static ICapability getCapability(Object obj, ResourceLocation loc)
	{
		return ((ICapabilityProvider)obj).getCapability(loc);
	}
	
	/**
	 * get a tickable capability instance
	 */
	public static ICapabilityTick getCapabilityTick(Object obj, ResourceLocation loc)
	{
		return ((ICapabilityProvider)obj).getCapabilityTick(loc);
	}

	public static void setCapContainer(Object obj, CapContainer container) 
	{
		((ICapabilityProvider)obj).setCapContainer(container);
	}
}
