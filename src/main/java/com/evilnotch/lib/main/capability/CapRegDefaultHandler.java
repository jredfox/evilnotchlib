package com.evilnotch.lib.main.capability;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.capability.registry.CapRegEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class CapRegDefaultHandler extends CapRegEntity{

	public static final ResourceLocation initSpawned = new ResourceLocation(MainJava.MODID, "initSpawned");
	public static final ResourceLocation addedToWorld = new ResourceLocation(MainJava.MODID, "addedToWorld");
	
	@Override
	public void register(Entity e, CapContainer c) 
	{
		if(e instanceof EntityLiving)
			c.registerCapability(initSpawned, new CapBoolean<Entity>(initSpawned.toString()));
		c.registerCapability(addedToWorld, new CapBoolean<Entity>(addedToWorld.toString()));
	}

}
