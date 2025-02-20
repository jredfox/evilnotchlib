package com.evilnotch.lib.main.loader;

import java.io.File;

import org.apache.logging.log4j.Logger;

import com.evilnotch.lib.api.mcp.MCPMappings;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.transformer.EntityTransformer;
import com.evilnotch.lib.asm.transformer.Transformer;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.capability.CapRegDefaultHandler;
import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.main.eventhandler.TickEventClient;
import com.evilnotch.lib.main.eventhandler.TickServerEvent;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.minecraft.basicmc.auto.block.BlockProperty;
import com.evilnotch.lib.minecraft.basicmc.auto.item.ToolMat;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.test.EnumCheese;
import com.evilnotch.lib.minecraft.basicmc.auto.test.MultiSidedGrass;
import com.evilnotch.lib.minecraft.basicmc.block.BasicBlock;
import com.evilnotch.lib.minecraft.basicmc.block.BasicMetaBlock;
import com.evilnotch.lib.minecraft.basicmc.block.property.PropertyMetaEnum;
import com.evilnotch.lib.minecraft.basicmc.client.creativetab.BasicCreativeTab;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItemMeta;
import com.evilnotch.lib.minecraft.basicmc.item.tool.BasicPickaxe;
import com.evilnotch.lib.minecraft.capability.registry.CapabilityRegistry;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.proxy.ServerProxy;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.world.FakeWorld;

