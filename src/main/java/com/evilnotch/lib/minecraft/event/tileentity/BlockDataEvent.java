package com.evilnotch.lib.minecraft.event.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * this event is specifically only for when a player places a tile entity like silkspawners
 * @author jredfox
 *
 */
public class BlockDataEvent {
	
	public static class Permissions extends TileUseItemEvent.Permissions
	{
		public boolean isVanilla;
		
		public Permissions(TileEntity tile, EntityPlayer player, ItemStack stack) 
		{
			super(tile, player, stack);
			this.isVanilla = isVanilla(stack);
		}
	}
	
	public static class Merge extends TileUseItemEvent.Merge
	{
		public boolean isVanilla;

		public Merge(TileEntity tile, EntityPlayer player, ItemStack stack, NBTTagCompound tileData, NBTTagCompound stackNBT)
		{
			super(tile, player, stack, tileData, stackNBT);
			this.isVanilla = isVanilla(stack);
		}
	}
	
	public static class Post extends TileUseItemEvent.Post
	{
		public boolean isVanilla;

		public Post(TileEntity tile, EntityPlayer player, ItemStack stack) 
		{
			super(tile, player, stack);
			this.isVanilla = isVanilla(stack);
		}
	}

	private static boolean isVanilla(ItemStack stack) {
		return stack.getSubCompound("BlockData") != null;
	}

}
