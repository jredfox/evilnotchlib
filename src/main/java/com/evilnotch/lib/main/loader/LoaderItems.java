package com.evilnotch.lib.main.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.basicmc.item.armor.ArmorMat;
import com.evilnotch.lib.minecraft.basicmc.item.armor.ArmorSet;
import com.evilnotch.lib.minecraft.basicmc.item.armor.IBasicArmor;
import com.evilnotch.lib.minecraft.basicmc.item.tool.ToolMat;
import com.evilnotch.lib.minecraft.basicmc.item.tool.ToolSet;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigNonMeta;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class LoaderItems {
	
	public static List<Item> items = new ArrayList();
	public static List<ArmorSet> armorsets = new ArrayList();
	public static List<ToolSet> toolsets = new ArrayList();
	
	public static void loadpreinit()
	{
		
	}

	public static void registerItems() 
	{
		for(Item i : LoaderItems.items)
		{
			ForgeRegistries.ITEMS.register(i);
		}
		items.clear();
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
		clearArrays();
	}

	private static void clearArrays() 
	{
		LoaderItems.armorsets.clear();
		LoaderItems.toolsets.clear();
//		ArmorMat.armorenums.clear();
//		ToolMat.toolenums.clear();
	}

}
