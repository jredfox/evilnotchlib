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
		
		public Permissions(ItemStack stack, TileEntity tile, EntityPlayer player) 
		{
			super(stack, tile, player);
			this.isVanilla = isVanilla(stack);
		}
	}
	
	public static class Merge extends TileUseItemEvent.Merge
	{
		public boolean isVanilla;

		public Merge(ItemStack stack, TileEntity tile, EntityPlayer player, NBTTagCompound tileData, NBTTagCompound stackNBT)
		{
			super(stack, tile, player, tileData, stackNBT);
			this.isVanilla = isVanilla(stack);
		}
	}
	
	public static class Post extends TileUseItemEvent.Post
	{
		public boolean isVanilla;

		public Post(ItemStack stack, TileEntity tile, EntityPlayer player) 
		{
			super(stack, tile, player);
			this.isVanilla = isVanilla(stack);
		}
	}

	private static boolean isVanilla(ItemStack stack) {
		return stack.getSubCompound("BlockData") != null;
	}

}
