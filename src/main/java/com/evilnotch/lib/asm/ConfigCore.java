package com.evilnotch.lib.asm;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigCore {
	
	public static boolean asm_playerlist = true;
	public static boolean asm_furnace = true;
	public static boolean asm_clientPlaceEvent = true;
	public static boolean asm_setTileNBTFix = true;
	public static boolean asm_TranslationEvent = true;
	public static boolean asm_middleClickEvent = true;
	public static boolean asm_enchantments = true;
	public static boolean asm_entityPatch = true;
	
	public static boolean fixPlayerDataSP;
	
	public static int cfgIndex = -1;

	
	public static void load()
	{
		File dir = new File(System.getProperty("user.dir"));
		File filecfg = new File(dir,"config/evilnotchlib/asm.cfg");
		System.out.println("Loading CoreMod Configurations for ASM:");
		
		Configuration config = new Configuration(filecfg);
		config.load();
		asm_playerlist = config.get("asm","uuidFixer",true).getBoolean();
		asm_furnace = config.get("asm","furnaceFix",true).getBoolean();
		asm_clientPlaceEvent = config.get("asm","clientBlockPlaceEvent",true).getBoolean();
		asm_setTileNBTFix = config.get("asm","setTileNBTItemBlockFix",true).getBoolean();
		asm_TranslationEvent = config.get("asm","dynamicTranslationEvent",true).getBoolean();
		asm_middleClickEvent = config.get("asm","middleClickEvent",true).getBoolean();
		asm_enchantments = config.get("asm","enchantmentNameFix",true).getBoolean();
		asm_entityPatch = config.get("asm","asm_entityPatch",true).getBoolean();
				
		fixPlayerDataSP = config.get("general","fixHostPlayerDataSP",true).getBoolean();
		
		cfgIndex = config.get("debug","cfgIndex",cfgIndex).getInt();
		config.save();
	}

}
