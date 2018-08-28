package com.EvilNotch.lib.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.main.eventhandlers.LibEvents;
import com.EvilNotch.lib.main.eventhandlers.VanillaBugFixes;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.minecraft.content.ArmorSet;
import com.EvilNotch.lib.minecraft.content.FakeWorld;
import com.EvilNotch.lib.minecraft.content.LangEntry;
import com.EvilNotch.lib.minecraft.content.ToolMat;
import com.EvilNotch.lib.minecraft.content.ToolSet;
import com.EvilNotch.lib.minecraft.content.blocks.BasicBlock;
import com.EvilNotch.lib.minecraft.content.blocks.BasicMetaBlock;
import com.EvilNotch.lib.minecraft.content.blocks.BlockProperties;
import com.EvilNotch.lib.minecraft.content.blocks.IBasicBlock;
import com.EvilNotch.lib.minecraft.content.blocks.property.PropertyMetaEnum;
import com.EvilNotch.lib.minecraft.content.blocks.test.EnumCheese;
import com.EvilNotch.lib.minecraft.content.blocks.test.MultiSidedGrass;
import com.EvilNotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.EvilNotch.lib.minecraft.content.commands.CMDDim;
import com.EvilNotch.lib.minecraft.content.commands.CMDKick;
import com.EvilNotch.lib.minecraft.content.commands.CMDSeedGet;
import com.EvilNotch.lib.minecraft.content.commands.CMDStack;
import com.EvilNotch.lib.minecraft.content.commands.CMDTP;
import com.EvilNotch.lib.minecraft.content.commands.CMDTeleport;
import com.EvilNotch.lib.minecraft.content.items.BasicItem;
import com.EvilNotch.lib.minecraft.content.items.BasicItemMeta;
import com.EvilNotch.lib.minecraft.content.items.IBasicArmor;
import com.EvilNotch.lib.minecraft.content.items.IBasicItem;
import com.EvilNotch.lib.minecraft.content.items.ItemBasicPickaxe;
import com.EvilNotch.lib.minecraft.content.pcapabilites.TickSaver;
import com.EvilNotch.lib.minecraft.content.tick.TickReg;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityHandler;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityReg;
import com.EvilNotch.lib.minecraft.network.NetWorkHandler;
import com.EvilNotch.lib.minecraft.proxy.ServerProxy;
import com.EvilNotch.lib.minecraft.registry.GeneralRegistry;
import com.EvilNotch.lib.util.Line.ConfigBase;
import com.EvilNotch.lib.util.Line.ConfigEnhanced;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.command.ICommand;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
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
import net.minecraftforge.fml.relauncher.ReflectionHelper;


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
	public static final String VERSION = "1.2.3";//SNAPSHOT 65
	public static final String NAME = "Evil Notch Lib";
	public static final String max_version = "4.0.0.0.0";//allows for 5 places in lib version
	public static boolean isClient = false;
	@SidedProxy(clientSide = "com.EvilNotch.lib.minecraft.proxy.ClientProxy", serverSide = "com.EvilNotch.lib.minecraft.proxy.ServerProxy")
	public static ServerProxy proxy;
	public static Logger logger;
	public static int recipeIndex = 0;
	public static World fake_world = null;
	/**
	 * A valid world reference once the game starts could be any dim
	 */
	public static World worldServer = null;

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{	
		proxy.proxypreinit();
		logger = e.getModLog();
	  	
		MCPMappings.cacheMCPApplicable(e.getModConfigurationDirectory());
		isDeObfuscated = isDeObfucscated();
		FieldAcess.cacheFields();
		fake_world = new FakeWorld();
		Config.loadConfig(e.getModConfigurationDirectory());
		proxy.preinit(e);
		GeneralRegistry.load();
		
		cfgTools = new ConfigEnhanced(new File(Config.cfg.getParent(),"config/tools.cfg"));
		cfgArmors = new ConfigEnhanced(new File(Config.cfg.getParent(),"config/armor.cfg"));
		cfgBlockProps = new ConfigEnhanced(new File(Config.cfg.getParent(),"config/blockprops.cfg"));
		
		MinecraftForge.EVENT_BUS.register(new VanillaBugFixes());
		MinecraftForge.EVENT_BUS.register(new LibEvents());
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		
		GeneralRegistry.registerCommand(new CMDDim());
		GeneralRegistry.registerCommand(new CMDStack());
		GeneralRegistry.registerCommand(new CMDKick());
		
		TickReg.regServer(new TickSaver());
		
		GeneralRegistry.replaceVanillaCommand("seed", new CMDSeedGet());
		if(Config.replaceTP)
		{
			GeneralRegistry.replaceVanillaCommand("tp",new CMDTP());
			GeneralRegistry.replaceVanillaCommand("teleport",new CMDTeleport());
		}
//		BlockApi.setMaterial(Blocks.DIAMOND_ORE,Material.WOOD,"axe");
//		BlockApi.setMaterial(Blocks.DIRT,Material.ROCK,"shovel");

		BlockProperties props = new BlockProperties(new ResourceLocation(MODID + ":" + "spider"),"pickaxe",11f,10f,1,SoundType.SNOW,20,100,10.6f,2);
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
	public void init(FMLLoadCompleteEvent e) throws Exception
	{
		proxy.onLoadComplete();
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent e)
	{
		proxy.lang();
		try
		{
			proxy.jsonGen();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	    proxy.postinit();//generate lang,generate shadow sizes
		
		if(!Config.isDev)
		{
			cfgArmors.updateConfig(true, false, true);
			cfgTools.updateConfig(true, false, true);
			cfgBlockProps.updateConfig(true, false, true);
		}
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
	    		basic.setArmorSet(set);
	    	}
	    	if(c.getItem() instanceof IBasicArmor)
	    	{
	    		IBasicArmor basic = (IBasicArmor)c.getItem();
	    		basic.setArmorSet(set);
	    	}
	    	if(l.getItem() instanceof IBasicArmor)
	    	{
	    		IBasicArmor basic = (IBasicArmor)l.getItem();
	    		basic.setArmorSet(set);
	    	}
	    	if(b.getItem() instanceof IBasicArmor)
	    	{
	    		IBasicArmor basic = (IBasicArmor)b.getItem();
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
		if(CapabilityReg.reg.size() == 0)
			return;
		
		System.out.println("Server is stopping. Saving capabilities");
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
		for(EntityPlayerMP p : players)
		{
			CapabilityReg.saveToFile(p);
			CapabilityReg.removeCapailityContainer(p);
		}
		
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
		
		GameRules g = e.getServer().getEntityWorld().getGameRules();
		GeneralRegistry.removeActiveGameRules(g);
		GeneralRegistry.injectGameRules(g);
		
		//directories instantiate
		System.out.println("Server Starting Event:");
		LibEvents.worlDir = server.worlds[0].getSaveHandler().getWorldDirectory();
		LibEvents.playerDataDir = new File(LibEvents.worlDir,"playerdata");
		LibEvents.playerDataNames = new File(LibEvents.worlDir,"playerdata/names");
	}

	public static boolean isDeObfucscated()
    {
    	try
    	{
    		ReflectionHelper.findField(Block.class, MCPMappings.getFieldOb(Block.class,"blockHardness"));
    		return false;//return false since obfuscated field had no exceptions
    	}
    	catch(Exception e){return true;}
    }

}