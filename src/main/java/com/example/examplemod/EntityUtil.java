package com.example.examplemod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.demo.DemoWorldManager;

public class EntityUtil {
	
	/**
	 * first parameter is player to teleport
	 * Doesn't use the vanilla teleport system so set xyz's of player before hand of the new world in the portal as well as yaw pitch and maybe head
	 */
	public static void telePortEntity(Entity e,MinecraftServer server, double x, double y, double z,float yaw,float pitch, int traveldim)
	{
        int prevDim = e.dimension;
    	
        if(traveldim != prevDim)
        {
        	teleportEntityInterdimentional(e,server,prevDim,traveldim,x,y,z,e.rotationYaw,e.rotationPitch);
        	World newWorld = e.worldObj;
        }
        if(e.posX != x || e.posY != y || e.posZ != z)
        {
        	doTeleport(e, x, y, z);
        }
	}
    /**
     * This is the black magic responsible for teleporting players between dimensions!
     */
    public static EntityPlayer teleportPlayerInterdimentional(EntityPlayerMP player, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) 
    {
        WorldServer sourceWorld = server.worldServerForDimension(sourceDim);
        WorldServer targetWorld = server.worldServerForDimension(targetDim);
        ServerConfigurationManager playerList = server.getConfigurationManager();

        player.dimension = targetDim;
        player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, targetWorld.difficultySetting, targetWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
//        playerList.updatePermissionLevel(player);
        
        sourceWorld.removePlayerEntityDangerously(player);
        player.isDead = false;

        //region Transfer to world
        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        player.playerNetServerHandler.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
        targetWorld.spawnEntityInWorld(player);
        targetWorld.updateEntityWithOptionalForce(player, false);
        player.setWorld(targetWorld);

        playerList.func_72375_a(player, sourceWorld);
        player.playerNetServerHandler.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
        player.theItemInWorldManager.setWorld(targetWorld);
        player.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(player.capabilities));
        
        //additions to brandon's core
        player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
//        player.connection.sendPacket(new Packet);//health update for mods that don't sync dimension change to start displaying weird
        
        playerList.updateTimeAndWeatherForPlayer(player, targetWorld);
        playerList.syncPlayerInventory(player);

        Iterator iterator = player.getActivePotionEffects().iterator();

        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect)iterator.next();
            player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, sourceDim, targetDim);
        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        
