package com.evilnotch.lib.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.evilnotch.lib.api.FieldAcess;
import com.evilnotch.lib.api.MCPMappings;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.minecraft.content.block.BasicBlock;
import com.evilnotch.lib.minecraft.content.block.BasicMetaBlock;
import com.evilnotch.lib.minecraft.content.block.BlockProperties;
import com.evilnotch.lib.minecraft.content.block.IBasicBlock;
import com.evilnotch.lib.minecraft.content.block.property.PropertyMetaEnum;
import com.evilnotch.lib.minecraft.content.block.test.EnumCheese;
import com.evilnotch.lib.minecraft.content.block.test.MultiSidedGrass;
import com.evilnotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.evilnotch.lib.minecraft.content.command.CMDDim;
import com.evilnotch.lib.minecraft.content.command.CMDKick;
import com.evilnotch.lib.minecraft.content.command.CMDSeedGet;
import com.evilnotch.lib.minecraft.content.command.CMDStack;
import com.evilnotch.lib.minecraft.content.command.CMDTP;
import com.evilnotch.lib.minecraft.content.command.CMDTeleport;
import com.evilnotch.lib.minecraft.content.item.BasicItem;
import com.evilnotch.lib.minecraft.content.item.BasicItemMeta;
import com.evilnotch.lib.minecraft.content.item.IBasicItem;
import com.evilnotch.lib.minecraft.content.item.armor.ArmorSet;
import com.evilnotch.lib.minecraft.content.item.armor.IBasicArmor;
import com.evilnotch.lib.minecraft.content.item.tool.ItemBasicPickaxe;
import com.evilnotch.lib.minecraft.content.item.tool.ToolMat;
import com.evilnotch.lib.minecraft.content.item.tool.ToolSet;
import com.evilnotch.lib.minecraft.content.lang.LangEntry;
import com.evilnotch.lib.minecraft.content.tick.TickReg;
import com.evilnotch.lib.minecraft.content.world.FakeWorld;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.proxy.ServerProxy;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigNonMeta;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;


@Mod(modid = MainJava.MODID,name = MainJava.NAME, version = MainJava.VERSION)
public class MainJava {
	//Automation
	public static ArrayList<IBasicBlock> blocks = new ArrayList();
	public static ArrayList<IBasicItem> items = new ArrayList();
	public static ArrayList<ArmorSet> armorsets = new ArrayList();
	public static ArrayList<ToolSet> toolsets = new ArrayList();
	public static ConfigBase cfgTools = null;
	public static ConfigBase cfgArmors = null;
	public static ConfigBase cfgBlockProps = null;
	
