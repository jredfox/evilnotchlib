package com.evilnotch.lib.minecraft.util;

import javax.annotation.Nullable;

import com.evilnotch.lib.minecraft.event.TileDataEvent;
import com.evilnotch.lib.minecraft.event.TileStackSyncEvent;

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
	 * set a tile entity nbt from anything except when placing an itemstack
	 */
	public static boolean setTileNBT(World worldIn,TileEntity tile, NBTTagCompound nbt)
	{
	   if (tile != null && nbt != null)
	   {
	   	  NBTTagCompound tileData = tile.writeToNBT(new NBTTagCompound());
	   	  NBTTagCompound copyTile = tileData.copy();
	       
	   	  TileDataEvent.Merge mergeEvent = new TileDataEvent.Merge(tile, worldIn, tileData, nbt);
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
	   		 TileDataEvent.Post event = new TileDataEvent.Post(tile, worldIn);
	   		 MinecraftForge.EVENT_BUS.post(event);
	   		 return true;
	   	  }
	   }
	   return false;
	}
	
	
	/**
	 * used for itemblock placement
	 */
	public static boolean placeTileNBT(World worldIn, BlockPos pos, EntityPlayer player, NBTTagCompound nbt, boolean blockData)
	{
		return placeTileNBT(worldIn,worldIn.getTileEntity(pos),player,nbt,blockData);
	}

	/**
	 * set a tile entity nbt from an Itemstack use
	 */
	public static boolean placeTileNBT(World worldIn,TileEntity tile, EntityPlayer player, NBTTagCompound nbt, boolean blockData)
	{
	   if (tile != null && nbt != null)
	   {
		   ItemStack stack = player == null ? ItemStack.EMPTY : player.getActiveItemStack();
	   	  TileStackSyncEvent.Permissions permissions = new TileStackSyncEvent.Permissions(stack, tile, player, worldIn, blockData);
	   	  permissions.canUseCommand = true;
	   	  MinecraftForge.EVENT_BUS.post(permissions);
	   	  if ((permissions.opsOnly) && (!permissions.canUseCommand))
	   	  {
	       	return false;
	   	  }
	   	  NBTTagCompound tileData = tile.writeToNBT(new NBTTagCompound());
	   	  NBTTagCompound copyTile = tileData.copy();
	       
	   	  TileStackSyncEvent.Merge mergeEvent = new TileStackSyncEvent.Merge(stack, tile, player, worldIn, blockData, tileData, nbt);
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
	       	TileStackSyncEvent.Post event = new TileStackSyncEvent.Post(stack, tile, player, worldIn, blockData);
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
    
    /**
     * player can be null but, use when available
     */
	public static void setSpawnerId(ResourceLocation loc, TileEntity tile, World w, EntityPlayer p) 
	{
  		NBTTagCompound nbt = new NBTTagCompound();
  		NBTTagCompound data = new NBTTagCompound();
  		data.setString("id", loc.toString());
  		nbt.setTag("SpawnData", data);
  		TileEntityUtil.setTileNBT(w, tile, nbt);
	}
	
}