//    	if(sourceDim == 1)
//    		removeDragonBars(sourceWorld);//vanilla bug fix 1.9+
        
        return player;
    }
    /**
     * respawns player in same place and dimension as original just updates forges capabilities
     */
    public static EntityPlayerMP updateForgeCapabilities(EntityPlayerMP p_72368_1_, int p_72368_2_, boolean p_72368_3_)
    {
    	MinecraftServer mcServer = p_72368_1_.mcServer;
        World world = mcServer.worldServerForDimension(p_72368_2_);
        if (world == null)
        {
            p_72368_2_ = 0;
        }

        p_72368_1_.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(p_72368_1_);
        p_72368_1_.getServerForPlayer().getEntityTracker().removeEntityFromAllTrackingPlayers(p_72368_1_);
        p_72368_1_.getServerForPlayer().getPlayerManager().removePlayer(p_72368_1_);
        mcServer.getConfigurationManager().playerEntityList.remove(p_72368_1_);
        mcServer.worldServerForDimension(p_72368_1_.dimension).removePlayerEntityDangerously(p_72368_1_);
        ChunkCoordinates chunkcoordinates = p_72368_1_.getBedLocation(p_72368_2_);
        boolean flag1 = p_72368_1_.isSpawnForced(p_72368_2_);
        p_72368_1_.dimension = p_72368_2_;
        Object object;

        if (mcServer.isDemo())
        {
            object = new DemoWorldManager(mcServer.worldServerForDimension(p_72368_1_.dimension));
        }
        else
        {
            object = new ItemInWorldManager(mcServer.worldServerForDimension(p_72368_1_.dimension));
        }

        EntityPlayerMP entityplayermp1 = new EntityPlayerMP(mcServer, mcServer.worldServerForDimension(p_72368_1_.dimension), p_72368_1_.getGameProfile(), (ItemInWorldManager)object);
        entityplayermp1.playerNetServerHandler = p_72368_1_.playerNetServerHandler;
        entityplayermp1.clonePlayer(p_72368_1_, p_72368_3_);
        entityplayermp1.dimension = p_72368_2_;
        entityplayermp1.setEntityId(p_72368_1_.getEntityId());
        WorldServer worldserver = mcServer.worldServerForDimension(p_72368_1_.dimension);
        func_72381_a(entityplayermp1, p_72368_1_, worldserver);
        ChunkCoordinates chunkcoordinates1;

        //bed fixer
        if (chunkcoordinates != null)
        {
            chunkcoordinates1 = EntityPlayer.verifyRespawnCoordinates(mcServer.worldServerForDimension(p_72368_1_.dimension), chunkcoordinates, flag1);

            if (chunkcoordinates1 != null)
            {
//                entityplayermp1.setLocationAndAngles((double)((float)entityplayermp1.posX + 0.5F), (double)((float)entityplayermp1.posY + 0.1F), (double)((float)entityplayermp1.posZ + 0.5F), 0.0F, 0.0F);
                entityplayermp1.setSpawnChunk(chunkcoordinates, flag1);
            }
            else
            {
                entityplayermp1.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
            }
        }

        worldserver.theChunkProviderServer.loadChunk((int)entityplayermp1.posX >> 4, (int)entityplayermp1.posZ >> 4);

        while (!worldserver.getCollidingBoundingBoxes(entityplayermp1, entityplayermp1.boundingBox).isEmpty())
        {
            entityplayermp1.setPosition(entityplayermp1.posX, entityplayermp1.posY + 1.0D, entityplayermp1.posZ);
        }

        entityplayermp1.playerNetServerHandler.sendPacket(new S07PacketRespawn(entityplayermp1.dimension, entityplayermp1.worldObj.difficultySetting, entityplayermp1.worldObj.getWorldInfo().getTerrainType(), entityplayermp1.theItemInWorldManager.getGameType()));
        chunkcoordinates1 = worldserver.getSpawnPoint();
        entityplayermp1.playerNetServerHandler.setPlayerLocation(entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch);
        entityplayermp1.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(chunkcoordinates1.posX, chunkcoordinates1.posY, chunkcoordinates1.posZ));
        entityplayermp1.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityplayermp1.experience, entityplayermp1.experienceTotal, entityplayermp1.experienceLevel));
        mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(entityplayermp1, worldserver);
        worldserver.getPlayerManager().addPlayer(entityplayermp1);
        worldserver.spawnEntityInWorld(entityplayermp1);
        mcServer.getConfigurationManager().playerEntityList.add(entityplayermp1);
        entityplayermp1.addSelfToInternalCraftingInventory();
        entityplayermp1.setHealth(entityplayermp1.getHealth());
        
        FMLCommonHandler.instance().firePlayerRespawnEvent(entityplayermp1);
        return entityplayermp1;
    }
    private static void func_72381_a(EntityPlayerMP newPlayer, EntityPlayerMP oldPlayer, World w)
    {
        newPlayer.theItemInWorldManager.initializeGameType(w.getWorldInfo().getGameType());
        if (oldPlayer != null)
        {
        	newPlayer.theItemInWorldManager.setGameType(oldPlayer.theItemInWorldManager.getGameType());
        }
    }
    
	public static Entity teleportEntityInterdimentional(Entity entity, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) 
    {
        if (entity == null || entity.isDead || entity.worldObj == null || entity.worldObj.isRemote) 
        {
            return null;
        }
        
        if(entity instanceof EntityPlayerMP)
        {
        	return teleportPlayerInterdimentional((EntityPlayerMP)entity,server,sourceDim,targetDim,xCoord,yCoord,zCoord,yaw,pitch);
        }
        
        WorldServer sourceWorld = server.worldServerForDimension(sourceDim);
        WorldServer targetWorld = server.worldServerForDimension(targetDim);

        //Set the entity dead before calling changeDimension. Still need to call changeDimension for things like minecarts which will drop their contents otherwise.
        if (!entity.isDead && entity instanceof EntityMinecart) 
        {
            entity.isDead = true;
            entity.travelToDimension(targetDim);
            entity.isDead = false;
        }

        entity.dimension = targetDim;

        sourceWorld.removeEntity(entity);
        entity.isDead = false;
        entity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        sourceWorld.updateEntityWithOptionalForce(entity, false);

        Entity newEntity = newEntity(entity.getClass(), targetWorld);
        if (newEntity != null) 
        {
        	try
        	{
//        		FieldAcess.methodEnt_copyDataFromOld.invoke(newEntity, entity);
        		newEntity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
        		boolean flag = newEntity.forceSpawn;
        		newEntity.forceSpawn = true;
        		targetWorld.spawnEntityInWorld(newEntity);
        		newEntity.forceSpawn = flag;
        		targetWorld.updateEntityWithOptionalForce(newEntity, false);
        	}
        	catch(Throwable t)
        	{
        		t.printStackTrace();
        		return null;
        	}
        }

        entity.isDead = true;
        sourceWorld.resetUpdateEntityTick();
        targetWorld.resetUpdateEntityTick();

        return newEntity;
    }
    public static Entity newEntity(Class<? extends Entity> oclass, World world) 
    {
        Entity entity = null;
    	try
        {
            if (oclass != null)
            {
                entity = (Entity)oclass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    	return entity;
	}
	/**
     * Call this after player is removed from the world
     * 1.9+
     */
    public static void removeDragonBars(World end) {}
	/**
     * Perform the actual teleport from xyz to xyz doesn't handle cross dimensions
     */
    public static void doTeleport(Entity e, double x, double y, double z)
    {
        if (e instanceof EntityPlayerMP)
        {
            e.mountEntity((Entity)null);
            if(e.posX != x || e.posY != y || e.posZ != z)
            {
            	e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
            	((EntityPlayerMP)e).playerNetServerHandler.setPlayerLocation(x, y, z, e.rotationYaw, e.rotationPitch);
            }
        }
        else
        {
            e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
        }

        if (!(e instanceof EntityLivingBase))
        {
            e.motionY = 0.0D;
            e.onGround = true;
        }
    }
    
	/**
	 * Returns the uuidFile or cached file based on uuid boolean
	 * @param player
	 * @param uuid
	 * @return player file
	 */
	public static File getPlayerFile(EntityPlayer player,boolean uuid)
	{
		if(uuid)
		{
			return new File(LibEvents.playerDataDir,player.getUniqueID().toString() + ".dat");
		}
		else
			return new File(LibEvents.playerDataNames,player.getCommandSenderName() + ".dat");
	}
	/**
	 * Update Player file
	 */
	public static void updatePlayerFile(File file, NBTTagCompound nbt) 
	{
		NBTUtil.updateNBTFileSafley(file,nbt);
	}
	/**
	 * Gets cached playerdata from filename don't call unless you have the exact string
	 */
	public static NBTTagCompound getPlayerFileNBT(String display,boolean uuidDir) 
	{
		FileInputStream stream = null;
		NBTTagCompound nbt = null;
		try
		{
			stream = !uuidDir ? new FileInputStream(new File(LibEvents.playerDataNames,display + ".dat")) : new FileInputStream(new File(LibEvents.playerDataDir,display + ".dat"));
			nbt = CompressedStreamTools.readCompressed(stream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			nbt = null;
		}
		finally
		{
			if(stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					System.out.println("unable to close input stream for player:" + display + ".dat");
				}
			}
		}
		return nbt;
	}
	
    public static void printChat(EntityPlayer player,String c_player, String c_msg, String messege)
	{
		player.addChatComponentMessage(new ChatComponentText(c_player + player.getCommandSenderName() + " " + c_msg + messege));
	}

}
