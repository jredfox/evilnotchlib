package com.evilnotch.lib.minecraft.content.auto.lang;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.evilnotch.lib.api.FieldAcess;
import com.evilnotch.lib.api.FieldAcessClient;
import com.evilnotch.lib.api.MCPMappings;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.content.block.BasicBlock;
import com.evilnotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.evilnotch.lib.minecraft.content.item.BasicItem;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.ILineHead;
import com.evilnotch.lib.util.line.LangLine;
import com.evilnotch.lib.util.line.config.ConfigLang;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;

public class LangRegistry {
	
	public static final List<LangEntry> langs = new ArrayList();
	public static String currentLang = null;
	public static Map<String, String> langlistClient = null;
	public static  Map<String,String> langlist = null;

	public static void registerLang() 
	{
		if(!LoaderMain.isDeObfuscated)
		{
			System.out.println("lan generation only occurs in dev enviorment on client:");
			return;
		}
		joinLang(BasicBlock.blocklangs);
		joinLang(BasicItem.itemlangs);
		joinLang(BasicCreativeTab.creativeTabLang);
		
		ClientProxy.checkRootFile();
		HashMap<File,ConfigLang> cfgs = new HashMap();
		populateLang(ClientProxy.root,langs,cfgs);
		
		if(langlistClient == null)
		{
			LanguageManager manager = Minecraft.getMinecraft().getLanguageManager();
			Locale l = (Locale)ReflectionUtil.getObject(manager, LanguageManager.class, FieldAcessClient.CURRENT_LOCALE);
			Map<String, String> map = (Map<String, String>) ReflectionUtil.getObject(l, Locale.class, FieldAcessClient.properties);
			langlistClient = map;
		}
		if(langlist == null)
		{
			LanguageMap manager = (LanguageMap) ReflectionUtil.getObject(null, I18n.class, FieldAcess.lang_localizedName);
			langlist = (Map<String, String>) ReflectionUtil.getObject(manager, LanguageMap.class, MCPMappings.getField(LanguageMap.class, "languageList"));
		}
		currentLang = getCurrentLang();
		//inject lang into mc ignoring if it has it already since in dev code is supreme
		for(ConfigLang cfg : cfgs.values())
		{
			if(!cfg.file.getName().endsWith(currentLang + ".lang"))
			{
				System.out.println("skipping cfgFile:" + cfg.file.getName() );
				continue;
			}
			for(ILine l : cfg.lines)
			{
				ILineHead line = (ILineHead)l;
				String key = line.getId();
				String value = (String) line.getHead();
				if(Config.debug)
					System.out.println("injecting:" + line);
				langlistClient.put(key,value);
				if(Config.debug)
					System.out.println("injectingServer:" + line);
				langlist.put(key,value);
			}
		}
	}

	public static void joinLang(List<LangEntry> itemlangs) {
		for(LangEntry lang : itemlangs)
			langs.add(lang);
	}

	/**
	 * generate lang files here
	 */
	public static void populateLang(File root, List<LangEntry> li,HashMap<File,ConfigLang> map) 
	{
		for(LangEntry lang : li)
		{
			String domain = lang.loc.getResourceDomain();
			if(!ClientProxy.compiledTracker.containsKey(domain))
				ClientProxy.compiledTracker.put(domain, MinecraftUtil.isModCompiled(domain));
			boolean compiled = ClientProxy.compiledTracker.get(domain);
			if(compiled)
			{
				if(Config.debug)
					System.out.println("skipping lang entry as mod is compiled:" + lang);
				continue;
			}
			File file = new File(root,domain + "/lang/" + lang.langType + ".lang");
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
	public void injectClientLang(File f) 
	{
		ConfigLang cfg = new ConfigLang(f);
		injectClientLang(cfg);
	}

	public void injectClientLang(ConfigLang cfg) 
	{
		for(ILine l : cfg.lines)
		{
			ILineHead line = (ILineHead)l;
			String key = line.getId();
			String value = (String) line.getHead();
			if(!langlistClient.containsKey(key))
				langlistClient.put(key,value);
		}
	}


}
