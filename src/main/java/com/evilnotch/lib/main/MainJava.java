package com.evilnotch.lib.main;

import java.io.File;

import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.loader.LoaderBlocks;
import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.content.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.content.block.BasicBlock;
import com.evilnotch.lib.minecraft.content.block.BasicMetaBlock;
import com.evilnotch.lib.minecraft.content.block.BlockProperties;
import com.evilnotch.lib.minecraft.content.block.IBasicBlock;
import com.evilnotch.lib.minecraft.content.block.property.PropertyMetaEnum;
import com.evilnotch.lib.minecraft.content.block.test.EnumCheese;
import com.evilnotch.lib.minecraft.content.block.test.MultiSidedGrass;
import com.evilnotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.evilnotch.lib.minecraft.content.item.BasicItem;
import com.evilnotch.lib.minecraft.content.item.BasicItemMeta;
import com.evilnotch.lib.minecraft.content.item.IBasicItem;
import com.evilnotch.lib.minecraft.content.item.armor.ArmorSet;
import com.evilnotch.lib.minecraft.content.item.armor.IBasicArmor;
import com.evilnotch.lib.minecraft.content.item.tool.ItemBasicPickaxe;
import com.evilnotch.lib.minecraft.content.item.tool.ToolMat;
import com.evilnotch.lib.minecraft.content.item.tool.ToolSet;
import com.evilnotch.lib.minecraft.content.tick.TickReg;
import com.evilnotch.lib.minecraft.content.world.FakeWorld;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.proxy.ServerProxy;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.util.EntityUtil;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.command.ICommand;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = MainJava.MODID,name = MainJava.NAME, version = MainJava.VERSION)
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
	
	/**
	 * clear various data from memory
	 */
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{	
		LoaderMain.serverStopping();
	}
	
	/**
	 * register all commands
	 */
	@Mod.EventHandler
	public void commandRegister(FMLServerStartingEvent e)
	{
		LoaderMain.serverStart(e);
	}
	
}
