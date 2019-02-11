package com.evilnotch.lib.minecraft.capability.registry;

import net.minecraft.tileentity.TileEntity;

public abstract class CapRegTileEntity implements ICapabilityRegistry<TileEntity>{
	
	@Override
	public Class getObjectClass() {
		return TileEntity.class;
	}

}
