package com.evilnotch.lib.main.loader;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.evilnotch.lib.api.mcp.MCPMappings;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.main.eventhandler.TickEventClient;
import com.evilnotch.lib.main.eventhandler.TickServerEvent;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.minecraft.basicmc.block.IBasicBlock;
import com.evilnotch.lib.minecraft.basicmc.item.IBasicItem;
import com.evilnotch.lib.minecraft.basicmc.item.armor.ArmorSet;
import com.evilnotch.lib.minecraft.basicmc.item.armor.IBasicArmor;
import com.evilnotch.lib.minecraft.basicmc.item.tool.ToolSet;
import com.evilnotch.lib.minecraft.event.PickEvent.Entity;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.proxy.ServerProxy;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.minecraft.world.FakeWorld;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LoaderMain {
	
	//lib stuffs
	public static boolean isDeObfuscated = true;
	public static boolean isClient = false;
	public static Logger logger;
	public static World fake_world = null;
	public static LoadingStage currentLoadingStage = null;
	
	public static void loadpreinit(FMLPreInitializationEvent e)
	{
		loaderMainPreInit(e);
		LoaderItems.loadpreinit();
		LoaderBlocks.loadpreinit();
		LoaderCommands.load();
	}

	public static void loadInit(FMLInitializationEvent e)
	{
		currentLoadingStage = LoadingStage.INIT;
		NetWorkHandler.init();
		MainJava.proxy.initMod();
		MCPMappings.clearMaps();
	}
	
	public static void loadPostInit(FMLPostInitializationEvent e)
	{
		currentLoadingStage = LoadingStage.POSTINIT;
		fake_world = new FakeWorld();
		LoaderItems.loadpostinit();
		LoaderBlocks.loadpostinit();
		LoaderGen.load();
	    MainJava.proxy.postinit();//generate lang,generate shadow sizes
	}
	
	public static void loadComplete(FMLLoadCompleteEvent e)
	{
		currentLoadingStage = LoadingStage.COMPLETE;
	}

	private static void loaderMainPreInit(FMLPreInitializationEvent e) 
	{
		currentLoadingStage = LoadingStage.PREINIT;
		isDeObfuscated = !FMLCorePlugin.isObf;
		logger = e.getModLog();
		
		MCPMappings.cacheMCPApplicable(e.getModConfigurationDirectory());
		LoaderFields.cacheFields();
		
		MainJava.proxy.proxyStart();
		MainJava.proxy.preinit(e);
		
		Config.loadConfig(e.getModConfigurationDirectory());
		GeneralRegistry.load();
		loadEvents();
	}
	
	private static void loadEvents() 
	{
		MinecraftForge.EVENT_BUS.register(new VanillaBugFixes());
		MinecraftForge.EVENT_BUS.register(new LibEvents());
		MinecraftForge.EVENT_BUS.register(new LoaderMain());
		TickRegistry.registerServer(new TickServerEvent());
		TickRegistry.registerClient(new TickEventClient());
	}
	
	/**
	 * prevent memory leaks
	 */
	public static void serverStopping() 
	{
		ServerProxy.clearServerData();
	}

	public static void serverStart(FMLServerStartingEvent e) 
	{
		LoaderCommands.registerToWorld(e);
		
		//directories instantiate
		MinecraftServer server = e.getServer();
		VanillaBugFixes.worlDir = server.worlds[0].getSaveHandler().getWorldDirectory();
		VanillaBugFixes.playerDataDir = new File(VanillaBugFixes.worlDir,"playerdata");
		VanillaBugFixes.playerDataNames = new File(VanillaBugFixes.worlDir,"playerdata/names");
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		LoaderItems.registerItems();
	}
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) 
	{
		LoaderBlocks.registerBlocks();
	}
	
	@SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) 
	{
		LoaderItems.registerRecipes(event);
	}

	public static boolean isLoadingStage(LoadingStage stage) 
	{
		return currentLoadingStage == stage;
	}

}
