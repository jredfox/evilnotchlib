package com.evilnotch.lib.minecraft.content.capabilites.registry;

import net.minecraft.world.storage.WorldInfo;

public abstract class CapRegWorldInfo implements ICapRegistry<WorldInfo>{
	
	@Override
	public Class getObjectClass(){
		return WorldInfo.class;
	}

}
