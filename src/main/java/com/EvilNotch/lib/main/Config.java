package com.EvilNotch.lib.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class Config {

	public static boolean debug = false;
	public static File cfg = null;
	public static boolean isDev = false;
	public static boolean tpAllowCrossDim = false;
	public static boolean replaceTP = true;
	public static List<ResourceLocation> cacheEntAllow = new ArrayList();
	
	public static void loadConfig(File d)
	{
		if(cfg == null)
			cfg = new File(d,MainJava.MODID + "/" + MainJava.MODID + ".cfg");
		Configuration config = new Configuration(cfg);
		config.load();
		debug = config.get("general", "Debug", false).getBoolean();
		isDev = config.get("general", "isDev", false).getBoolean();
		tpAllowCrossDim = config.get("general","tpAllowCrossDim",true).getBoolean();
		replaceTP = config.get("general","tpReplace",true).getBoolean();
		config.getStringList("domainEntityAllowed", "lib", new String[]{"jurassicraft"}, "add a whitelist of domains that are ok for creating entity living bases");
		config.save();
	}
}
