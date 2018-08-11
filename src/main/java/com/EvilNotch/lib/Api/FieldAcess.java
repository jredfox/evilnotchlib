package com.EvilNotch.lib.Api;

import java.lang.reflect.Method;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.storage.SaveHandler;

public class FieldAcess {
	
	//blockapi and fields
	public static String blockHardness = null;
	public static String blockMaterial = null;
	public static String blockSoundType = null;
	public static String translucent = null;
	public static String blockResistance = null;
	public static String enableStats = null;
	public static String isTileProvider = null;
	public static String harvestTool = null;
	public static String blastResistence = null;
	public static String blockMaterialMapColor = null;
	
	public static String soundName = null;//sounds
	public static String commandsAllowed = null;//FakeWorld
	public static String lang_localizedName = null;
	
	public static String playerDataManager = null;
	public static String dataFixer = null;
	
	public static String entityUniqueID = null;
	public static String cachedUniqueIdString = null;
	
	public static String lowestRiddenX = null;
    public static String lowestRiddenY = null;
    public static String lowestRiddenZ = null;
    public static String lowestRiddenX1= null;
    public static String lowestRiddenY1 = null;
    public static String lowestRiddenZ1 = null;
	public static String lowestRiddenEnt = null;
	public static String commandSet = null;
	
	//methods
	public static Method method_dragonManager = null;
	public static Method methodEnt_copyDataFromOld = null;
    public static Method capture;
    public static Method chunkLoaded = null;
    public static Method setFlag;
	
	public static boolean cached = false;
	public static String gameProfileId = null;
	public static String blockstate = null;
	public static String gameRuleMap = null;
	public static String shadowSize = null;
	
	public static void cacheFields()
	{
		if(cached)
			return;
		
		blockHardness = MCPMappings.getField(Block.class, "blockHardness");
		blockMaterial = MCPMappings.getField(Block.class, "blockMaterial");
		blockSoundType = MCPMappings.getField(Block.class, "blockSoundType");
		translucent = MCPMappings.getField(Block.class, "translucent");
		blockResistance = MCPMappings.getField(Block.class, "blockResistance");
		enableStats = MCPMappings.getField(Block.class, "enableStats");
		isTileProvider = MCPMappings.getField(Block.class, "isTileProvider");
		harvestTool = MCPMappings.getField(Block.class, "harvestTool");
		blastResistence = MCPMappings.getField(Block.class, "blockResistance");
		blockMaterialMapColor = MCPMappings.getField(Block.class, "blockMapColor");
		commandsAllowed =  MCPMappings.getField(WorldSettings.class, "commandsAllowed");
		lang_localizedName = MCPMappings.getField(I18n.class, "localizedName");
		playerDataManager = MCPMappings.getField(PlayerList.class, "playerDataManager");
		dataFixer = MCPMappings.getField(SaveHandler.class, "dataFixer");
		lowestRiddenEnt = MCPMappings.getField(NetHandlerPlayServer.class, "lowestRiddenEnt");
		commandSet = MCPMappings.getField(CommandHandler.class,"commandSet");
		entityUniqueID = MCPMappings.getField(Entity.class, "entityUniqueID");
		cachedUniqueIdString = MCPMappings.getField(Entity.class, "cachedUniqueIdString");
		gameProfileId = MCPMappings.getField(GameProfile.class, "id");
		blockstate = MCPMappings.getField(Block.class, "blockState");
		gameRuleMap =  MCPMappings.getField(GameRules.class, "rules");
		shadowSize = MCPMappings.getField(Render.class, "shadowSize");
		
		try 
		{
			method_dragonManager = DragonFightManager.class.getDeclaredMethod(MCPMappings.getMethod(DragonFightManager.class, "updateplayers"));
			methodEnt_copyDataFromOld = Entity.class.getDeclaredMethod(MCPMappings.getMethod(Entity.class,"copyDataFromOld"), Entity.class);
			chunkLoaded = World.class.getDeclaredMethod(MCPMappings.getMethod(World.class, "isChunkLoaded"), int.class,int.class,boolean.class);
			capture = NetHandlerPlayServer.class.getDeclaredMethod(MCPMappings.getMethod(NetHandlerPlayServer.class, "captureCurrentPosition"));	
			setFlag = Entity.class.getDeclaredMethod(MCPMappings.getMethod(Entity.class, "setFlag"), int.class,boolean.class);
			
			lowestRiddenX = MCPMappings.getField(NetHandlerPlayServer.class, "lowestRiddenX");
		    lowestRiddenY = MCPMappings.getField(NetHandlerPlayServer.class, "lowestRiddenY");
		    lowestRiddenZ = MCPMappings.getField(NetHandlerPlayServer.class, "lowestRiddenZ");
		    lowestRiddenX1 = MCPMappings.getField(NetHandlerPlayServer.class, "lowestRiddenX1");
		    lowestRiddenY1 = MCPMappings.getField(NetHandlerPlayServer.class, "lowestRiddenY1");
		    lowestRiddenZ1 = MCPMappings.getField(NetHandlerPlayServer.class, "lowestRiddenZ1");
			
			methodEnt_copyDataFromOld.setAccessible(true);
			method_dragonManager.setAccessible(true);
			capture.setAccessible(true);
			chunkLoaded.setAccessible(true);
			setFlag.setAccessible(true);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		
		cached = true;
	}

	public static ResourceLocation getSoundEventAsLoc(SoundEvent soundEvent) {
		if(soundName == null)
			soundName = MCPMappings.getField(SoundEvent.class, MCPMappings.getField(SoundEvent.class, "soundName"));
		if(soundEvent == null)
			return null;
		return (ResourceLocation) ReflectionUtil.getObject(soundEvent, SoundEvent.class, soundName);
	}

}
