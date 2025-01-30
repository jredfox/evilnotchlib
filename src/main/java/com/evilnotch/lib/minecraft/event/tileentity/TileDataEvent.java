package com.evilnotch.lib.minecraft.event.tileentity;

import com.evilnotch.lib.minecraft.util.NBTUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileDataEvent extends Event{
	
	public TileEntity tile;
	public World world;
	public BlockPos pos;
	
	public TileDataEvent(TileEntity tile)
	{
		this.tile = tile;
		this.world = tile.getWorld();
		this.pos = tile.getPos();
	}
	
	public static class Merge extends TileDataEvent
	{
		public NBTTagCompound tileData;
		public NBTTagCompound nbt;
		
		public Merge(TileEntity tile, NBTTagCompound tileData, NBTTagCompound nbt) 
		{
			super(tile);
			this.tileData = tileData;
			this.nbt = NBTUtil.copyNBT(nbt);
		}
	}
	
	public static class Post extends TileDataEvent
	{
		public Post(TileEntity tile) 
		{
			super(tile);
		}
	}

}
