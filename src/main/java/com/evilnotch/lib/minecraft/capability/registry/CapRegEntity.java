package com.evilnotch.lib.minecraft.capability.registry;

import net.minecraft.entity.Entity;

public abstract class CapRegEntity implements ICapabilityRegistry<Entity>{

	@Override
	public Class getObjectClass() 
	{
		return Entity.class;
	}

}
