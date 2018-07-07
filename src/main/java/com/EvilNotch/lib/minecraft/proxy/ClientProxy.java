package com.EvilNotch.lib.minecraft.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.EvilNotch.lib.Api.BlockApi;
import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.FieldAcessClient;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.main.eventhandlers.ClientEvents;
import com.EvilNotch.lib.minecraft.content.ConfigLang;
import com.EvilNotch.lib.minecraft.content.LangEntry;
import com.EvilNotch.lib.minecraft.content.LangLine;
import com.EvilNotch.lib.minecraft.content.blocks.BasicBlock;
import com.EvilNotch.lib.minecraft.content.blocks.IBasicBlock;
import com.EvilNotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.EvilNotch.lib.minecraft.content.client.gui.MenuRegistry;
import com.EvilNotch.lib.minecraft.content.items.BasicItem;
import com.EvilNotch.lib.minecraft.content.items.IBasicItem;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.Comment;
import com.EvilNotch.lib.util.Line.ConfigBase;
import com.EvilNotch.lib.util.Line.IHead;
import com.EvilNotch.lib.util.Line.ILine;
import com.EvilNotch.lib.util.Line.LineItemStack;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends ServerProxy{
	
	public static String currentLang = null;
	public static Map<String, String> langlistClient = null;
	public static  Map<String,String> langlist = null;

	@Override
	public void proxypreinit()
	{
		MainJava.isClient = true;
	}
	
	@Override
	public void preinit() 
	{
		super.preinit();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		FieldAcessClient.cacheFields();
		MenuRegistry.registerGuiMenu(GuiMainMenu.class, new ResourceLocation("mainmenu"));
	}
	@Override
	public void lang() 
	{
		if(!MainJava.isDeObfuscated)
		{
			System.out.println("lan generation only occurs in dev enviorment on client:");
			return;
		}
		File root = new File(Config.cfg.getParentFile().getParentFile().getParentFile().getParentFile(),"src/main/resources/assets");
		if(!root.exists())
			root.mkdirs();
		HashMap<File,ConfigLang> cfgs = new HashMap();
		populateLang(root,BasicBlock.blocklangs,cfgs);
		populateLang(root,BasicItem.itemlangs,cfgs);
		populateLang(root,BasicCreativeTab.creativeTabLang,cfgs);
		
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
		for(ConfigLang cfg : cfgs.values())
		{
			for(ILine line : cfg.lines)
			{
				String key = line.getModPath();
				String value = (String) line.getHead();
				if(!langlistClient.containsKey(key))
				{
					if(Config.debug)
						System.out.println("injecting:" + line.getString());
					langlistClient.put(key,value);
				}
				if(!langlist.containsKey(key))
				{
					if(Config.debug)
						System.out.println("injectingServer:" + line.getString());
					langlist.put(key,value);
				}
			}
		}
	}
	/**
	 * generate lang files here
	 */
	public void populateLang(File root, ArrayList<LangEntry> li,HashMap<File,ConfigLang> map) 
	{
		String currentLang = getCurrentLang();
		for(LangEntry lang : li)
		{
			File file = new File(root,lang.loc.getResourceDomain() + "/lang/" + currentLang + ".lang");
			ConfigLang cfg = map.get(file);
			if(cfg == null)
			{
				cfg = new ConfigLang(file);
				map.put(file, cfg);
			}
			LangLine line = new LangLine(lang.getString());
			cfg.addLine(line);
		}
		for(ConfigLang lang : map.values())
		{	
			lang.updateConfig();
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
		for(ILine line : cfg.lines)
		{
			String key = line.getModPath();
			String value = (String) line.getHead();
			if(!langlistClient.containsKey(key))
				langlistClient.put(key,value);
		}
	}

	@Override
	public void initMod()
	{
		menuLibInit();
		super.initMod();
	}
	
	@Override
	public void postinit()
	{
		super.postinit();
	}

	/**
	 * Reorder menus or if client overrides using whitelist do only the whitelist
	 */
	public static void menuLibInit() 
	{	
		//register user registered menus
		File f = new File(Config.cfgmenu.getParent(),"menulib.cfg");
		ArrayList<Comment> comments = (ArrayList<Comment>)JavaUtil.asArray(new Comment[]{new Comment("Menu Lib Configuration File. Register Other Mod's Main Menus That refuse to do it themselves :("),new Comment("Format is: \"modid:mainmenu\" = \"class.full.name\"")});
		ConfigBase cfg = new ConfigBase(f,comments);
		if(Loader.isModLoaded("thebetweenlands"))
		{
			cfg.addLine(new LineItemStack("\"thebetweenlands:mainmenu\" = \"thebetweenlands.client.gui.menu.GuiBLMainMenu\""));
			cfg.updateConfig(false, false, true);
		}
		for(ILine line : cfg.lines)
		{
			IHead head = (IHead)line;
			try
			{
				MenuRegistry.registerGuiMenu((Class<? extends GuiScreen>) Class.forName(head.getStringHead()), line.getResourceLocation());
			}
			catch(Throwable t)
			{
				System.out.print("[MenuLib/ERR] Unable to Locate class skipping menu registration for:" + line.getString() + "\n");
			}
		}
		
		MenuRegistry.reOrder();
		MenuRegistry.setCurrentMenu(Config.currentMenuIndex);
	}

	/**
	 * future:Generate models with textures coming from the registry name
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void modeltest(ModelBakeEvent event)
	{
		modelpreInit();
	}
	
	public static void modelpreInit() 
	{
		for(IBasicItem i : MainJava.items)
	    {
		   if(i.registerModel())
		   {
			  Item item = (Item)i;
			  for(int index = 0;index<15;index++)
				  ModelLoader.setCustomModelResourceLocation(item, index, new ModelResourceLocation(BlockApi.getItemString(item), "inventory"));
		   }
	   }
	   for(IBasicBlock i : MainJava.blocks)
	   {
		   if(i.registerModel())
		   {
			   Block b = (Block)i;
			   for(String s : i.getModelStates())
				   for(int index=0;index<15;index++)
					   ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), index,  new ModelResourceLocation(BlockApi.getBlockString(b), s));
		   }
	   }
	}

	public static EntityPlayer getPlayer() {
		return FMLClientHandler.instance().getClientPlayerEntity();
	}

}
