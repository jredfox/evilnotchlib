package com.evilnotch.lib.minecraft.util;

import com.evilnotch.lib.minecraft.event.tileentity.BlockDataEvent;
import com.evilnotch.lib.minecraft.event.tileentity.TileDataEvent;
import com.evilnotch.lib.minecraft.event.tileentity.TileUseItemEvent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
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
		updateTileEntity(w, tile, pos);
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
	 * set a TileEntity's nbt without a player using an Item
	 */
	public static boolean setTileNBT(TileEntity tile, NBTTagCompound nbt)
	{
	   if (tile != null && nbt != null)
	   {
	   	  NBTTagCompound tileData = tile.writeToNBT(new NBTTagCompound());
	   	  NBTTagCompound copyTile = tileData.copy();
	       
	   	  TileDataEvent.Merge mergeEvent = new TileDataEvent.Merge(tile, tileData, nbt);
	   	  MinecraftForge.EVENT_BUS.post(mergeEvent);
	   	  tileData = mergeEvent.tileData;
	   	  nbt = mergeEvent.nbt;
	       
	   	  tileData.merge(nbt);
	   	  BlockPos pos = tile.getPos();
	   	  tileData.setInteger("x", pos.getX());
	   	  tileData.setInteger("y", pos.getY());
	   	  tileData.setInteger("z", pos.getZ());
	   	  
	   	  if (!tileData.equals(copyTile))
	   	  {
	   		 tile.readFromNBT(tileData);
	   		 tile.markDirty();
	   		 TileDataEvent.Post event = new TileDataEvent.Post(tile);
	   		 MinecraftForge.EVENT_BUS.post(event);
	   		 return true;
	   	  }
	   }
	   return false;
	}
	
	/**
	 * set a tile's nbt with option of player denial of permissions and work with the itemstack
	 */
	public static boolean setTileNBT(TileEntity tile, NBTTagCompound nbt, EntityPlayer player, ItemStack stack)
	{
		if (tile != null && nbt != null)
		{
			TileUseItemEvent.Permissions permissions = new TileUseItemEvent.Permissions(tile, player, stack);
		   	permissions.canUseCommand = true;
		   	MinecraftForge.EVENT_BUS.post(permissions);
		   	if ((permissions.opsOnly) && (!permissions.canUseCommand))
		   	{
		    	return false;
		   	}
		   	NBTTagCompound tileData = tile.writeToNBT(new NBTTagCompound());
		   	NBTTagCompound copyTile = tileData.copy();
		       
		   	TileUseItemEvent.Merge mergeEvent = new TileUseItemEvent.Merge(tile, player, stack, tileData, nbt);
		   	MinecraftForge.EVENT_BUS.post(mergeEvent);
		   	tileData = mergeEvent.tileData;
		   	nbt = mergeEvent.nbt;
		       
		   	tileData.merge(nbt);
		   	BlockPos pos = tile.getPos();
		   	tileData.setInteger("x", pos.getX());
		   	tileData.setInteger("y", pos.getY());
		   	tileData.setInteger("z", pos.getZ());
		   	  
		   	if (!tileData.equals(copyTile))
		   	{
		   		tile.readFromNBT(tileData);
		   		tile.markDirty();
		   		TileUseItemEvent.Post event = new TileUseItemEvent.Post(tile, player, stack);
		       	MinecraftForge.EVENT_BUS.post(event);
		       	return true;
		    }
		 }
		 return false;
	}
	
	
	/**
	 * used for ItemBlock placement
	 */
	public static boolean placeTileNBT(World worldIn, BlockPos pos, NBTTagCompound nbt, EntityPlayer player, ItemStack stack)
	{
		return placeTileNBT(worldIn.getTileEntity(pos), nbt, player, stack);
	}

	/**
	 * set a tile entity nbt on placement of like a block or special item
	 */
	public static boolean placeTileNBT(TileEntity tile, NBTTagCompound nbt, EntityPlayer player, ItemStack stack)
	{
	   if (tile != null && nbt != null)
	   {
	   	   BlockDataEvent.Permissions permissions = new BlockDataEvent.Permissions(tile, player, stack);
	   	   permissions.canUseCommand = true;
	   	   MinecraftForge.EVENT_BUS.post(permissions);
	   	   if ((permissions.opsOnly) && (!permissions.canUseCommand))
	   	   {
	        	return false;
	   	   }
	   	   NBTTagCompound tileData = tile.writeToNBT(new NBTTagCompound());
	   	   NBTTagCompound copyTile = tileData.copy();
	       
	   	   BlockDataEvent.Merge mergeEvent = new BlockDataEvent.Merge(tile, player, stack, tileData, nbt);
	   	   MinecraftForge.EVENT_BUS.post(mergeEvent);
	   	   tileData = mergeEvent.tileData;
	   	   nbt = mergeEvent.nbt;
	       
	   	   tileData.merge(nbt);
	   	   BlockPos pos = tile.getPos();
	   	   tileData.setInteger("x", pos.getX());
	   	   tileData.setInteger("y", pos.getY());
	   	   tileData.setInteger("z", pos.getZ());
	   	  
	   	   if (!tileData.equals(copyTile))
	   	   {
	   	      tile.readFromNBT(tileData);
	   	      tile.markDirty();
	   		  BlockDataEvent.Post event = new BlockDataEvent.Post(tile, player, stack);
	          MinecraftForge.EVENT_BUS.post(event);
	          return true;
	      }
	   }
	   return false;
	}
	
	/**
	 * this is a method that will store te to the stack saving everything including mob spawner delay
	 */
    public static void storeTEInStack(ItemStack stack, TileEntity te)
    {
        NBTTagCompound nbttagcompound = te.writeToNBT(new NBTTagCompound());
        nbttagcompound.removeTag("x");
        nbttagcompound.removeTag("y");
        nbttagcompound.removeTag("z");
        nbttagcompound.removeTag("id");

        if (stack.getItem() == Items.SKULL && nbttagcompound.hasKey("Owner"))
        {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
            nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
            stack.setTagCompound(nbttagcompound3);
        }
        else
        {
            stack.setTagInfo("BlockEntityTag", nbttagcompound);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.appendTag(new NBTTagString("(+NBT)"));
            nbttagcompound1.setTag("Lore", nbttaglist);
            stack.setTagInfo("display", nbttagcompound1);
        }
    }
    
	public static void setSpawnerId(ResourceLocation loc, TileEntity tile) 
	{
		setSpawnerId(loc, tile, null, null);
	}
	
    /**
     * if player is placing a mob spawner do not use this method as this is for non placeTileNBT() methods
     */
	public static void setSpawnerId(ResourceLocation loc, TileEntity tile, EntityPlayer player, ItemStack stack) 
	{
  		NBTTagCompound nbt = new NBTTagCompound();
  		NBTTagCompound data = new NBTTagCompound();
  		data.setString("id", loc.toString());
  		nbt.setTag("SpawnData", data);
  		if(player != null)
  			TileEntityUtil.setTileNBT(tile, nbt, player, stack);
  		else
  			TileEntityUtil.setTileNBT(tile, nbt);
	}
	
}
