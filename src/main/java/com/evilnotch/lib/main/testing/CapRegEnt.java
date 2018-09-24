package com.evilnotch.lib.main.testing;

import com.evilnotch.lib.minecraft.content.capability.primitive.CapString;
import com.evilnotch.lib.minecraft.content.capability.registry.CapContainer;
import com.evilnotch.lib.minecraft.content.capability.registry.CapRegEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CapRegEnt extends CapRegEntity{

	@Override
	public void register(Entity object, CapContainer c) 
	{
		if(object instanceof EntityPlayer)
		{
			c.registerCapability(new ResourceLocation("a:b"), new CapString<Entity>("libCap"));
			c.registerCapability(new ResourceLocation("a:c"), new CapTickTest());
		}
	}

}
