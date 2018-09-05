package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import java.util.HashSet;
import java.util.Set;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.util.ResourceLocation;

public class CapRegHandler {
	
	public static Set<ICapRegistry> regs = new HashSet();
	
	public static void registerRegistry(ICapRegistry reg)
	{
		regs.add(reg);
	}
	/**
	 * this attaches your caps to the object on construction
	 */
	public static void registerCapsToObj(Object o)
	{
		ICapProvider obj = (ICapProvider)o;
		obj.setCapContainer(new CapContainer());//reset the data each time the registry event fires
		for(ICapRegistry reg : regs)
		{
			if(JavaUtil.isClassExtending(reg.getObjectClass(),obj.getClass()))
			{
				reg.register(obj,obj.getCapContainer());
			}
		}
	}
	/**
	 * get the entire capability container form the object
	 * @param T is what class the ICapProvider belongs to for example CapContainer[Entity]
	 * @return
	 */
	public static CapContainer getCapContainer(Object obj)
	{
		return ((ICapProvider)obj).getCapContainer();
	}
	
	/**
	 * get a ICapability from the ICapProvider which get's it directly from it's container 
	 * might be slightly more optimized sadly you can't reference it without casting it to an interface anyways
	 */
	public static ICapability getCapability(Object obj,ResourceLocation loc)
	{
		return ((ICapProvider)obj).getCapability(loc);
	}
}
