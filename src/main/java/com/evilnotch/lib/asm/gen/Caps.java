package com.evilnotch.lib.asm.gen;

import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.ICapability;
import com.evilnotch.lib.minecraft.capability.ICapabilityProvider;
import com.evilnotch.lib.minecraft.capability.ICapabilityTick;

import net.minecraft.util.ResourceLocation;
/**
 * this file is generated into all objects via asm that I support with my capability system.
 * @author jredfox
 *
 */
public class Caps implements ICapabilityProvider{
	
	public CapContainer capContainer = new CapContainer();
	
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	@Override
	public CapContainer getCapContainer()
	{
		return this.capContainer;
	}
	
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	@Override
	public void setCapContainer(CapContainer c)
	{
		this.capContainer = c;
	}
	
	/**
	 * this is a universal method for getting a capability from the ICapProvider object
	 */
	@Override
	public ICapability getCapability(ResourceLocation loc)
	{
		return this.capContainer.getCapability(loc);
	}
	
	@Override
	public ICapabilityTick getCapabilityTick(ResourceLocation loc) 
	{
		return this.capContainer.getTickableCapability(loc);
	}
	
}
