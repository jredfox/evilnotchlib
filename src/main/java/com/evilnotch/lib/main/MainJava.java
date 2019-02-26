package com.evilnotch.lib.main;

import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.test.EnumCheese;
import com.evilnotch.lib.minecraft.basicmc.auto.test.MultiSidedGrass;
import com.evilnotch.lib.minecraft.basicmc.block.BasicBlock;
import com.evilnotch.lib.minecraft.basicmc.block.BasicMetaBlock;
import com.evilnotch.lib.minecraft.basicmc.block.BlockProperties;
import com.evilnotch.lib.minecraft.basicmc.block.property.PropertyMetaEnum;
import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;
import com.evilnotch.lib.minecraft.basicmc.client.creativetab.BasicCreativeTab;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItemMeta;
import com.evilnotch.lib.minecraft.basicmc.item.tool.ItemBasicPickaxe;
import com.evilnotch.lib.minecraft.basicmc.item.tool.ToolMat;
import com.evilnotch.lib.minecraft.proxy.ServerProxy;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = MainJava.MODID, name = MainJava.NAME, version = MainJava.VERSION)
public class MainJava {
	
	public static final String MODID =  "evilnotchlib";
	public static final String VERSION = "1.2.3";//SNAPSHOT 74
	public static final String NAME = "Evil Notch Lib";
	public static final String max_version = "4.0.0.0.0";//allows for 5 places in lib version
	@SidedProxy(clientSide = "com.evilnotch.lib.minecraft.proxy.ClientProxy", serverSide = "com.evilnotch.lib.minecraft.proxy.ServerProxy")
	public static ServerProxy proxy;
	
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{	
		LoaderMain.loadpreinit(e);
		
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
		
		JsonGen.registerBlockJson(b);
		JsonGen.registerBlockMetaJson(b2, ModelPart.cube_all, b2.getProperty());
		JsonGen.registerBlockMetaJson(b3, ModelPart.cube_all, b3.getProperty());
		JsonGen.registerBlockMetaJson(b4, ModelPart.cube_all, b4.getProperty());
		JsonGen.registerBlockMetaJson(b5, ModelPart.cube_all, b5.getProperty());
		JsonGen.registerBlockMetaJson(b6, ModelPart.cube_bottom_top, b6.getProperty());
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e) throws Exception
	{
		LoaderMain.loadInit(e);
	}
	
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent e)
	{
		LoaderMain.loadPostInit(e);
	}
	
	@Mod.EventHandler
	public void complete(FMLLoadCompleteEvent e)
	{
		LoaderMain.loadComplete(e);
	}
	
	/**
	 * register all commands
	 */
	@Mod.EventHandler
	public void serverStart(FMLServerStartingEvent e)
	{
		LoaderMain.serverStart(e);
	}
	
	/**
	 * clear various data from memory
	 */
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{	
		LoaderMain.serverStopping();
	}
	
}
