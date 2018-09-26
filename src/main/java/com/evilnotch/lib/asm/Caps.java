package com.evilnotch.lib.asm;

import com.evilnotch.lib.minecraft.content.capability.ICapability;
import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;
import com.evilnotch.lib.minecraft.content.capability.registry.ICapProvider;

import net.minecraft.util.ResourceLocation;

public class Caps implements ICapProvider{
	
	public CapContainer capContainer = new CapContainer();
	
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	public CapContainer getCapContainer()
	{
		return this.capContainer;
	}
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	public void setCapContainer(CapContainer c)
	{
		this.capContainer = c;
	}
	/**
	 * this is a universal method for getting a capability from the ICapProvider object
	 */
	public ICapability getCapability(ResourceLocation loc)
	{
		return this.capContainer.getCapability(loc);
	}
	
}
