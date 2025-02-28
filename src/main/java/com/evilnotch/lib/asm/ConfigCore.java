package com.evilnotch.lib.asm;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.crashy.Crashy;

public class ConfigCore {
	
	//asm_debug
	public static boolean asm_furnace = true;
	public static boolean asm_clientPlaceEvent = true;
	public static boolean asm_setTileNBTFix = true;
	public static boolean asm_middleClickEvent = true;
	public static boolean asm_playermp = true;
	
	//asm_safe
	public static boolean asm_TranslationEvent = true;
	public static boolean asm_enchantmentNameFix = true;
	public static boolean asm_FSFix = true;
	public static boolean asm_patchLanSkins = true;
	public static boolean asm_patchLanSkins429 = true;
	public static boolean asm_skinURLHook = true;
	public static boolean asm_skinAgentMozilla = true;
	public static boolean asm_stopSteve = true;
	public static boolean asm_mouse_ears = true;
	public static boolean asm_dinnerbone = true;
	public static boolean asm_teams = true;
	public static boolean asm_teams_full = true;
	public static boolean asm_guiTabOverlay = true;
	public static boolean asm_batchLoad = true;
	
	public static boolean dumpASMJVM = Boolean.parseBoolean(System.getProperty("asm.dump", "false"));
	public static boolean dumpASM = dumpASMJVM;
	
	public static void load()
	{
		File dir = new File(System.getProperty("user.dir"));
		File filecfg = new File(dir,"config/evilnotchlib/asm.cfg");
		
		Configuration config = new Configuration(filecfg);
		config.load();
		
		//ASM-DEBUG Disable At your own Risk
		asm_furnace = config.get("asm_debug","asm_furnaceFix", true).getBoolean();
		asm_clientPlaceEvent = config.get("asm_debug","asm_clientBlockPlaceEvent", true).getBoolean();
		asm_setTileNBTFix = config.get("asm_debug","asm_setTileNBTItemBlockFix", true).getBoolean();
		asm_middleClickEvent = config.get("asm_debug","asm_middleClickEvent", true).getBoolean();
		asm_playermp = config.get("asm_debug","asm_playermp", true).getBoolean();
		asm_patchLanSkins = config.get("asm_debug","asm_patchLanSkins", true).getBoolean();
		asm_patchLanSkins429 = config.get("asm_debug","asm_patchLanSkins429", true).getBoolean();
		
		//ASM Config That Can Safely Be Turned Off with few Issues
		asm_TranslationEvent = config.get("asm_safe_cfg","asm_dynamicTranslationEvent", true).getBoolean();
		asm_enchantmentNameFix = config.get("asm_safe_cfg","asm_enchantmentNameFix", true).getBoolean();
		asm_FSFix = config.get("asm_safe_cfg","asm_FSFix", true).getBoolean();
		asm_stopSteve = config.get("asm_safe_cfg", "asm_stopSteve", true).getBoolean();
		asm_skinURLHook = config.get("asm_safe_cfg","asm_skinURLHook", true).getBoolean();
		asm_skinAgentMozilla = config.get("asm_safe_cfg","asm_skinAgentMozilla", true).getBoolean();
		asm_mouse_ears = config.get("asm_safe_cfg", "asm_mouse_ears", true).getBoolean();
		asm_dinnerbone = config.get("asm_safe_cfg", "asm_dinnerbone", true).getBoolean();
		asm_teams = config.get("asm_safe_cfg", "asm_teams", true).getBoolean();
		asm_teams_full = config.get("asm_safe_cfg", "asm_teams_full", true).getBoolean();
		asm_guiTabOverlay = config.get("asm_safe_cfg", "asm_guiTabOverlay", true).getBoolean();
		asm_batchLoad = config.get("asm_safe_cfg", "asm_batchLoad", true).getBoolean();
		
		Crashy.GUI = config.get("asm_safe_cfg","asm_gui_crash", true).getBoolean();
		
		dumpASMJVM = Boolean.parseBoolean(System.getProperty("asm.dump", "false"));
		dumpASM = config.get("debug","dumpASM", false).getBoolean() || dumpASMJVM;
		config.save();
	}

}
