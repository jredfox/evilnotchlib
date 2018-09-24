package com.evilnotch.lib.minecraft.content.capability.registry;

import net.minecraft.entity.Entity;

public abstract class CapRegEntity implements ICapRegistry<Entity>{

	@Override
	public Class getObjectClass() {
		return Entity.class;
	}

}
