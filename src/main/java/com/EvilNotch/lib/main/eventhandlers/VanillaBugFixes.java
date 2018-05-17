package com.EvilNotch.lib.main.eventhandlers;

import com.EvilNotch.lib.Api.BlockApi;
import com.EvilNotch.lib.minecraft.BlockUtil;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.ItemUtil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VanillaBugFixes {
	
	/**
	 * if it's not the right tool set the break speed to 0
	 */
    @SubscribeEvent
    public void breakFix(PlayerEvent.BreakSpeed e)
    {
    	EntityPlayer p = e.getEntityPlayer();
    	ItemStack stack = EntityUtil.getActiveItemStack(p,EnumHand.MAIN_HAND);
    	if(stack.isEmpty() || !(stack.getItem() instanceof ItemTool))
    		return;
    	
    	IBlockState state =  e.getState();
    	Block b = state.getBlock();
    	String blockclazz = BlockUtil.getToolFromBlock(b,b.getMetaFromState(state),state);

    	boolean hasItemClazz = ItemUtil.hasToolClass(stack,blockclazz);
    	
    	if(!hasItemClazz && blockclazz != null && BlockApi.blocksModified.contains(b.getRegistryName() ))
    	{
    		e.setNewSpeed(1.0F);
//    		System.out.println("here:" + blockclazz );
    	}
    }
	
	/**
	 * use to occur up till integrated server then easter egg stopped working
	 */
    @SubscribeEvent
    public void notchFix(PlayerDropsEvent e)
    {
    	EntityPlayer player = e.getEntityPlayer();
    	if(player == null || player.world.isRemote)
    		return;
    	if(player.getName().equals("Notch"))
    	{
    		e.getDrops().add(player.dropItem(new ItemStack(Items.APPLE, 1), true, false) );
    	}
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
