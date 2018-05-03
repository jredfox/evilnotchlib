package com.EvilNotch.lib.Api;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.end.DragonFightManager;

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
	
	//methods
	public static Method method_dragonManager = null;
	
	public static boolean cached = false;
	
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
		
		try 
		{
			method_dragonManager = DragonFightManager.class.getDeclaredMethod(MCPMappings.getMethod(DragonFightManager.class, "updateplayers"));
			method_dragonManager.setAccessible(true);
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
