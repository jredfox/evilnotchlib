package com.evilnotch.lib.asm;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.crashy.Crashy;

public class ConfigCore {
	
	public static boolean asm_playerlist = true;
	public static boolean asm_furnace = true;
	public static boolean asm_clientPlaceEvent = true;
	public static boolean asm_setTileNBTFix = true;
	public static boolean asm_TranslationEvent = true;
	public static boolean asm_middleClickEvent = true;
	public static boolean asm_entityPatch = true;
	public static boolean asm_enchantmentNameFix = true;
	public static boolean asm_patchLanSkins = true;
	public static boolean asm_FSFix = true;
	
	public static boolean dumpASMJVM = Boolean.parseBoolean(System.getProperty("asm.dump", "false"));
	public static boolean dumpASM = dumpASMJVM;
	
	public static void load()
	{
		File dir = new File(System.getProperty("user.dir"));
		File filecfg = new File(dir,"config/evilnotchlib/asm.cfg");
		System.out.println("Loading CoreMod Configurations for ASM:");
		
		Configuration config = new Configuration(filecfg);
		config.load();
		asm_playerlist = config.get("asm","uuidFixer", true).getBoolean();
		asm_furnace = config.get("asm","furnaceFix", true).getBoolean();
		asm_clientPlaceEvent = config.get("asm","clientBlockPlaceEvent", true).getBoolean();
		asm_setTileNBTFix = config.get("asm","setTileNBTItemBlockFix", true).getBoolean();
		asm_TranslationEvent = config.get("asm","dynamicTranslationEvent", true).getBoolean();
		asm_middleClickEvent = config.get("asm","middleClickEvent", true).getBoolean();
		asm_entityPatch = config.get("asm","asm_entityPatch", true).getBoolean();
		asm_enchantmentNameFix = config.get("asm","asm_enchantmentNameFix", true).getBoolean();
		asm_patchLanSkins = config.get("asm","asm_patchLanSkins", true).getBoolean();
		Crashy.GUI = config.get("asm","asm_gui_crash", true).getBoolean();
		asm_FSFix = config.get("asm","asm_FSFix", true).getBoolean();
		
		dumpASMJVM = Boolean.parseBoolean(System.getProperty("asm.dump", "false"));
		dumpASM = config.get("debug","dumpASM", false).getBoolean() || dumpASMJVM;
		config.save();
	}

}