import jredfox.clfix.LaunchClassLoaderFix;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class LoaderMain {
	
	//lib stuffs
	public static boolean isDeObfuscated = true;
	public static boolean isClient = false;
	public static Logger logger;
	public static World fake_world = null;
	public static LoadingStage currentLoadingStage = null;
	public static Thread serverThread = null;
	
	public static void loadpreinit(FMLPreInitializationEvent e, ClassLoader clforge)
	{
		loaderMainPreInit(e, clforge);
		LoaderItems.loadpreinit();
		LoaderBlocks.loadpreinit();
		LoaderCommands.load();
		loadDebugItems();
	}

	public static void loadInit(FMLInitializationEvent e)
	{
		currentLoadingStage = LoadingStage.INIT;
		MCPMappings.init();
		NetWorkHandler.init();
		MainJava.proxy.initMod();
	}
	
	public static void loadPostInit(FMLPostInitializationEvent e)
	{
		currentLoadingStage = LoadingStage.POSTINIT;
		fake_world = new FakeWorld();
		LoaderItems.loadpostinit();
		LoaderBlocks.loadpostinit();
		LoaderGen.gen();
		
	    MainJava.proxy.postinit();//generate lang,generate shadow sizes
	}
	
	public static void loadComplete(FMLLoadCompleteEvent e, ClassLoader clforge)
	{
		currentLoadingStage = LoadingStage.COMPLETE;
		LaunchClassLoaderFix.verify(clforge);
	}

	private static void loaderMainPreInit(FMLPreInitializationEvent e, ClassLoader clforge) 
	{
		currentLoadingStage = LoadingStage.PREINIT;
		isDeObfuscated = !FMLCorePlugin.isObf;
		logger = e.getModLog();
		File dir = e.getModConfigurationDirectory();
		
		MCPMappings.cacheMCP();
		LoaderFields.cacheFields();
		
		MainJava.proxy.proxyStart();
		Config.loadConfig(dir);
		MainJava.proxy.preinit(e);
		
		GeneralRegistry.load();
		GeneralRegistry.registerGameRule("PlayerLvlDAT", true);
		loadEvents();
		LaunchClassLoaderFix.stopMemoryOverflowFoamFix(clforge);
		CapabilityRegistry.registerRegistry(new CapRegDefaultHandler());
	}
	
	/**
	 * load Debug Blocks/Items
	 */
	private static void loadDebugItems() 
	{
		if(Config.debug)
		{
			BlockProperty props = new BlockProperty(new ResourceLocation(MainJava.MODID + ":" + "spider_material"), new ResourceLocation("ROCK"), "pickaxe",11f,10f,1,SoundType.SNOW,20,100,10.6f,2);
			BasicCreativeTab tab = new BasicCreativeTab(new ResourceLocation(MainJava.MODID + ":spidertesting"),new ItemStack(Items.CAKE),new LangEntry("en_us","Custom Shiny Tab"),new LangEntry("ru_ru","Ã�Å¸Ã�Â¾Ã�Â»Ã‘Å’Ã�Â·Ã�Â¾Ã�Â²Ã�Â°Ã‘â€šÃ�ÂµÃ�Â»Ã‘Å’Ã‘ï¿½Ã�ÂºÃ�Â°Ã‘ï¿½ Ã�Â±Ã�Â»Ã�ÂµÃ‘ï¿½Ã‘â€šÃ‘ï¿½Ã‘â€°Ã�Â°Ã‘ï¿½ Ã�Â²Ã�ÂºÃ�Â»Ã�Â°Ã�Â´Ã�ÂºÃ�Â°") );
			BasicItem item = new BasicItem(new ResourceLocation(MainJava.MODID + ":" + "stick"),tab,new LangEntry("en_us","Modded Stick"));
			ToolMat test = new ToolMat(new ResourceLocation(MainJava.MODID + ":" + "radioactive"), 2, 100, 10, 3, 30);
			BasicPickaxe axe = new BasicPickaxe(new ResourceLocation(MainJava.MODID + ":" + "pickaxe"),test,tab, new LangEntry("en_us","RadioActive Pick"));
			BasicBlock b = new BasicBlock(new ResourceLocation(MainJava.MODID + ":" + "spider"),Material.ROCK,tab,props,new LangEntry("en_us","Spider FU"),new LangEntry("ru_ru","Ã�Â¿Ã�Â°Ã‘Æ’Ã�Âº"));
		
			BasicMetaBlock b2 = new BasicMetaBlock(new ResourceLocation(MainJava.MODID + ":" + "cheese"),PropertyMetaEnum.createProperty("cheese", EnumCheese.class), Material.ROCK, tab,new LangEntry("en_us","American Cheese","american"),new LangEntry("en_us","Swiss Cheese","swiss"));
			BasicMetaBlock b3 = new BasicMetaBlock(new ResourceLocation(MainJava.MODID + ":" + "meat"), PropertyInteger.create("meat",0,15), Material.ROCK, tab,new LangEntry("en_us","Steak Block","0"),new LangEntry("en_us","Pork Block","1"));
			BasicMetaBlock b4 = new BasicMetaBlock(new ResourceLocation(MainJava.MODID + ":" + "wolf"), PropertyBool.create("wolf"), Material.ROCK, tab,new LangEntry("en_us","Angry Wolf Block","false"),new LangEntry("en_us","Wolf Block","true"));
			BasicMetaBlock b5 = new BasicMetaBlock(new ResourceLocation(MainJava.MODID + ":" + "facing"), PropertyDirection.create("direction"), Material.ROCK, tab,
				new LangEntry("en_us","UP","up"),new LangEntry("en_us","Down","down"),new LangEntry("en_us","North","north"),
				new LangEntry("en_us","South","south"),new LangEntry("en_us","East","east"),new LangEntry("en_us","West","west"));
		
			MultiSidedGrass b6 = new MultiSidedGrass(new ResourceLocation(MainJava.MODID + ":" + "grass"),PropertyInteger.create("grass_test", 0, 2),Material.GRASS,tab,new LangEntry("en_us","Cool Grass","0"),new LangEntry("en_us","Purple Grass","1"),new LangEntry("en_us","Orange Grass","2"));
		
			BasicItemMeta i2 = new BasicItemMeta(new ResourceLocation(MainJava.MODID + ":" + "ingot"), 4,tab,
				new LangEntry("en_us","Purple Ingot","0"),new LangEntry("en_us","Yellow Ingot","1"),new LangEntry("en_us","Tropical Ingot","2"),new LangEntry("en_us","Blue Ingot","3"),
				new LangEntry("en_us","Cloud Ingot","4"));
			
			BasicBlock state = new BasicBlock(new ResourceLocation(MainJava.MODID,"statetest"),Material.ROCK)
			{
				@Override
				public boolean hasItemBlock()
				{
					return false;
				}
			};
		}
	}

	private static void loadEvents() 
	{
		MinecraftForge.EVENT_BUS.register(new VanillaBugFixes());
		MinecraftForge.EVENT_BUS.register(new LibEvents());
		MinecraftForge.EVENT_BUS.register(new LoaderMain());
		TickRegistry.register(new TickServerEvent(), Side.SERVER);
		TickRegistry.register(new TickEventClient(), Side.CLIENT);
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
		serverThread = Thread.currentThread();
		
		LoaderCommands.registerToWorld(e);
		
		//directories instantiate
		MinecraftServer server = e.getServer();
		VanillaBugFixes.worlDir = server.worlds[0].getSaveHandler().getWorldDirectory().getAbsoluteFile();
		VanillaBugFixes.playerDataDir = new File(VanillaBugFixes.worlDir, "playerdata");
		VanillaBugFixes.playerDataNames = new File(VanillaBugFixes.worlDir, "playerdata/names");
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
