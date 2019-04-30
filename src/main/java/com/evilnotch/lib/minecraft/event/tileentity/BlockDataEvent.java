package com.evilnotch.lib.minecraft.event.tileentity;

import javax.annotation.Nullable;

import com.evilnotch.lib.minecraft.util.NBTUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * this event is specifically only for when a player places a tile entity like silkspawners
 * @author jredfox
 */
public class BlockDataEvent {
	
	public static class Permissions extends TileUseItemEvent.Permissions
	{
		public boolean isVanilla;
		
		public Permissions(TileEntity tile, NBTTagCompound nbt, EntityPlayer player, ItemStack stack) 
		{
			super(tile, nbt, player, stack);
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

		public Post(TileEntity tile, NBTTagCompound nbt, EntityPlayer player, ItemStack stack) 
		{
			super(tile, nbt, player, stack);
			this.isVanilla = isVanilla(stack);
		}
	}

	private static boolean isVanilla(ItemStack stack) {
		return stack.getSubCompound("BlockData") != null;
	}

	public static class HasTileData extends TileDataEvent
	{
		public ItemStack stack;
		/**
		 * the item stack's nbttagcompound or sub tag
		 */
		@Nullable
		public NBTTagCompound nbt;
		/**
		 * change this to true if you find an item that doesn't automatically fire both sides with a designated blockdata tag
		 */
		public boolean canFire;
		
		public HasTileData(TileEntity tile, NBTTagCompound nbt, ItemStack stack) 
		{
			super(tile);
			this.canFire = nbt != null;
			this.nbt = nbt != null ? nbt.copy() : NBTUtil.getOrCreateNBT(stack).copy();
			this.stack = stack;
		}
	}

}
