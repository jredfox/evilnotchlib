package com.evilnotch.lib.minecraft.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketYawOffset;
import com.evilnotch.lib.minecraft.network.packet.PacketYawPitch;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.common.DimensionManager;

public class TeleportUtil {
	
	/**
	 * teleport an entity to spawn across dimenions support
	 */
    public static void teleportSpawn(Entity ep,MinecraftServer server,int dimension) throws WrongUsageException 
    {
    	World w = server.getWorld(dimension);
    	BlockPos bp = w.provider.getRandomizedSpawnPoint();
    	double x = bp.getX() + 0.5;
		double y = bp.getY();
		double z = bp.getZ() + 0.5;
		ep.setPosition(x, y, z);
		
		if(ep.world.provider.getDimension() != dimension)
			ep.setWorld(w);
		ep.world.getChunkFromBlockCoords(bp);

		while (!ep.world.getCollisionBoxes(ep, ep.getEntityBoundingBox()).isEmpty() && ep.posY < 256.0D)
	    {
	    	ep.setPosition(ep.posX, ep.posY + 1.0D, ep.posZ);
	    }
		telePortEntitySync(ep, server, ep.posX,ep.posY,ep.posZ, ep.rotationYaw, ep.rotationPitch, dimension);
	}
    
	/**
	 * teleport entire stack
	 */
	public static void teleportStack(Entity index,MinecraftServer server,double x, double y, double z, float yaw, float pitch, int traveldim) throws WrongUsageException
	{
		teleportStackAtIndex(index.getLowestRidingEntity(),server,x,y,z,yaw,pitch,traveldim);
	}
	
	/**
	 * used for /tpdim as requested by micah_laster to save all mounts above you
	 */
	public static void teleportStackAtIndex(Entity entity,MinecraftServer server,double x, double y, double z, float yaw, float pitch, int traveldim) throws WrongUsageException
	{
		if(entity.getLowestRidingEntity() != entity)
			entity.dismountRidingEntity();
		
		//get all passengers and riding entities
        List<Entity> list = (ArrayList<Entity>) JavaUtil.toArray(entity.getRecursivePassengers());
        list.add(0,entity);
       
        List<Entity> toRide = new ArrayList();
        for(Entity e : list)
        	toRide.add(e.getRidingEntity());

        for(int i=0;i<list.size();i++)
        {
        	Entity e = list.get(i);
        	e = telePortEntity(e,server,x,y,z,yaw,pitch,traveldim);//keep yaw/pitch so they all don't face the same way only the base mob
        	setEntity(e,toRide);
        }
        //remount entities then sync players
        for(int i=0;i<list.size();i++)
        {
        	Entity e = list.get(i);
        	Entity other = toRide.get(i);
        	if(other != null)
        		e.startRiding(other,true);
        	
            if (e instanceof EntityPlayerMP) 
            {
               	updatePassengerClient((EntityPlayerMP)e,e);
            }
        }
	}
    public static void updatePassengerClient(EntityPlayerMP playerMP,Entity e) 
    {
    	if(e != null)
    	{
    		System.out.println("Syncing Packets to:" + playerMP.getName() + " with:" + e.getName());
    		playerMP.connection.sendPacket(new SPacketSetPassengers(e));
    	}
	}

	protected static void setEntity(Entity changed, List<Entity> rides) 
	{
		for(int i=0;i<rides.size();i++)
		{
			Entity e = rides.get(i);
			if(e == null)
			{
				continue;
			}
			if(e.getUniqueID().toString().equals(changed.getUniqueID().toString()) && e != changed )
			{
				rides.set(i, changed);
				System.out.println("changed:" + changed.getName());
			}
		}
	}
	
	/**
	 * work around for the shitty cheat detection system in vanilla
	 */
	public static void captureCoords(EntityPlayerMP player)
	{
		NetHandlerPlayServer connection = player.connection;
		connection.captureCurrentPosition();
		
		connection.lowestRiddenEnt = player.getLowestRidingEntity();
		
		connection.lowestRiddenX = player.posX;
		connection.lowestRiddenY = player.posY;
		connection.lowestRiddenZ = player.posZ;
		
		connection.lowestRiddenX1 = player.posX;
		connection.lowestRiddenY1 = player.posY;
		connection.lowestRiddenZ1 = player.posZ;
	}
	
