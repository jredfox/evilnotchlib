package com.evilnotch.lib.minecraft.basicmc.auto.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.loader.LoaderGen;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.block.BasicBlock;
import com.evilnotch.lib.minecraft.basicmc.client.creativetab.BasicCreativeTab;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.ILineHead;
import com.evilnotch.lib.util.line.LangLine;
import com.evilnotch.lib.util.line.config.ConfigLang;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class LangRegistry {
	
	private static final List<LangEntry> langs = new ArrayList();
	public static String currentLang = null;
	public static Map<String, String> langlistClient = null;
	public static  Map<String,String> langlist = null;

	public static void registerLang() 
	{	
		if(!LoaderMain.isDeObfuscated)
		{
			return;
		}
		LoaderGen.checkRootFile();
		HashMap<File,ConfigLang> cfgs = new HashMap();
		populateLang(LoaderGen.root,langs,cfgs);
		
		if(langlistClient == null)
		{
			LanguageManager manager = Minecraft.getMinecraft().getLanguageManager();
			Locale l = manager.CURRENT_LOCALE;
			Map<String, String> map = l.properties;
			langlistClient = map;
		}
		if(langlist == null)
		{
			LanguageMap manager = I18n.localizedName;
			langlist = manager.languageList;
		}
		currentLang = getCurrentLang();
		
		//inject lang into mc ignoring if it has it already since in dev code is supreme
		for(ConfigLang cfg : cfgs.values())
		{
			if(!cfg.file.getName().endsWith(currentLang + ".lang"))
			{
				continue;
			}
			injectClientLang(cfg);
		}
		clear();
	}

	public static void joinLang(List<LangEntry> itemlangs) 
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		for(LangEntry lang : itemlangs)
			langs.add(lang);
	}

	/**
	 * generate lang files here
	 */
	private static void populateLang(File root, List<LangEntry> li,HashMap<File,ConfigLang> map) 
	{
		for(LangEntry lang : li)
		{
			String domain = lang.loc.getResourceDomain();
			boolean compiled = MinecraftUtil.isModCompiled(domain);
			if(compiled)
			{
				if(Config.debug)
					System.out.println("skipping lang entry as mod is compiled:" + lang);
				continue;
			}
			File file = new File(root,domain + "/lang/" + lang.region + ".lang");
			ConfigLang cfg = map.get(file);
			if(cfg == null)
			{
				cfg = new ConfigLang(file);
				cfg.loadConfig();
				map.put(file, cfg);
			}
			LangLine line = new LangLine(lang.getString());
			cfg.setLine(line);
		}
		for(ConfigLang lang : map.values())
		{	
			lang.saveConfig(true,false,true);
		}
	}

	public static String getCurrentLang() 
	{
		 return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}

	/**
	 * Create fast utf-8 instanceof ConfigLang then do manual injections
	 */
	private static void injectClientLang(File f) 
	{
		ConfigLang cfg = new ConfigLang(f);
		injectClientLang(cfg);
	}

	public static void injectClientLang(ConfigLang cfg) 
	{
		for(ILine l : cfg.lines)
		{
			ILineHead line = (ILineHead)l;
			String key = line.getId();
			String value = (String) line.getHead();
			langlistClient.put(key,value);
			langlist.put(key,value);
		}
	}

	public static void add(LangEntry entry) 
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		langs.add(entry);
	}
	
	public static void clear() 
	{
		langs.clear();
	}
	
	public static void registerLang(Object b, LangEntry... entries) 
	{
		registerLang(b, getRegistryName(b), entries);
	}
	
	public static void registerLang(Object b, String loc, LangEntry... entries) 
	{
		String pre = "";
		if(b instanceof Block)
			pre = "tile.";
		else if(b instanceof Item)
			pre = "item.";
		else if(b instanceof Enchantment)
			pre = "enchantment.";
		else if(b instanceof Biome)
			pre = "biome.";
		else if(b instanceof Potion)
			pre = "potion.";
		else if(b instanceof CreativeTabs)
			pre = "itemGroup.";
		else if(b instanceof Entity)
			pre = "entity.";
		for(LangEntry lang : entries)
		{
			lang.langId = pre + loc.toString().replaceAll(":", ".") + ".name";
			lang.loc = new ResourceLocation(loc.replaceAll(".", ":"));
			LangRegistry.add(lang);
		}
	}
	
	public static void registerMetaLang(Object obj, LangEntry... entries)
	{
		registerMetaLang(obj, getRegistryName(obj), entries);
	}
	
	public static void registerMetaLang(Object obj, String loc, LangEntry... entries)
	{
		String pre = null;
		if(obj instanceof Block)
			pre = "tile.";
		else if(obj instanceof Item)
			pre = "item.";
		for(LangEntry lang : entries)
		{
			lang.langId = pre + loc.toString().replaceAll(":", ".") + "_" + lang.meta + ".name";
			LangRegistry.add(lang);
		}
	}

	public static String getRegistryName(Object obj) 
	{
		if(obj instanceof IForgeRegistryEntry.Impl)
			return ((IForgeRegistryEntry.Impl)obj).getRegistryName().toString();
		else if(obj instanceof CreativeTabs)
			return ((CreativeTabs)obj).tabLabel;
		return null;
	}
	

}
