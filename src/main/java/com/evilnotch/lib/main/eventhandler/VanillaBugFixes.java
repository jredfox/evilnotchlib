package com.evilnotch.lib.main.eventhandler;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.evilnotch.lib.minecraft.event.PickEvent;
import com.evilnotch.lib.minecraft.event.tileentity.BlockDataEvent;
import com.evilnotch.lib.minecraft.event.tileentity.TileDataEvent;
import com.evilnotch.lib.minecraft.network.IgnoreTilePacket;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.network.packet.PacketYawHead;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.minecraft.util.TileEntityUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
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
	/**
	 * send the corrected uuid of the player to the client
	 */
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void join(PlayerLoggedInEvent e)
	{
		if(!e.player.world.isRemote && playerFlags.contains(e.player.getName()))
		{
			EntityPlayerMP playerIn = (EntityPlayerMP) e.player;
			PacketUUID id = new PacketUUID(playerIn.getEntityId(), playerIn.getUniqueID());
			NetWorkHandler.INSTANCE.sendTo(id, playerIn);
			playerFlags.remove(e.player.getName());
		}
//		else
//			NetWorkHandler.INSTANCE.sendTo(new PacketRefreshSkin(), (EntityPlayerMP) e.player);
	}
	
	/**
	 * data fixers
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void tilesync(TileDataEvent.Merge e)
	{
        if(e.tile instanceof TileEntityMobSpawner && !e.nbt.hasKey("SpawnPotentials") && e.nbt.hasKey("SpawnData"))
        {
        	NBTTagList list = new NBTTagList();
        	list.appendTag(new WeightedSpawnerEntity(1,(NBTTagCompound) e.nbt.getTag("SpawnData").copy() ).toCompoundTag());
        	e.nbt.setTag("SpawnPotentials", list);
        }
	}

	@SubscribeEvent(priority=EventPriority.HIGH)
	public void tilesync(BlockDataEvent.Post e)
	{
        if(e.world.isRemote && e.tile instanceof TileEntityMobSpawner)
        {
        	IgnoreTilePacket.ignoreTiles.add(e.pos);//tells your client to ignore the next tile entity packet sent to you
        }
	}
	
	/**
	 * fix mob spawner returning null stack
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void pick(PickEvent.Block e)
	{
		if(e.current.isEmpty() && e.tile instanceof TileEntityMobSpawner)
		{
			Block b = e.state.getBlock();
			e.current = new ItemStack(b,1,b.getMetaFromState(e.state));
		}
	}
	
	/**
	 * fix heads being on backwards when you start tracking a player
	 */
	 @SubscribeEvent(priority=EventPriority.HIGH)
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
    @SubscribeEvent(priority=EventPriority.HIGH)
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
    @SubscribeEvent(priority=EventPriority.HIGH)
  	public void onVanillaEgg(PlayerInteractEvent.RightClickBlock e)
  	{
  		World w = e.getWorld();
  		EntityPlayer p = e.getEntityPlayer();
  		ItemStack stack = p.getHeldItem(e.getHand());
  		BlockPos pos = e.getPos();
  		
  		if(!(stack.getItem() instanceof ItemMonsterPlacer))
  			return;
  		TileEntity tile = w.getTileEntity(pos);
  		
  		if (!(tile instanceof TileEntityMobSpawner))
  		{
  			return;
  		}
  		ResourceLocation loc = ItemMonsterPlacer.getNamedIdFrom(stack);
  		TileEntityUtil.setSpawnerId(loc, tile, p, stack);
  		PlayerUtil.rightClickBlockSucess(e, p);
  	}

}
