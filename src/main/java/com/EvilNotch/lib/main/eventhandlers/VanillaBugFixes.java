package com.EvilNotch.lib.main.eventhandlers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;

import com.EvilNotch.lib.Api.BlockApi;
import com.EvilNotch.lib.minecraft.BlockUtil;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.ItemUtil;
import com.EvilNotch.lib.minecraft.MinecraftUtil;
import com.EvilNotch.lib.minecraft.TestProps;
import com.EvilNotch.lib.util.JavaUtil;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

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
  		   	  ResourceLocation loc = ItemMonsterPlacer.getNamedIdFrom(stack);
  			  String name = loc == null ? null : loc.toString();
  			  if(name == null)
  				  return;
  			  
  			  //spawndata reset
  			  NBTTagCompound data = new NBTTagCompound();
  			  data.setString("id", loc.toString());
  			  nbt.setTag("SpawnData", data);
  			  
  			  //spawn potentials reset
  			  NBTTagList pot = new NBTTagList();
  			  pot.appendTag(new WeightedSpawnerEntity(1,data.copy()).toCompoundTag() );
  			  nbt.setTag("SpawnPotentials", pot);
  			  
  			  if (!p.capabilities.isCreativeMode)
  			       stack.shrink(1);
  			  
  			  tile.readFromNBT(nbt);
  			  tile.markDirty();
  			  w.notifyBlockUpdate(pos, state, w.getBlockState(pos), 3);
  			  e.setCanceled(true);
  		  }
  	}

}
