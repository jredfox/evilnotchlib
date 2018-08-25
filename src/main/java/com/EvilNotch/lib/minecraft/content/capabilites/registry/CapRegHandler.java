package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.lib.util.JavaUtil;

public class CapRegHandler {
	
	public static List<ICapRegistry> regs = new ArrayList();
	
	public static void registerRegistry(ICapRegistry reg)
	{
		regs.add(reg);
	}
	/**
	 * this attaches your caps to the object on construction
	 */
	public static void registerCapsToObj(ICapProvider obj)
	{
		obj.setCapcontainer(new CapContainer());//reset the data each time the registry event fires
		for(ICapRegistry reg : regs)
		{
			if(JavaUtil.isClassExtending(reg.getObjectClass(),obj.getClass()))
			{
				reg.register(obj.getCapContainer(), obj);
			}
		}
	}
}
