package com.EvilNotch.lib.asm;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapRegHandler;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;

import net.minecraft.nbt.NBTTagCompound;

public class Caps implements ICapProvider{
	
	public Caps()
	{
		CapRegHandler.registerCapsToObj(this);
	}
	
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
	
}
