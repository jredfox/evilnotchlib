package com.EvilNotch.lib.minecraft.content.capabilites.registry;

import net.minecraft.tileentity.TileEntity;

public abstract class CapRegTileEntity implements ICapRegistry{
	
	@Override
	public Class getObjectClass() {
		return TileEntity.class;
	}

}
