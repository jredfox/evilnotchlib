package com.EvilNotch.lib.asm;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigCore {
	
	public static boolean playerOwnerAlwaysFix = true;
	
	public static boolean asm_playerlist = true;
	public static boolean asm_furnace = true;
	public static boolean asm_clientPlaceEvent = true;
	public static boolean asm_setTileNBTFix = true;
	
	public static void load()
	{
		File dir = new File(System.getProperty("user.dir"));
		File filecfg = new File(dir,"config/evilnotchlib/asm.cfg");
		System.out.println("Loading CoreMod Configurations for ASM:" +  filecfg);
		Configuration config = new Configuration(filecfg);
		config.load();
		asm_playerlist = config.get("asm","uuidFixer",true).getBoolean();
		asm_furnace = config.get("asm","furnaceFix",true).getBoolean();
		asm_clientPlaceEvent = config.get("asm","clientBlockPlaceEvent",true).getBoolean();
		asm_setTileNBTFix = config.get("asm","setTileNBTItemBlockFix",true).getBoolean();
		
		playerOwnerAlwaysFix = config.get("general", "playerOwnerSwapAlwaysFix", true).getBoolean();
		config.save();
	}

}
