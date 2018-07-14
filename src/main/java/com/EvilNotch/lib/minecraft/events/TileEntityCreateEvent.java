package com.EvilNotch.lib.minecraft.events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileEntityCreateEvent extends TileEntityEvent{
	
	public TileEntityCreateEvent(TileEntity tile, World world){
		super(tile,world);
	}

}
