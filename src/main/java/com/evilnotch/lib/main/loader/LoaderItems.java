package com.evilnotch.lib.main.loader;

import java.io.File;
import java.util.ArrayList;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicItem;
import com.evilnotch.lib.minecraft.basicmc.item.armor.ArmorSet;
import com.evilnotch.lib.minecraft.basicmc.item.armor.IBasicArmor;
import com.evilnotch.lib.minecraft.basicmc.item.tool.ToolSet;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigNonMeta;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LoaderItems {
	
	public static ArrayList<IBasicItem> items = new ArrayList();
	public static ArrayList<ArmorSet> armorsets = new ArrayList();
	public static ArrayList<ToolSet> toolsets = new ArrayList();
	
	public static ConfigBase cfgTools = null;
	public static ConfigBase cfgArmors = null;
	
	public static void loadpreinit()
	{
		loadConfig();
	}

	private static void loadConfig() 
	{
		cfgTools = new ConfigNonMeta(new File(Config.cfg.getParent(),"config/tools.cfg"));
		cfgArmors = new ConfigNonMeta(new File(Config.cfg.getParent(),"config/armor.cfg"));
		cfgTools.loadConfig();
		cfgArmors.loadConfig();
	}

	public static void registerItems() 
	{
		for(IBasicItem i : LoaderItems.items)
		{
			ForgeRegistries.ITEMS.register((Item)i.getObject());
		}
	}

	public static void registerRecipes(Register<IRecipe> event) 
	{
		//this is how block armor was created via this algorithm basically
	    for(ArmorSet set : LoaderItems.armorsets)
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
	    		GeneralRegistry.addShapedRecipe(h, new Object[]{"bbb","b b",'b',meta ? block.getItem() : block } );
	    	//chestplate
	    	if(c != null)
	    		GeneralRegistry.addShapedRecipe(c, new Object[]{"b b","bbb","bbb",'b',meta ? block.getItem() : block} );
	    	//leggings
	    	if(l != null)
	    		GeneralRegistry.addShapedRecipe(l, new Object[]{"bbb","b b","b b",'b',meta ? block.getItem() : block} );
	    	//boots
	    	if(b != null)
	    		GeneralRegistry.addShapedRecipe(b, new Object[]{"b b","b b",'b',meta ? block.getItem() : block} );
	    }
	    //generator for tools
	    for(ToolSet set : LoaderItems.toolsets)
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
	    		GeneralRegistry.addShapedRecipe(pickaxe, new Object[]{"bbb"," s "," s ",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    	if(axe != null)
	    		GeneralRegistry.addShapedRecipe(axe, new Object[]{"bb ","bs "," s ",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    	if(sword != null)
	    		GeneralRegistry.addShapedRecipe(sword, new Object[]{"b","b","s",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    	if(spade != null)
	    		GeneralRegistry.addShapedRecipe(spade, new Object[]{"b","s","s",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    	if(hoe != null)
	    		GeneralRegistry.addShapedRecipe(hoe, new Object[]{"bb "," s "," s ",'b',mb ? block.getItem() : block,'s',ms ? stick.getItem() : stick} );
	    }
	}

	public static void loadpostinit() 
	{
		if(!LoaderMain.isDeObfuscated)
		{
			LoaderItems.cfgArmors.saveConfig(true, false, true);
			LoaderItems.cfgTools.saveConfig(true, false, true);
		}
		clearItems();
	}

	private static void clearItems() 
	{
		LoaderItems.items.clear();
		LoaderItems.armorsets.clear();
		LoaderItems.toolsets.clear();
		cfgTools = null;
		cfgArmors = null;
	}

}
