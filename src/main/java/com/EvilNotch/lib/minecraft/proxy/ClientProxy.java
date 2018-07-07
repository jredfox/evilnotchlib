package com.EvilNotch.lib.minecraft.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.EvilNotch.lib.Api.BlockApi;
import com.EvilNotch.lib.Api.FieldAcessClient;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.main.eventhandlers.ClientEvents;
import com.EvilNotch.lib.minecraft.content.ConfigLang;
import com.EvilNotch.lib.minecraft.content.blocks.IBasicBlock;
import com.EvilNotch.lib.minecraft.content.client.gui.GuiMainMenuBase;
import com.EvilNotch.lib.minecraft.content.client.gui.MenuRegistry;
import com.EvilNotch.lib.minecraft.content.items.IBasicItem;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.Comment;
import com.EvilNotch.lib.util.Line.ConfigBase;
import com.EvilNotch.lib.util.Line.IHead;
import com.EvilNotch.lib.util.Line.ILine;
import com.EvilNotch.lib.util.Line.LineEnhanced;
import com.EvilNotch.lib.util.Line.LineItemStack;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
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
		lang();
	}
	public void lang() 
	{
		if(!MainJava.isDeObfuscated)
		{
			System.out.println("lan generation only occurs in dev enviorment:");
			return;
		}
		File root = new File(Config.cfg.getParentFile().getParentFile().getParentFile().getParentFile(),"src/main/resources/assets");
		if(!root.exists())
			root.mkdirs();
		super.lang();
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
			  ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(BlockApi.getItemString(item), "inventory"));
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
