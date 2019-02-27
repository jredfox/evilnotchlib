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
import com.evilnotch.lib.minecraft.basicmc.client.creativetab.BasicCreativeTab;
import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;
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
