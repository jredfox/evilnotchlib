package com.EvilNotch.lib.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.EvilNotch.lib.Api.BlockApi;
import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.eventhandlers.LibEvents;
import com.EvilNotch.lib.main.eventhandlers.UUIDFixer;
import com.EvilNotch.lib.main.eventhandlers.VanillaBugFixes;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.minecraft.NBTUtil;
import com.EvilNotch.lib.minecraft.SkinUpdater;
import com.EvilNotch.lib.minecraft.content.ArmorSet;
import com.EvilNotch.lib.minecraft.content.FakeWorld;
import com.EvilNotch.lib.minecraft.content.ToolSet;
import com.EvilNotch.lib.minecraft.content.blocks.IBasicBlock;
import com.EvilNotch.lib.minecraft.content.commands.CMDDim;
import com.EvilNotch.lib.minecraft.content.commands.CMDKick;
import com.EvilNotch.lib.minecraft.content.commands.CMDSeedGet;
import com.EvilNotch.lib.minecraft.content.items.IBasicItem;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityContainer;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityHandler;
import com.EvilNotch.lib.minecraft.content.pcapabilites.CapabilityReg;
import com.EvilNotch.lib.minecraft.proxy.ServerProxy;
import com.EvilNotch.lib.minecraft.registry.GeneralRegistry;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.ConfigBase;
import com.EvilNotch.lib.util.Line.ConfigEnhanced;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import it.unimi.dsi.fastutil.floats.Float2ByteMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.command.ICommand;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;


@Mod(modid = MainJava.MODID,name = MainJava.NAME, version = MainJava.VERSION,acceptableRemoteVersions = "*")
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
	public static final String VERSION = "1.2.3-43";//SNAPSHOT
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
		proxy.preinit();
		GeneralRegistry.load();
		
		cfgTools = new ConfigEnhanced(new File(Config.cfg.getParent(),"config/tools.cfg"));
		cfgArmors = new ConfigEnhanced(new File(Config.cfg.getParent(),"config/armor.cfg"));
		cfgBlockProps = new ConfigEnhanced(new File(Config.cfg.getParent(),"config/blockprops.cfg"));
		
		MinecraftForge.EVENT_BUS.register(new UUIDFixer());
		MinecraftForge.EVENT_BUS.register(new VanillaBugFixes());
		MinecraftForge.EVENT_BUS.register(new LibEvents());
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		
		GeneralRegistry.registerCommand(new CMDDim());
		GeneralRegistry.registerCommand(new CMDSeedGet());
		GeneralRegistry.registerCommand(new CMDKick());
		
		File dir = e.getModConfigurationDirectory().getParentFile();
		File skinCache = new File(dir,"skinCache.json");
		if(skinCache.exists())
		{
			try
			{
				JSONParser parser = new JSONParser();
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(skinCache),StandardCharsets.UTF_8) );
				JSONArray arr = (JSONArray) parser.parse(reader);
				Iterator<JSONObject> it = arr.iterator();
				while(it.hasNext())
				{
					JSONObject obj = it.next();
					SkinUpdater.uuids.put((String)obj.get("name"), (String)obj.get("uuid"));
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
//		BlockApi.setMaterial(Blocks.DIAMOND_ORE,Material.WOOD,"axe");
//		BlockApi.setMaterial(Blocks.DIRT,Material.ROCK,"shovel");

//		BasicBlock.Properties props = new BasicBlock.Properties(new ResourceLocation(MODID + ":" + "spider"),Material.CACTUS,"pickaxe",11f,10f,1,SoundType.SNOW,20,100,10.6f,2);
//		BasicCreativeTab tab = new BasicCreativeTab("spiderTesting",new ItemStack(Items.CAKE),new LangEntry("Custom Shiny Tab","en_us"),new LangEntry("Ã�Å¸Ã�Â¾Ã�Â»Ã‘Å’Ã�Â·Ã�Â¾Ã�Â²Ã�Â°Ã‘â€šÃ�ÂµÃ�Â»Ã‘Å’Ã‘ï¿½Ã�ÂºÃ�Â°Ã‘ï¿½ Ã�Â±Ã�Â»Ã�ÂµÃ‘ï¿½Ã‘â€šÃ‘ï¿½Ã‘â€°Ã�Â°Ã‘ï¿½ Ã�Â²Ã�ÂºÃ�Â»Ã�Â°Ã�Â´Ã�ÂºÃ�Â°","ru_ru") );
//		BasicItem item = new BasicItem(new ResourceLocation(MODID + ":" + "stick"),tab,new LangEntry("Modded Stick","en_us"));
//		BasicBlock b = new BasicBlock(Material.ROCK, new ResourceLocation(MODID + ":" + "spider"),tab,props,new LangEntry("Spider Master","en_us"),new LangEntry("Ã�Â¿Ã�Â°Ã‘Æ’Ã�Âº","ru_ru"));
	}
	@Mod.EventHandler
	public void post(FMLInitializationEvent e)
	{
		proxy.initMod();
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent e)
	{
	    proxy.postinit();//generate lang,generate shadow sizes
	    
		long time = System.currentTimeMillis();
		EntityUtil.cacheEnts();
		JavaUtil.printTime(time, "Entity Util Cached Ents:");
		
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
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
		
		System.out.println("Server is stopping is this a cliiiiiiiiiiiiiient?");
		Iterator<Map.Entry<String,CapabilityContainer>> it = CapabilityReg.capabilities.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<String,CapabilityContainer> pair = it.next();
			String name = pair.getKey();
			EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(name);
			File f = new File(LibEvents.playerDataDir,"caps/" + player.getUniqueID().toString() + ".dat");
			NBTTagCompound nbt = new NBTTagCompound();
			CapabilityReg.save(player,nbt);
			NBTUtil.updateNBTFileSafley(f, nbt);
			System.out.print("saved player:" + name + " toFile:" + f.getName() + ".dat\n");
			it.remove();
		}
		UUIDFixer.count = 0;
		
		//player premium uuid cache
		File rootDir = Config.cfg.getParentFile().getParentFile().getParentFile();
		Iterator<Map.Entry<String,String>> itSkin = SkinUpdater.uuids.entrySet().iterator();
		JSONArray arr = new JSONArray();
		while(itSkin.hasNext())
		{
			Map.Entry<String, String> pair = itSkin.next();
			String username = pair.getKey();
			String value = pair.getValue();
			JSONObject json = new JSONObject();
			json.put("name", username);
			json.put("uuid", value);
			arr.add(json);
		}
		File cache = new File(rootDir,"skinCache.json");
		if(!cache.exists())
		{
			try 
			{
				cache.createNewFile();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		JavaUtil.saveFileLines(JavaUtil.asArray(new String[]{arr.toJSONString()}), cache, true);
	}
	
	@Mod.EventHandler
	public void commandRegister(FMLServerStartingEvent e)
	{
		for(ICommand cmd : GeneralRegistry.getCmdList())
			e.registerServerCommand(cmd);
	}

	public static boolean isDeObfucscated()
    {
    	try{
    		ReflectionHelper.findField(Block.class, MCPMappings.getFieldOb(Block.class,"blockHardness"));
    		return false;//return false since obfuscated field had no exceptions
    	}
    	catch(Exception e){return true;}
    }

}