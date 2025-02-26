package com.evilnotch.lib.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class Config {

	public static boolean debug = false;
	public static File cfg = null;
	public static boolean tpAllowCrossDim = false;
	public static boolean replaceTP = true;
	public static boolean seedOpsOnly = false;
	public static boolean seedDisplay = true;
	public static boolean unloadDimensions = false;
	public static boolean unloadDimOverride = true;
	public static boolean playerLvlDat = true;
	public static boolean UUIDCrashOnFailure = true;
	
	//start skin fixes
	public static boolean allowskintrans = true;
	public static boolean skinCache = true;
	public static int skinCacheHours = 48;
	public static int skinCacheFast = 20;
	public static boolean stopSteve = true;
	public static long stopSteveMs = 2800;
	public static long stopSteveOtherMs = 280;
	public static boolean skinPacketSmooth = true;
	
	/**
	 * list of domains that are not acceptable
	 */
	public static List<String> cacheEntDeny = new ArrayList();
	/**
	 * blacklist of entities that are not allowed even though no exceptions are thrown
	 */
	public static List<ResourceLocation> cacheEntNamesDeny = new ArrayList();
	public static String[] skinDomains = new String[]{".minecraft.net", ".mojang.com", "crafatar.com"};
	public static List<Class> multiparts = new ArrayList();

	
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
		unloadDimensions = config.get("general", "unloadDimensions", unloadDimensions).getBoolean();
		unloadDimOverride = config.get("general", "unloadDimensionsOverride", unloadDimOverride, "This Requires unloadDimensions to be true! Overrides DimensionManager#keepLoaded. Disable this if Issues Occur").getBoolean();
		playerLvlDat = config.get("general", "playerLvlDat", playerLvlDat, "The Default GameRule Value of PlayerLVLDat When creating a new world. When the gamerule is false The new player won't recieve the old players data from level.dat").getBoolean();
		UUIDCrashOnFailure = config.get("general", "UUIDPatcherCrashOnFailure", UUIDCrashOnFailure, "Disable This if there are more then Forge's FakePlayer in your Modpack").getBoolean();
		
		allowskintrans = config.get("skins", "allowSkinTransparency", allowskintrans).getBoolean();
		skinCache = config.get("skins", "skinCache", skinCache).getBoolean();
		skinCacheHours = config.get("skins", "skinCacheMaxHours", skinCacheHours).getInt();
		skinCacheFast = config.get("skins", "skinCacheFastMinutes", skinCacheFast).getInt();
		skinDomains = config.getStringList("skinDomains", "skins", skinDomains, "Domain files must hash the file name or the clients will always assume the skin is up to date");
		stopSteve = config.get("skins", "stopSteve", stopSteve).getBoolean();
		stopSteveMs = config.get("skins", "stopSteveMs", (int)stopSteveMs).getInt();
		stopSteveOtherMs = config.get("skins", "stopSteveOtherMs", (int)stopSteveOtherMs).getInt();
		skinPacketSmooth = config.get("skins", "skinCachePacketSmooth", skinPacketSmooth).getBoolean();
		if(!ConfigCore.asm_stopSteve && skinPacketSmooth)
			throw new RuntimeException("IStopSteve#skinUnAvailable ASM Hook is Needed for Config#skinPacketSmooth to Work Properly!");
		
		//entity cache data for black list and allow certain entities to pass through
		cacheEntDeny = JavaUtil.<String>staticToArray(config.getStringList("domainEntityDeny", "cache_entity", new String[]{"customnpcs"}, "blacklist domain of entities that are bad"));
		String[] str = config.getStringList("blacklistEntity", "cache_entity", new String[]{""}, "don't want to blacklist entire mod domain use this list");
		cacheEntNamesDeny = JavaUtil.stringToLocArray(str);
		
		//parse MultiPart Entity's Parts
		String[] parts = config.getStringList("EntityParts", "cache_entity", new String[]{""}, "List of Entity Classes That is a Part of an Entity like MultiPartEntityPart");
		for(String s : parts)
		{
			s = s.trim();
			if(s.isEmpty())
				continue;
			Class c = ReflectionUtil.classForName(s);
			if(c != null)
				multiparts.add(c);
		}
		
		config.save();
	}
}
