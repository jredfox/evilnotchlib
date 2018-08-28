package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class CapRegWorld implements ICapRegistry<World>{
	
	@Override
	public Class getObjectClass() {
		return World.class;
	}

}
