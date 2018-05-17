package com.EvilNotch.lib.minecraft.content.pcapabilites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CapabilityContainer {
	
	public HashMap<ResourceLocation,ICapability> capabilities = null;
	
	public CapabilityContainer(){
		this.capabilities = new HashMap();
	}
	
	public ICapability getCapability(ResourceLocation loc)
	{
		return this.capabilities.get(loc);
	}

}
