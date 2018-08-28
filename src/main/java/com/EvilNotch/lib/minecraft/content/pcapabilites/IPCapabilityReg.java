package com.EvilNotch.lib.minecraft.content.pcapabilites;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.ICapRegistry;

import net.minecraft.entity.player.EntityPlayer;

public interface IPCapabilityReg extends ICapRegistry<EntityPlayer>{
	
	@Override
	public default Class getObjectClass()
	{
		return EntityPlayer.class;
	}

}