	/**
	 * first parameter is player to teleport
	 * Doesn't use the vanilla teleport system so set xyz's of player before hand of the new world in the portal as well as yaw pitch and maybe head
	 * @throws WrongUsageException 
	 */
	public static Entity telePortEntity(Entity e,MinecraftServer server, double x, double y, double z,float yaw,float pitch, int traveldim) throws WrongUsageException
	{
		if(e.isDead)
		{
			return e;
		}
    	e.dismountRidingEntity();
    	e.removePassengers();
        int prevDim = e.dimension;
    	
        if(traveldim != prevDim)
        {
        	return teleportEntityInterdimentional(e,server,prevDim,traveldim,x,y,z,yaw,pitch);
        }
        return doTeleport(e, x, y, z,yaw,pitch);
	}
    /**
     * This is the black magic responsible for teleporting players between dimensions!
     */
    public static EntityPlayer teleportPlayerInterdimentional(EntityPlayerMP player, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) 
    {
        WorldServer sourceWorld = server.getWorld(sourceDim);
        WorldServer targetWorld = server.getWorld(targetDim);
        PlayerList playerList = server.getPlayerList();
        
        sourceWorld.playerEntities.remove(player);
    	if(sourceDim == 1)
    		removeDragonBars(sourceWorld);//vanilla bug fix 1.9+

        player.dimension = targetDim;
        player.connection.sendPacket(new SPacketRespawn(player.dimension, targetWorld.getDifficulty(), targetWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
        playerList.updatePermissionLevel(player);
        sourceWorld.removeEntityDangerously(player);
        player.isDead = false;

        //region Transfer to world
        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
        targetWorld.spawnEntity(player);
        targetWorld.updateEntityWithOptionalForce(player, false);
        player.setWorld(targetWorld);

        playerList.preparePlayer(player, sourceWorld);
        if(player.posX != xCoord || player.posY != yCoord|| player.posZ != zCoord || player.rotationYaw != yaw || player.rotationPitch != pitch)
        {
        	System.out.println("mod incompatiblity detected overriding it!");
        	player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
        }
        captureCoords(player);
        
        //update yaw/pitch and sync shoulders
        sysncShoulders(player,yaw,pitch);
        
        player.interactionManager.setWorld(targetWorld);
        player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
        
        //additions to brandon's core
        player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
        player.setHealth(player.getHealth());
        
        playerList.updateTimeAndWeatherForPlayer(player, targetWorld);
        playerList.syncPlayerInventory(player);

        for (PotionEffect potioneffect : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, sourceDim, targetDim);
        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        
        return player;
    }
    
    public static void sysncShoulders(EntityPlayerMP player, float yaw, float pitch) 
    {
        player.renderYawOffset = yaw;
        player.prevRenderYawOffset = yaw;
        PacketYawOffset packet = new PacketYawOffset(yaw, player.getEntityId());
//        PacketYawPitch packet2 = new PacketYawPitch(yaw, pitch, player.getEntityId());
        NetWorkHandler.INSTANCE.sendToTrackingAndPlayer(packet, player);
	}

	public static Entity teleportEntityInterdimentional(Entity entity, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) throws WrongUsageException 
    {
        if (entity == null || entity.isDead || entity.world == null || entity.world.isRemote) 
        {
            return null;
        }
        
        if(!DimensionManager.isDimensionRegistered(targetDim))
        	throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
        
        if(entity instanceof EntityPlayerMP)
        {
        	return teleportPlayerInterdimentional((EntityPlayerMP)entity,server,sourceDim,targetDim,xCoord,yCoord,zCoord,yaw,pitch);
        }
        
        WorldServer sourceWorld = server.getWorld(sourceDim);
        WorldServer targetWorld = server.getWorld(targetDim);
        entity.dimension = targetDim;
        Entity newEntity = EntityList.newEntity(entity.getClass(), targetWorld);
        
        if (newEntity != null) 
        {
        	try
        	{
        		newEntity.copyDataFromOld(entity);
        		newEntity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        		boolean flag = newEntity.forceSpawn;
        		newEntity.forceSpawn = true;
        		targetWorld.spawnEntity(newEntity);
        		newEntity.forceSpawn = flag;
        		targetWorld.updateEntityWithOptionalForce(newEntity, false);
        		
                sourceWorld.removeEntity(entity);
                entity.setDropItemsWhenDead(false);
                sourceWorld.updateEntityWithOptionalForce(entity, false);
        	}
        	catch(Throwable t)
        	{
        		t.printStackTrace();
        		return null;
        	}
        }
        sourceWorld.resetUpdateEntityTick();
        targetWorld.resetUpdateEntityTick();

        return newEntity;
    }
    /**
     * Call this after player is removed from the world
     */
    public static void removeDragonBars(World end) 
    {
		DragonFightManager fightManager = ((WorldProviderEnd)end.provider).getDragonFightManager();
		if(fightManager != null)
		{
			fightManager.updateplayers();
		}
    }
	/**
     *supports teleporting entities from location to location in the same dimension doesn't support actual cross dimensional teleport
     */
    public static Entity doTeleport(Entity e, double x, double y, double z,float yaw, float pitch)
    {	
        int chunkOldX = (int)e.posX >> 4;
 		int chunkOldZ = (int)e.posZ >> 4;
        int chunkX = (int)x >> 4;
		int chunkZ = (int)z >> 4;
		
        if (e instanceof EntityPlayerMP)
        {
            Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);
            EntityPlayerMP player = (EntityPlayerMP)e;
            player.setLocationAndAngles(chunkX, y, chunkZ, yaw, pitch);
            player.connection.setPlayerLocation(x, y, z, yaw, pitch, set);
            captureCoords(player);
            sysncShoulders(player,yaw,pitch);
        }
        else
        {
            e.setLocationAndAngles(x, y, z, yaw, pitch);    
        }
        
        if (!(e instanceof EntityLivingBase) || !((EntityLivingBase)e).isElytraFlying())
        {
            e.motionY = 0.0D;
            e.onGround = true;
        }

        //vanilla hotfix if entity isn't loaded and not added to the chunk do this don't check players because sometimes the world will randomly remove them causing chunks not to load from players
       if(!(e instanceof EntityPlayer))
       {
        	if(!MinecraftUtil.isChunkLoaded(e.world, chunkX, chunkZ, true))
        	{
        		//remove from old chunk
        		if(chunkOldX != chunkX || chunkOldZ != chunkZ)
        		{
        			Chunk chunkOld = e.world.getChunkFromChunkCoords(chunkOldX,chunkOldZ);
        			chunkOld.removeEntity(e);
        		}
        		Chunk chunk = e.world.getChunkFromChunkCoords(chunkX,chunkZ);
        		/*if(!containsEntity(chunk.getEntityLists(),e))
        		{
        			System.out.println("here adding:" + e.getName() + " \"chunk not loaded\"");
        			chunk.addEntity(e);
        		}*/
        	}
        	e.world.updateEntityWithOptionalForce(e, false);
        }
      	
        return e;
    }

	public static boolean containsEntity(ClassInheritanceMultiMap<Entity>[] list,Entity e) 
	{
		for(int i=0;i<list.length;i++)
		{
			ClassInheritanceMultiMap map = list[i];
			Iterator<Entity> it = map.iterator();
			while(it.hasNext())
			{
				Entity e2 = it.next();
				if(e == e2)
					return true;
			}
		}
		return false;
	}
	
    /**
     * use this for individual entity telports otherwise use teleport stack or teleport stack at index
     */
	public static void telePortEntitySync(Entity entity, MinecraftServer server, double x, double y, double z,float yaw, float pitch, int traveldim) throws WrongUsageException 
	{	
		Entity riding = null;
		if(entity instanceof EntityPlayerMP)
		{
			riding = entity.getRidingEntity();
		}
		
		telePortEntity(entity, server, x, y, z, yaw, pitch, traveldim);
		
		if(riding != null)
		{
			updatePassengerClient((EntityPlayerMP) entity, entity);
		}
	}

}
