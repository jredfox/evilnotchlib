package com.evilnotch.lib.minecraft.content.capability.registry;

import net.minecraft.tileentity.TileEntity;

public abstract class CapRegTileEntity implements ICapRegistry<TileEntity>{
	
	@Override
	public Class getObjectClass() {
		return TileEntity.class;
	}

}
