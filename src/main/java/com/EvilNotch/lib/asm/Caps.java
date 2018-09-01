package com.EvilNotch.lib.asm;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapProvider;

import net.minecraft.nbt.NBTTagCompound;

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
	public int a(NBTTagCompound nbt){
		this.readFromNBT(nbt);
		int a = 1;int b=2;
		return -1;
	}
	public void b(NBTTagCompound nbt)
	{
		this.writeToNBTTagCompound(nbt);
	}
	private void readFromNBT(NBTTagCompound nbt) {
		this.capContainer.readFromNBT(null, nbt);
		
	}
	private void writeToNBTTagCompound(NBTTagCompound nbt) {
	}
	
}
