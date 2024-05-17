package com.evilnotch.lib.main.eventhandler;

import java.io.File;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.evilnotch.lib.minecraft.event.PickEvent;
import com.evilnotch.lib.minecraft.event.tileentity.BlockDataEvent;
import com.evilnotch.lib.minecraft.event.tileentity.TileDataEvent;
import com.evilnotch.lib.minecraft.network.IgnoreTilePacket;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.network.packet.PacketYawHead;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.minecraft.util.TileEntityUtil;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
			if(!profile.org.equals(profile.getId()))
			{
				System.out.println("Sending UUID Change to Client:" + e.player.getName());
				NetWorkHandler.INSTANCE.sendTo(new PacketUUID(e.player.getUniqueID()), (EntityPlayerMP)e.player);
			}
		}
		
//		updateSkinPackets((EntityPlayerMP) e.player);
	}
	
	/**
	 * force update skin render for you and all other players 
	 * note skin has to be changed before calling this method
	 */
    public static void updateSkinPackets(EntityPlayerMP p)
    {
		SPacketPlayerListItem removeInfo;
		SPacketDestroyEntities removeEntity;
		SPacketSpawnPlayer addNamed;
	    SPacketPlayerListItem addInfo;
	    SPacketRespawn respawn;
	    try
	    {
	      int entId = p.getEntityId();
	      removeInfo = new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER,p);
	      removeEntity = new SPacketDestroyEntities(new int[] { entId });
	      addNamed = new SPacketSpawnPlayer(p);
	      addInfo = new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER,p);
	      respawn = new SPacketRespawn(p.dimension, p.getServerWorld().getDifficulty(), p.getServerWorld().getWorldType(), p.getServer().getGameType());
	      
	     for (EntityPlayer pOnlines : p.mcServer.getPlayerList().getPlayers())
	     {
	        EntityPlayerMP pOnline = (EntityPlayerMP)pOnlines;
	        NetHandlerPlayServer con = pOnline.connection;
	        if (pOnline.equals(p))
	        {
		       con.sendPacket(removeInfo);
		       con.sendPacket(respawn);
		       con.sendPacket(addInfo);
			       
	      	  //gamemode packet
	      	   PlayerUtil.setGameTypeSafley(p,p.interactionManager.getGameType());
	      	   p.mcServer.getPlayerList().updatePermissionLevel(p);
	      	   p.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(p, (WorldServer) p.world);
	      	   p.world.updateAllPlayersSleepingFlag();
	      	  
	           //prevent the moved too quickly message
	      	   p.setRotationYawHead(p.rotationYawHead);
	           p.connection.setPlayerLocation(p.posX, p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
	           //trigger update exp
	           p.connection.sendPacket(new SPacketSetExperience(p.experience, p.experienceTotal, p.experienceLevel));

	           //send the current inventory - otherwise player would have an empty inventory
	           p.sendContainerToPlayer(p.inventoryContainer);
	           p.setPlayerHealthUpdated();
	           p.setPrimaryHand(p.getPrimaryHand());
	           p.connection.sendPacket(new SPacketHeldItemChange(p.inventory.currentItem));

	           InventoryPlayer inventory = p.inventory;
	           p.setHeldItem(EnumHand.MAIN_HAND, p.getHeldItemMainhand());
	           p.setHeldItem(EnumHand.OFF_HAND, p.getHeldItemOffhand());

	           //health && food
	           p.setHealth(p.getHealth());
	           FoodStats fs = p.getFoodStats();
	           fs.setFoodLevel(fs.getFoodLevel());
	           MainJava.proxy.setFoodSaturationLevel(fs, fs.getSaturationLevel());
	           p.interactionManager.setWorld(p.getServerWorld()); 
	          
	           con.sendPacket(new SPacketSpawnPosition(p.getPosition()));
	           
	           boolean end = true;
	           p.copyFrom(p, end);
	           net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerRespawnEvent(p, end);
	         }
	         else 
	         {
	           con.sendPacket(removeEntity);
	           con.sendPacket(removeInfo);
	           con.sendPacket(addInfo);
	           con.sendPacket(addNamed);
	         }
	      }
           //show && hide player to update their skin on their render
	       PlayerUtil.hidePlayer(p);
	       PlayerUtil.showPlayer(p);
	    }
	    catch (Exception localException) {}
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
