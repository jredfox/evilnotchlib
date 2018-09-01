package com.EvilNotch.lib.main;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapTick;
import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.tick.ITick;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CapTickTest implements ICapability,ICapTick<Entity> {

	@Override
	public void writeToNBT(Object object, NBTTagCompound nbt, CapContainer c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(Object object, NBTTagCompound nbt, CapContainer c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick(Entity object, CapContainer c) 
	{
		
	}

}
