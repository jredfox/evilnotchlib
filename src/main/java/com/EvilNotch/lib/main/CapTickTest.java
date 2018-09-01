package com.EvilNotch.lib.main;

import com.EvilNotch.lib.minecraft.content.capabilites.ICapTick;
import com.EvilNotch.lib.minecraft.content.capabilites.ICapability;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.minecraft.content.tick.ITick;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CapTickTest implements ICapability<Entity>,ICapTick<Entity> {

	@Override
	public void writeToNBT(Entity object, NBTTagCompound nbt, CapContainer c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(Entity object, NBTTagCompound nbt, CapContainer c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick(Entity object, CapContainer c) 
	{
		
	}
}
