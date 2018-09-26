package com.evilnotch.lib.minecraft.content.capability.registry;

import net.minecraft.world.World;

public abstract class CapRegWorld implements ICapRegistry<World>{
	
	@Override
	public Class getObjectClass() {
		return World.class;
	}

}
