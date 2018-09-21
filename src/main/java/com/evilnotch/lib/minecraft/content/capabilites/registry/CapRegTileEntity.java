package com.evilnotch.lib.minecraft.content.capabilites.registry;

import net.minecraft.tileentity.TileEntity;

public abstract class CapRegTileEntity implements ICapRegistry<TileEntity>{
	
	@Override
	public Class getObjectClass() {
		return TileEntity.class;
	}

}
