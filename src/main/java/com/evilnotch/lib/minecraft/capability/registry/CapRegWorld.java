package com.evilnotch.lib.minecraft.capability.registry;

import net.minecraft.world.World;

public abstract class CapRegWorld implements ICapabilityRegistry<World>{
	
	@Override
	public Class getObjectClass() {
		return World.class;
	}

}
