package com.evilnotch.lib.minecraft.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TileStackSyncEvent extends TileDataEvent{
	
	public ItemStack stack;
	public EntityPlayer player;
	/**
	 * you can filter out if it's a vanilla or modded event by looking at this boolean true when using BlockEntityTag
	 */
	public boolean isBlockData;
	
	/**
	 * the base class for all the tile stack sync events do not use this directly
	 */
	public TileStackSyncEvent(ItemStack stack, TileEntity tile, EntityPlayer player, World w, boolean data)
	{
		super(tile,w);
		this.stack = stack;
		this.player = player;
		this.isBlockData = data;
	}
	
	/**
	 * allow people to deny permisions of both ops only for placement and canUseCommand booleans This isn't cancelable
	 * @author jredfox
	 */
	public static class Permissions extends TileStackSyncEvent
	{
		public boolean opsOnly;
		public boolean canUseCommand;
		
		public Permissions(ItemStack item, TileEntity tile, EntityPlayer player, World w, boolean data)
		{
			super(item,tile,player,w,data);
			this.opsOnly = tile.onlyOpsCanSetNbt();
			this.canUseCommand = player != null && player.canUseCommand(2, "");
		}
	}
	
	/**
	 * allows you to edit the nbttagcompounds before the tileData merges with the nbt tag compound to cancle an event use permissions
	 */
	public static class Merge extends TileStackSyncEvent
	{	
		public NBTTagCompound tileData;
		public NBTTagCompound nbt;
		
		public Merge(ItemStack stack, TileEntity tile, EntityPlayer player, World w, boolean data, NBTTagCompound tileData, NBTTagCompound stackNBT)
		{
			super(stack,tile,player,w,data);
			this.tileData = tileData;
			this.nbt = stackNBT;
		}
	}
	
	/**
	 * sync any additional changes after the event has fired. This isn't cancelable
	 * @author jredfox
	 */
	public static class Post extends TileStackSyncEvent
	{
		public Post(ItemStack stack, TileEntity tile, EntityPlayer player, World w, boolean data)
		{
			super(stack,tile,player,w,data);
		}
	}

}
