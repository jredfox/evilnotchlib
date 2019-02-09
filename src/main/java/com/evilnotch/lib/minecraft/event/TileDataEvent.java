package com.evilnotch.lib.minecraft.event;

import com.evilnotch.lib.minecraft.util.TileEntityUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileDataEvent extends Event{
	
	public BlockPos pos;
	public TileEntity tile;
	public World world;
	
	public TileDataEvent(TileEntity tile,World w)
	{
		this.pos = tile.getPos();
		this.tile = tile;
		this.world = w;
	}
	
	public static class Merge extends TileDataEvent
	{
		public NBTTagCompound tileData;
		public NBTTagCompound nbt;
		
		public Merge(TileEntity tile, World w, NBTTagCompound tileData, NBTTagCompound nbt) 
		{
			super(tile,w);
			this.tileData = tileData == null ? TileEntityUtil.getTileNBT(tile) : tileData;
			this.nbt = nbt;
		}	
	}
	
	public static class Post extends TileDataEvent
	{
		public Post(TileEntity tile, World w) 
		{
			super(tile,w);
		}	
	}

}
