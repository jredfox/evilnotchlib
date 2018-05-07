package com.EvilNotch.lib.main.eventhandlers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class VanillaBugFixes {
	
	@SubscribeEvent
	public void onRespawn(PlayerRespawnEvent e)
	{
		if(e.player.world.isRemote)
			return;
		e.player.extinguish();
	}
	
	/**
     * This fixes the vanilla ItemMonsterSpawner Bugs
     */
    @SubscribeEvent
  	public void onVanillaEgg(PlayerInteractEvent.RightClickBlock e)
  	{
  		World w = e.getWorld();
  		EntityPlayer p = e.getEntityPlayer();
  	
  		if(p == null || w == null || w.isRemote || e.getHand() == null)
  			return;
  		ItemStack stack = p.getHeldItem(e.getHand());
  		BlockPos pos = e.getPos();
  		EnumFacing face = e.getFace();
  		if(pos == null || face == null)
  			return;
  		
  		TileEntity tile = w.getTileEntity(pos);
  		IBlockState state =  w.getBlockState(pos);
  		
  		if (stack == null || stack.getItem() != Items.SPAWN_EGG || tile == null || !(tile instanceof TileEntityMobSpawner) || state == null)
  			return;
  		
  		  if (tile instanceof TileEntityMobSpawner)
  		  {
  			  NBTTagCompound nbt = new NBTTagCompound();
  		   	  tile.writeToNBT(nbt);
  		   	  ResourceLocation stringId = ItemMonsterPlacer.getNamedIdFrom(stack);
  			  String name = stringId == null ? null : stringId.toString();
  			  if(name == null)
  				  return;
  			  NBTTagList spawnpot = new NBTTagList();
  			  NBTTagCompound entry = new NBTTagCompound();
  			  entry.setInteger("Weight", 1);
  			
  			  NBTTagCompound entity = new NBTTagCompound();
  			  entity.setString("id",name);
  			  
  			  entry.setTag("Entity", entity);
  			  spawnpot.appendTag(entry);
  			  
  			  nbt.setTag("SpawnPotentials", spawnpot);
  			  NBTTagCompound data = new NBTTagCompound();
  			  data.setString("id", name);
  			  nbt.setTag("SpawnData", data);
  			  if (!p.capabilities.isCreativeMode)
  			       stack.shrink(1);
  			  
  			  tile.readFromNBT(nbt);
  			  tile.markDirty();
  			  w.notifyBlockUpdate(pos, state, w.getBlockState(pos), 3);
  			  e.setCanceled(true);
  		  }
  	  }

}
