package com.evilnotch.lib.main.eventhandler;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.evilnotch.lib.api.BlockApi;
import com.evilnotch.lib.minecraft.event.PickEvent;
import com.evilnotch.lib.minecraft.event.TileStackSyncEvent;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.network.packet.PacketYawHead;
import com.evilnotch.lib.minecraft.util.BlockUtil;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.ItemUtil;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class VanillaBugFixes {
	
	public static File worlDir = null;
	public static File playerDataNames = null;
	public static File playerDataDir = null;
	
	public static Set<String> playerFlags = new HashSet();
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void join(PlayerLoggedInEvent e)
	{
		if(!e.player.world.isRemote && playerFlags.contains(e.player.getName()))
		{
			EntityPlayerMP playerIn = (EntityPlayerMP) e.player;
			PacketUUID id = new PacketUUID(playerIn.getUniqueID());
			NetWorkHandler.INSTANCE.sendTo(id, playerIn);
			playerFlags.remove(e.player.getName());
		}
	}

	/**
	 * if it's not the right tool set the break speed to 0
	 */
    /*@SubscribeEvent
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
    }*/
	
	@SubscribeEvent
	public void tilesync(TileStackSyncEvent.Merge e)
	{
		TileEntity tileentity = e.tile;
        if(tileentity instanceof TileEntityMobSpawner && !e.nbt.hasKey("SpawnPotentials"))
        {
        	NBTTagList list = new NBTTagList();
        	list.appendTag(new WeightedSpawnerEntity(1,(NBTTagCompound) e.nbt.getTag("SpawnData").copy() ).toCompoundTag());
        	e.nbt.setTag("SpawnPotentials", list);
        }
	}
	
	@SubscribeEvent
	public void tilesync(TileStackSyncEvent.Post e)
	{
        if(e.world.isRemote)
        {
        	if(e.tile instanceof TileEntityMobSpawner)
        		SPacketUpdateTileEntity.toIgnore.add(e.pos);//tells your client to ignore the next tile entity packet sent to you
        }
	}
	
	/**
	 * fix mob spawner returning null stack
	 * @param e
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void pick(PickEvent.Block e)
	{
		if(e.tile instanceof TileEntityMobSpawner)
		{
			Block b = e.state.getBlock();
			e.current = new ItemStack(b,1,b.getMetaFromState(e.state));
		}
	}
	
	/**
	 * fix heads being on backwards when you start tracking a player
	 */
	@SubscribeEvent
	public void headFix(PlayerEvent.StartTracking e)
	{
		if(!(e.getTarget() instanceof EntityPlayerMP))
			return;
		EntityPlayerMP targ = (EntityPlayerMP) e.getTarget();
		NetWorkHandler.INSTANCE.sendTo(new PacketYawHead(targ.getRotationYawHead(),targ.getEntityId()), (EntityPlayerMP)e.getEntityPlayer());
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
