package com.EvilNotch.lib.minecraft.events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileEntityEvent extends Event{
	
	public TileEntity tile;
	public World world;
	
	public TileEntityEvent(TileEntity tile, World world){
		this.tile = tile;
		this.world = world;
	}

}
