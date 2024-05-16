package com.evilnotch.lib.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	public static boolean debug = false;
	public static File cfg = null;
	public static boolean tpAllowCrossDim = false;
	public static boolean replaceTP = true;
	public static boolean seedOpsOnly = false;
	public static boolean seedDisplay = true;
	
	//start skin fixes
	public static boolean allowskintrans = true;
	public static int skinCacheMax = 1000;
	public static int skinCacheHours = 48;
	
	/**
	 * list of domains that are not acceptable
	 */
	public static List<String> cacheEntDeny = new ArrayList();
	/**
	 * blacklist of entities that are not allowed even though no exceptions are thrown
	 */
	public static List<ResourceLocation> cacheEntNamesDeny = new ArrayList();
	public static String[] skinDomains = new String[]{".minecraft.net", ".mojang.com", "crafatar.com"};
	
	public static void loadConfig(File d)
	{
		if(cfg == null)
			cfg = new File(d, MainJava.MODID + "/" + MainJava.MODID + ".cfg");
		Configuration config = new Configuration(cfg);
		config.load();
		debug = config.get("general", "debug", false).getBoolean();
		tpAllowCrossDim = config.get("general", "tpAllowCrossDim", true).getBoolean();
		replaceTP = config.get("general", "tpReplace", true).getBoolean();
		seedOpsOnly = config.get("general", "seedOpsOnly", false).getBoolean();
		seedDisplay = config.get("general", "seedF3", true).getBoolean();
		
		allowskintrans = config.get("skins", "allowSkinTransparency", true).getBoolean();
		skinCacheMax = config.get("skins", "skinCacheMax", skinCacheMax).getInt();
		skinCacheHours = config.get("skins", "skinCacheMax", skinCacheHours).getInt();
		skinDomains = config.getStringList("skinDomains", "skins", skinDomains, "Domain files must hash the file name or the clients will always assume the skin is up to date");
		
		//entity cache data for black list and allow certain entities to pass through
		cacheEntDeny = JavaUtil.<String>staticToArray(config.getStringList("domainEntityDeny", "cache_entity", new String[]{"customnpcs"}, "blacklist domain of entities that are bad"));
		String[] str = config.getStringList("blacklistEntity", "cache_entity", new String[]{""}, "don't want to blacklist entire mod domain use this list");
		cacheEntNamesDeny = JavaUtil.stringToLocArray(str);
		config.save();
	}
}