	//lib stuffs
	public static boolean isDeObfuscated = true;
	public static final String MODID =  "evilnotchlib";
	public static final String VERSION = "1.2.3";//SNAPSHOT 74
	public static final String NAME = "Evil Notch Lib";
	public static final String max_version = "4.0.0.0.0";//allows for 5 places in lib version
	public static boolean isClient = false;
	@SidedProxy(clientSide = "com.evilnotch.lib.minecraft.proxy.ClientProxy", serverSide = "com.evilnotch.lib.minecraft.proxy.ServerProxy")
	public static ServerProxy proxy;
	public static Logger logger;
	public static int recipeIndex = 0;
	public static World fake_world = null;

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{	
		isDeObfuscated = !FMLCorePlugin.isObf;
		proxy.proxypreinit();
		logger = e.getModLog();
	  	
		MCPMappings.cacheMCPApplicable(e.getModConfigurationDirectory());
		FieldAcess.cacheFields();
		Config.loadConfig(e.getModConfigurationDirectory());
		proxy.preinit(e);
		GeneralRegistry.load();
		
		cfgTools = new ConfigNonMeta(new File(Config.cfg.getParent(),"config/tools.cfg"));
		cfgArmors = new ConfigNonMeta(new File(Config.cfg.getParent(),"config/armor.cfg"));
		cfgBlockProps = new ConfigNonMeta(new File(Config.cfg.getParent(),"config/blockprops.cfg"));
		
		cfgTools.loadConfig();
		cfgArmors.loadConfig();
		cfgBlockProps.loadConfig();
		
		MinecraftForge.EVENT_BUS.register(new VanillaBugFixes());
		MinecraftForge.EVENT_BUS.register(new LibEvents());
		MinecraftForge.EVENT_BUS.register(this);
		
		GeneralRegistry.registerCommand(new CMDDim());
		GeneralRegistry.registerCommand(new CMDStack());
		GeneralRegistry.registerCommand(new CMDKick());
		
		GeneralRegistry.replaceVanillaCommand("seed", new CMDSeedGet());
		if(Config.replaceTP)
		{
			GeneralRegistry.replaceVanillaCommand("tp",new CMDTP());
			GeneralRegistry.replaceVanillaCommand("teleport",new CMDTeleport());
		}
		
//		BlockApi.setMaterial(Blocks.DIAMOND_ORE,Material.WOOD,"axe");
//		BlockApi.setMaterial(Blocks.DIRT,Material.ROCK,"shovel");

		BlockProperties props = new BlockProperties(new ResourceLocation(MODID + ":" + "rock"),"pickaxe",11f,10f,1,SoundType.SNOW,20,100,10.6f,2);
		BasicCreativeTab tab = new BasicCreativeTab(new ResourceLocation(MODID + ":spidertesting"),new ItemStack(Items.CAKE),new LangEntry("Custom Shiny Tab","en_us"),new LangEntry("Ã�Å¸Ã�Â¾Ã�Â»Ã‘Å’Ã�Â·Ã�Â¾Ã�Â²Ã�Â°Ã‘â€šÃ�ÂµÃ�Â»Ã‘Å’Ã‘ï¿½Ã�ÂºÃ�Â°Ã‘ï¿½ Ã�Â±Ã�Â»Ã�ÂµÃ‘ï¿½Ã‘â€šÃ‘ï¿½Ã‘â€°Ã�Â°Ã‘ï¿½ Ã�Â²Ã�ÂºÃ�Â»Ã�Â°Ã�Â´Ã�ÂºÃ�Â°","ru_ru") );
		BasicItem item = new BasicItem(new ResourceLocation(MODID + ":" + "stick"),tab,new LangEntry("Modded Stick","en_us"));
		ToolMat test = new ToolMat(new ResourceLocation(MODID + ":" + "test"), 2, 100, 10, 3, 30);
		ItemBasicPickaxe axe = new ItemBasicPickaxe(test,new ResourceLocation(MODID + ":" + "pickaxe"),tab, new LangEntry("RadioActive Pick","en_us"));
		BasicBlock b = new BasicBlock(Material.ROCK, new ResourceLocation(MODID + ":" + "spider"),tab,props,new LangEntry("Spider Master","en_us"),new LangEntry("Ã�Â¿Ã�Â°Ã‘Æ’Ã�Âº","ru_ru"));
		
		BasicMetaBlock b2 = new BasicMetaBlock(Material.ROCK, new ResourceLocation(MODID + ":" + "cheese"), tab, null,PropertyMetaEnum.createProperty("cheese", EnumCheese.class),new LangEntry("American Cheese","en_us","american"),new LangEntry("Swiss Cheese","en_us","swiss"));
		BasicMetaBlock b3 = new BasicMetaBlock(Material.ROCK, new ResourceLocation(MODID + ":" + "meat"), tab, null,PropertyInteger.create("meat",0,15),new LangEntry("Steak Block","en_us","0"),new LangEntry("Pork Block","en_us","1"));
		BasicMetaBlock b4 = new BasicMetaBlock(Material.ROCK, new ResourceLocation(MODID + ":" + "wolf"), tab, null,PropertyBool.create("wolf"),new LangEntry("Angry Wolf Block","en_us","false"),new LangEntry("Wolf Block","en_us","true"));
		BasicMetaBlock b5 = new BasicMetaBlock(Material.ROCK, new ResourceLocation(MODID + ":" + "facing"), tab, null,PropertyDirection.create("direction"),
				new LangEntry("UP","en_us","up"),new LangEntry("Down","en_us","down"),new LangEntry("North","en_us","north"),
				new LangEntry("South","en_us","south"),new LangEntry("East","en_us","east"),new LangEntry("West","en_us","west"));
		MultiSidedGrass b6 = new MultiSidedGrass(Material.GRASS,new ResourceLocation(MODID + ":" + "grass"),tab,null,PropertyInteger.create("grass_test", 0, 2),new LangEntry("Cool Grass","en_us","0"),new LangEntry("Purple Grass","en_us","1"),new LangEntry("Orange Grass","en_us","2"));
		
		BasicItemMeta i2 = new BasicItemMeta(new ResourceLocation(MODID + ":" + "ingot"), 4,tab,
				new LangEntry("Purple Ingot","en_us","0"),new LangEntry("Yellow Ingot","en_us","1"),new LangEntry("Tropical Ingot","en_us","2"),new LangEntry("Blue Ingot","en_us","3"),
				new LangEntry("Cloud Ingot","en_us","4"));
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e) throws Exception
	{
		proxy.initMod();
		NetWorkHandler.init();
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent e)
	{
		/**
		 * if world capabilities are still not registered by init that is a mod's issue for the world and not mine
		 */
		fake_world = new FakeWorld();
		try
		{
			proxy.jsonGen();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		proxy.lang();
		
		if(!MainJava.isDeObfuscated)
		{
			cfgArmors.saveConfig(true, false, true);
			cfgTools.saveConfig(true, false, true);
			cfgBlockProps.saveConfig(true, false, true);
		}
		
	    proxy.postinit();//generate lang,generate shadow sizes
	}
	
	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent e) throws Exception
	{
		
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		//registries
		for(IBasicItem i : MainJava.items)
		{
			if(i.register())
			{
			   Item item = (Item)i;
			   ForgeRegistries.ITEMS.register(item);
			}
		}
	}
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) 
	{
		for(IBasicBlock i : MainJava.blocks)
		{
		   if(i.register())
		   {
			   Block b = (Block)i;
			   ForgeRegistries.BLOCKS.register(b);
			   if(i.hasItemBlock())
				   ForgeRegistries.ITEMS.register(i.getItemBlock());
		   }
	    }
	}
	//recipe generators for basic toolsets/armorsets that can be auto generated
	@SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) 
	{
		//this is how block armor was created via this algorithm basically
	    for(ArmorSet set : armorsets)
	    {
	    	ItemStack h = set.helmet;
	    	ItemStack c = set.chestplate;
	    	ItemStack l = set.leggings;
	    	ItemStack b = set.boots;
	    	if(h.getItem() instanceof IBasicArmor)
	    	{
	    		IBasicArmor basic = (IBasicArmor)h.getItem();
	    		//check before overriding the armor set
	    		if(basic.getArmorSet() == null)
	    			basic.setArmorSet(set);
	    	}
	    	if(c.getItem() instanceof IBasicArmor)
	    	{
	    		IBasicArmor basic = (IBasicArmor)c.getItem();
	    		if(basic.getArmorSet() == null)
	    			basic.setArmorSet(set);
	    	}
	    	if(l.getItem() instanceof IBasicArmor)
	    	{
	    		IBasicArmor basic = (IBasicArmor)l.getItem();
	    		if(basic.getArmorSet() == null)
	    			basic.setArmorSet(set);
	    	}
	    	if(b.getItem() instanceof IBasicArmor)
	    	{
	    		IBasicArmor basic = (IBasicArmor)b.getItem();
	    		if(basic.getArmorSet() == null)
	    			basic.setArmorSet(set);
	    	}
	    	
	    	if(!set.hasRecipe)
	    		continue;
	    	ItemStack block = set.block;
	    	boolean meta = set.allMetaBlock;
	    	//helmet
	    	if(h != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), h, new Object[]{"bbb","b b",'b',meta ? block.getItem() : block } );
	    	//chestplate
	    	if(c != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), c, new Object[]{"b b","bbb","bbb",'b',meta ? block.getItem() : block} );
	    	//leggings
	    	if(l != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), l, new Object[]{"bbb","b b","b b",'b',meta ? block.getItem() : block} );
	    	//boots
	    	if(b != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), b, new Object[]{"b b","b b",'b',meta ? block.getItem() : block} );
	    }
	    //generator for tools
	    for(ToolSet set : toolsets)
	    {
	    	ItemStack pickaxe = set.pickaxe;
	    	ItemStack axe = set.axe;
	    	ItemStack sword = set.sword;
	    	ItemStack spade = set.shovel;
	    	ItemStack hoe = set.hoe;
	    	ItemStack block = set.block;
	    	ItemStack stick = set.stick;
	    	boolean mb = set.allMetaBlock;
	    	boolean ms = set.allMetaStick;
	    	
	    	if(pickaxe != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), pickaxe, new Object[]{"bbb"," s "," s ",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    	if(axe != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), axe, new Object[]{"bb ","bs "," s ",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    	if(sword != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), sword, new Object[]{"b","b","s",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    	if(spade != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), spade, new Object[]{"b","s","s",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    	if(hoe != null)
	    		GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":" + recipeIndex++), new ResourceLocation("recipes"), hoe, new Object[]{"bb "," s "," s ",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    }
	}
	/**
	 * save all capabilities on server closing since player logout events don't fire then
	 */
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{	
		//prevent memory leaks
		TickReg.garbageCollectServer();
		EntityUtil.nbts.clear();
		LibEvents.playerFlags.clear();
		LibEvents.kicker.clear();
		LibEvents.isKickerIterating = false;
		LibEvents.msgs.clear();
	}
	@Mod.EventHandler
	public void commandRegister(FMLServerStartingEvent e)
	{
		MinecraftServer server = e.getServer();
		GeneralRegistry.removeActiveCommands(server);
		for(ICommand cmd : GeneralRegistry.getCmdList())
			e.registerServerCommand(cmd);
		
		GameRules g = server.getEntityWorld().getGameRules();
		GeneralRegistry.removeActiveGameRules(g);
		GeneralRegistry.injectGameRules(g);
		
		//directories instantiate
		LibEvents.worlDir = server.worlds[0].getSaveHandler().getWorldDirectory();
		LibEvents.playerDataDir = new File(LibEvents.worlDir,"playerdata");
		LibEvents.playerDataNames = new File(LibEvents.worlDir,"playerdata/names");
	}

}
