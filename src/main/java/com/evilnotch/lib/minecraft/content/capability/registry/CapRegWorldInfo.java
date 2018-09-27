package com.evilnotch.lib.minecraft.content.capability.registry;

import net.minecraft.world.storage.WorldInfo;

public abstract class CapRegWorldInfo implements ICapRegistry<WorldInfo>{
	
	@Override
	public Class getObjectClass(){
		return WorldInfo.class;
	}

}