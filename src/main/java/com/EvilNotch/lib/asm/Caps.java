package com.EvilNotch.lib.asm;

import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;

public class Caps {
	
	public CapContainer capContainer = new CapContainer();
	
	/**
	 * this method is injected into all ICapProvider Objects then patched and repaired
	 * @return
	 */
	public CapContainer getCapContainer()
	{
		CapContainer c = this.capContainer;
		if(c == null)
		{
			this.setCapContainer(new CapContainer());
			c = this.capContainer;
		}
		return c;
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
