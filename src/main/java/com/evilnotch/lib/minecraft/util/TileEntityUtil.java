package com.evilnotch.lib.minecraft.util;

import javax.annotation.Nullable;

import com.evilnotch.lib.minecraft.event.TileStackSyncEvent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityUtil {
	/**
	 * Gets Tile Entity NBT
	 */
	public static NBTTagCompound getTileNBT(TileEntity tile)
	{
		if(tile == null)
			return null;
		NBTTagCompound nbt = new NBTTagCompound();
		tile.writeToNBT(nbt);
		return nbt;
	}
	
	public static void setTileEntity(World w,TileEntity tile,BlockPos pos)
	{
		w.setTileEntity(pos, tile);
		updateTileEntity(w,tile,pos);
	}
	
	/**
	 * Do not call if your doing this during chunk population simply call tile.markDirty()
	 */
	public static void updateTileEntity(World w,TileEntity tile,BlockPos pos) 
	{
		IBlockState state = w.getBlockState(pos);
		tile.markDirty();
		w.notifyBlockUpdate(pos, state, w.getBlockState(pos), 3);
	}
	
	public static ResourceLocation getResoureLocation(TileEntity te) 
	{
		return TileEntity.getKey(te.getClass());
	}
	
	/**
	 * set a tile entity nbt as placing it with an itemstack
	 */
	public static boolean setTileNBT(World worldIn, @Nullable EntityPlayer player, BlockPos pos, NBTTagCompound stack, boolean blockData)
	{
		return setTileNBT(worldIn, worldIn.getTileEntity(pos), player, pos, stack, blockData);
	}
	
	/**
	 * set a tile entity nbt from like a command block or dungeon tweaks
	 */
	public static boolean setTileNBT(World worldIn,TileEntity tile, EntityPlayer player, BlockPos pos, NBTTagCompound nbt, boolean blockData)
	{
	   if (tile != null)
	   {
	   	  TileStackSyncEvent.Permissions permissions = new TileStackSyncEvent.Permissions(ItemStack.EMPTY, pos, tile, player, worldIn, blockData);
	   	  permissions.canUseCommand = true;
	   	  MinecraftForge.EVENT_BUS.post(permissions);
	   	  if ((permissions.opsOnly) && (!permissions.canUseCommand))
	   	  {
	       	return false;
	   	  }
	   	  NBTTagCompound tileData = tile.writeToNBT(new NBTTagCompound());
	   	  NBTTagCompound copyTile = tileData.copy();
	       
	   	  TileStackSyncEvent.Merge mergeEvent = new TileStackSyncEvent.Merge(ItemStack.EMPTY, pos, tile, player, worldIn, blockData, tileData, nbt);
	   	  MinecraftForge.EVENT_BUS.post(mergeEvent);
	   	  tileData = mergeEvent.tileData;
	   	  nbt = mergeEvent.nbt;
	       
	   	  tileData.merge(nbt);
	   	  tileData.setInteger("x", pos.getX());
	   	  tileData.setInteger("y", pos.getY());
	   	  tileData.setInteger("z", pos.getZ());
	   	  
	   	  if (!tileData.equals(copyTile))
	   	  {
	   		tile.readFromNBT(tileData);
	   		tile.markDirty();
	       	TileStackSyncEvent.Post event = new TileStackSyncEvent.Post(ItemStack.EMPTY, pos, tile, player, worldIn, blockData);
	       	MinecraftForge.EVENT_BUS.post(event);
	       	return true;
	      }
	   }
	   return false;
	}
}
