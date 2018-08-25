package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import net.minecraft.entity.Entity;

public abstract class CapRegEntity implements ICapRegistry{

	@Override
	public Class getObjectClass() {
		return Entity.class;
	}

}
