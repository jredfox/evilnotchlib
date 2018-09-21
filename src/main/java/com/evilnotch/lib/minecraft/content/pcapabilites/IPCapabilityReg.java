package com.evilnotch.lib.minecraft.content.pcapabilites;

import com.evilnotch.lib.minecraft.content.capabilites.registry.ICapRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public interface IPCapabilityReg extends ICapRegistry<EntityPlayerMP>{
	
	@Override
	public default Class getObjectClass()
	{
		return EntityPlayer.class;
	}

}
