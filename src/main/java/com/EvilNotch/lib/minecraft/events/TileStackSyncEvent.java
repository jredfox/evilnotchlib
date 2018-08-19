package com.EvilNotch.lib.minecraft.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileStackSyncEvent extends Event{
	
	public ItemStack stack;
	public BlockPos pos;
	public TileEntity tile;
	public EntityPlayer player;
	public World world;
	/**
	 * the base class for all the tile stack sync events do not use this directly
	 */
	public TileStackSyncEvent(ItemStack stack,BlockPos pos,TileEntity tile,EntityPlayer player,World w)
	{
		this.stack = stack;
		this.pos = pos;
		this.tile = tile;
		this.player = player;
		this.world = w;
	}
	
	/**
	 * allow people to deny permisions of both ops only for placement and canUseCommand booleans This isn't cancelable
	 * @author jredfox
	 */
	public static class Permissions extends TileStackSyncEvent
	{
		public boolean opsOnly;
		public boolean canUseCommand;
		
		public Permissions(ItemStack item,BlockPos pos,TileEntity tile,EntityPlayer player,World w)
		{
			super(item,pos,tile,player,w);
			this.opsOnly = tile.onlyOpsCanSetNbt();
			this.canUseCommand = player != null && player.canUseCommand(2, "");
		}
	}
	/**
	 * allows you to edit the nbttagcompounds before the tileData merges with the nbt tag compound to cancle an event use permissions
	 */
	public static class Pre extends TileStackSyncEvent
	{	
		public NBTTagCompound tileData;
		public NBTTagCompound nbt;
		
		public Pre(ItemStack stack,BlockPos pos,TileEntity tile,EntityPlayer player,World w,NBTTagCompound tileData,NBTTagCompound stackNBT)
		{
			super(stack,pos,tile,player,w);
			this.nbt = stackNBT;
			this.tileData = tileData;
		}
	}
	/**
	 * sync any additional changes after the event has fired. This isn't cancelable
	 * @author jredfox
	 */
	public static class Post extends TileStackSyncEvent
	{
		public Post(ItemStack stack,BlockPos pos,TileEntity tile,EntityPlayer player,World w)
		{
			super(stack,pos,tile,player,w);
		}
	}

}
