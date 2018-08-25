package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import java.util.ArrayList;
import java.util.List;

public class CapRegHandler {
	
	public static List<ICapRegistry> regs = new ArrayList();
	
	public static void registerRegistry(ICapRegistry reg)
	{
		regs.add(reg);
	}

}
