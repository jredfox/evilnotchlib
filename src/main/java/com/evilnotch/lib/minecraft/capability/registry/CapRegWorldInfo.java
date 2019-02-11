package com.evilnotch.lib.minecraft.capability.registry;

import net.minecraft.world.storage.WorldInfo;

public abstract class CapRegWorldInfo implements ICapabilityRegistry<WorldInfo>{
	
	@Override
	public Class getObjectClass(){
		return WorldInfo.class;
	}

}
