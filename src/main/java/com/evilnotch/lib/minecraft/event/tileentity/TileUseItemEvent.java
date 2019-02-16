package com.evilnotch.lib.minecraft.event.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * this is the root class it's used by spawn eggs but, not on tile placement
 * @author jredfox
 */
public class TileUseItemEvent extends TileDataEvent{
	
	
	private TileUseItemEvent(TileEntity tile) 
	{
		super(tile);
	}

	/**
	 * allow people to deny permissions if player can't use said item either spawn egg or spawner
	 */
	public static class Permissions extends TileUseItemEvent
	{
		public ItemStack stack;
		public EntityPlayer player;
		
		public boolean opsOnly;
		public boolean canUseCommand;
		
		public Permissions(TileEntity tile, EntityPlayer player, ItemStack stack)
		{
			super(tile);
			this.opsOnly = tile.onlyOpsCanSetNbt();
			this.canUseCommand = player != null && player.canUseCommand(2, "");
			
			this.stack = stack;
			this.player = player;
		}
	}
	
	/**
	 * allows you to edit the nbttagcompounds before the tileData merges with the nbt tag compound to cancle an event use permissions
	 */
	public static class Merge extends TileDataEvent.Merge
	{	
		public ItemStack stack;
		public EntityPlayer player;
		
		public Merge(TileEntity tile, EntityPlayer player, ItemStack stack, NBTTagCompound tileData, NBTTagCompound stackNBT)
		{
			super(tile, tileData, stackNBT);
			
			this.stack = stack;
			this.player = player;
		}
	}
	
	/**
	 * sync any additional changes after the event has fired. This isn't cancelable
	 * @author jredfox
	 */
	public static class Post extends TileDataEvent.Post
	{
		public ItemStack stack;
		public EntityPlayer player;
		
		public Post(TileEntity tile, EntityPlayer player, ItemStack stack)
		{
			super(tile);
			
			this.stack = stack;
			this.player = player;
		}
	}

}
