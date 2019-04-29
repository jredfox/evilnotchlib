package com.evilnotch.lib.main.capability;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.capability.registry.CapRegEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CapRegDefaultHandler extends CapRegEntity{

	public static final ResourceLocation initSpawned = new ResourceLocation(MainJava.MODID, "initSpawned");
	
	@Override
	public void register(Entity object, CapContainer c) 
	{
		c.registerCapability(initSpawned, new CapBoolean<Entity>(initSpawned.toString()));
	}

}
