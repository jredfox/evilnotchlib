package com.EvilNotch.lib.minecraft.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.content.ConfigLang;
import com.EvilNotch.lib.minecraft.content.LangEntry;
import com.EvilNotch.lib.minecraft.content.LangLine;
import com.EvilNotch.lib.minecraft.content.blocks.BasicBlock;
import com.EvilNotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.EvilNotch.lib.minecraft.content.items.BasicItem;
import com.EvilNotch.lib.util.Line.ILine;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;

public class ServerProxy {
	public static Map<String,String> langlist = null;
	public static Map<String,String> langlist_fallback = null;
	public static HashMap<File, ConfigLang> langCfgs = null;
	public static File dirResourcePack = null;
	public static File langDir = null;
	
	public void preinit(){}

	public void postinit() 
	{
		if(langlist == null)
		{
			LanguageMap manager = (LanguageMap) ReflectionUtil.getObject(null, I18n.class, FieldAcess.lang_localizedName);
			langlist = (Map<String, String>) ReflectionUtil.getObject(manager, LanguageMap.class, MCPMappings.getField(LanguageMap.class, "languageList"));
		}
		if(langlist_fallback == null)
		{
			LanguageMap manager = (LanguageMap) ReflectionUtil.getObject(null, I18n.class, MCPMappings.getField(I18n.class, "fallbackTranslator"));
			langlist_fallback = (Map<String, String>) ReflectionUtil.getObject(manager, LanguageMap.class,MCPMappings.getField(LanguageMap.class, "languageList"));
		}
			
		File dir = new File(Config.cfg.getParent(),"resourcepacks/langpack");
		dir.mkdirs();
		dirResourcePack = dir;
		File langdir = new File(dir,"assets/" + MainJava.MODID + "/lang");
		langdir.mkdirs();
		generateLang(langdir);
	}
	
	public static void generateLang(File langdir)
	{
		HashMap<File,ConfigLang> map = new HashMap();
		ServerProxy.populateLang(map,langdir,BasicBlock.blocklangs);
		ServerProxy.populateLang(map,langdir,BasicItem.itemlangs);
		ServerProxy.populateLang(map,langdir,BasicCreativeTab.creativeTabLang);
		langCfgs = map;
		langDir = langdir;
		
		for(ConfigLang cfg : map.values())
			cfg.updateConfig(true,false,true);
		
		if(!MainJava.isClient)
		{
			System.out.print("[EvilNotchLib/Info] Dedicated Server Injecting Lang:" + langdir + "\n");
			injectServerLang(langdir,map);//dedicated server call only
		}
	}
	public static void injectServerLang(File langdir,HashMap<File,ConfigLang> map)
	{
		//inject current lang only dedicated servers only support en_us
		String currentLang = MainJava.isClient ? ClientProxy.currentLang() : "en_us";
		File lang = new File(langdir,currentLang + ".lang");
		if(lang.exists())
		{
			ConfigLang cfglang = map.get(lang);
			if(cfglang != null)
			{
				for(ILine l : cfglang.lines)
				{
					LangLine line = (LangLine)l;
					String id = line.getModPath();
					if(!langlist.containsKey(id) )
						langlist.put(id, (String)line.getHead());
				}
			}
		}
		
		//support fallback for clients for mc hardcoded en_us lang style
		File lang2 = new File(langdir, "en_us" + ".lang");
		ConfigLang cfg2 = map.get(lang2);
		if(cfg2 != null)
		{
			for(ILine line : cfg2.lines)
			{
				String id = line.getModPath();
				if(!langlist_fallback.containsKey(id) )
					langlist_fallback.put(id, (String)line.getHead());
			
				if(!currentLang.equals("en_us"))
				{
					if(!langlist.containsKey(id) )
						langlist.put(id, (String)line.getHead());
				}
			}
		}
	}
	
	/**
	 * doesn't update them as it's unoptimized if have custom list update them yourself or you can call generatelang
	 */
	public static void populateLang(HashMap<File,ConfigLang> map,File langdir,ArrayList<LangEntry> list) 
	{
		for(LangEntry lang : list)
		{
			File f = new File(langdir,lang.langType + ".lang");
			ConfigLang cfg = map.get(f);
			if(cfg == null)
			{
				cfg = new ConfigLang(f);
				map.put(f, cfg);
			}
			if(Config.isDev) 
				cfg.setLine(new LangLine(lang));
			else
				cfg.addLine(new LangLine(lang));//force config to update if strings are not the same
		}
	}

	/**
	 * called before anything else
	 */
	public void proxypreinit() {}
	public void initMod() {}

}
