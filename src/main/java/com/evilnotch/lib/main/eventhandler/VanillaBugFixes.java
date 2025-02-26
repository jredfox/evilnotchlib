package com.evilnotch.lib.main.eventhandler;

import java.io.File;
import java.util.UUID;
import java.util.WeakHashMap;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.evilnotch.lib.minecraft.capability.client.ClientCapHooks;
import com.evilnotch.lib.minecraft.event.PickEvent;
import com.evilnotch.lib.minecraft.event.tileentity.BlockDataEvent;
import com.evilnotch.lib.minecraft.event.tileentity.TileDataEvent;
import com.evilnotch.lib.minecraft.network.IgnoreTilePacket;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PCCapDownload;
import com.evilnotch.lib.minecraft.network.packet.PCCapRem;
import com.evilnotch.lib.minecraft.network.packet.PacketSkin;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.network.packet.PacketYawHead;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.minecraft.util.TileEntityUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;

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
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class VanillaBugFixes {
	
	public static File worlDir = null;
	public static File playerDataNames = null;
	public static File playerDataDir = null;
	
	/**
	 * send the corrected uuid of the player to the client
	 */
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void join(PlayerLoggedInEvent e)
	{
		if(e.player.world.isRemote)
			return;
		
		GameProfile prof = e.player.getGameProfile();
		if(prof instanceof EvilGameProfile)
		{
			EvilGameProfile profile = (EvilGameProfile)prof;
			profile.login = null;//clear the login cache NBT as we are now fully logged in
			profile.iloginhooks = null;//clear iloginhooks as player has logged in now
			if(!profile.org.equals(profile.getId()))
			{
				System.out.println("Sending UUID Change to Client:" + e.player.getName());
				NetWorkHandler.INSTANCE.sendTo(new PacketUUID(e.player.getUniqueID()), (EntityPlayerMP)e.player);
			}
		}
		
		EntityPlayerMP p = (EntityPlayerMP) e.player;
		
		//Download You to Others
		if(!ClientCapHooks.getLoginCap((EntityPlayerMP) e.player).getClientCaps().hasNoTags())
			NetWorkHandler.INSTANCE.sendToAll(new PCCapDownload(p));
		
		//Download Other players to you
		for(EntityPlayerMP o : e.player.getServer().getPlayerList().getPlayers())
		{
			if(!ClientCapHooks.getLoginCap(o).getClientCaps().hasNoTags())
				NetWorkHandler.INSTANCE.sendTo(new PCCapDownload(o), p);
		}
	}
	
	/**
	 * Sync your player skin with yourself and all players tracking you.
	 */
	public static void syncSkin(EntityPlayerMP player)
	{
		if(Config.skinLowBandwidth)
			NetWorkHandler.INSTANCE.sendToTrackingAndPlayer(new PacketSkin(player), player);
		else
			NetWorkHandler.INSTANCE.sendToAll(new PacketSkin(player));
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
	public void track(PlayerEvent.StartTracking e)
	{
		if(!(e.getTarget() instanceof EntityPlayerMP))
			return;
		EntityPlayerMP targ = (EntityPlayerMP) e.getTarget();
		NetWorkHandler.INSTANCE.sendTo(new PacketYawHead(targ.getRotationYawHead(),targ.getEntityId()), (EntityPlayerMP)e.getEntityPlayer());
		NetWorkHandler.INSTANCE.sendTo(new PCCapDownload(targ), (EntityPlayerMP) e.getEntityPlayer());
		if(Config.skinCache && Config.skinLowBandwidth)
			NetWorkHandler.INSTANCE.sendTo(new PacketSkin(targ), (EntityPlayerMP) e.getEntityPlayer());
	}
	
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void logout(PlayerLoggedOutEvent e)
	{
		if(!(e.player instanceof EntityPlayerMP))
			return;
		NetWorkHandler.INSTANCE.sendToAll(new PCCapRem((EntityPlayerMP) e.player));
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

	public static void fixMcProfileProperties() 
	{
		MainJava.proxy.fixMcProfileProperties();
	}

	public static WeakHashMap<UUID, GameProfile> badProfiles = new WeakHashMap();

	public static GameProfile getCachedProfile(GameProfile profile, LoadingCache<GameProfile, GameProfile> insecureProfiles) 
	{
		GameProfile cached = insecureProfiles.getUnchecked(profile);
		if (cached.getProperties().isEmpty() && !badProfiles.containsKey(profile.getId()) && JavaUtil.isOnline("sessionserver.mojang.com")) 
		{
			insecureProfiles.invalidate(profile);
			cached = insecureProfiles.getUnchecked(profile);
			//Unavoidable second ping to sessionserver.mojang.com to avoid race condition bugs
			//In the very rare case where wifi goes down during the download then instantly back up for the ping over 1 mil chance you can dc from the world and go back in
			//If mojang services are down no need to reboot for the skin simply disconnect and reconnect to the world
			if (cached.getProperties().isEmpty() && JavaUtil.isOnline("sessionserver.mojang.com"))
			{
				System.err.println("Bad Profile UUID:" + profile.getId() + " Name:" + profile.getName());
				badProfiles.put(profile.getId(), profile);
			}
		}
		return cached;
	}

}