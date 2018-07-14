package com.EvilNotch.lib.minecraft.events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityRemoveEvent extends TileEntityEvent{

	public TileEntityRemoveEvent(TileEntity tile,World w) {
		super(tile,w);
	}

}
