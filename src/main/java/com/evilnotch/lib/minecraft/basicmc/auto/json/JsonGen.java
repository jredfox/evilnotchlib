package com.evilnotch.lib.minecraft.basicmc.auto.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.evilnotch.lib.main.loader.LoaderGen;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.auto.BasicBlockJSON;
import com.evilnotch.lib.minecraft.basicmc.auto.BasicItemJSON;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicItem;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;

public class JsonGen {
	
	public static List<BasicBlockJSON> blocks = new ArrayList<BasicBlockJSON>();
//	public static List<IBasicBlockMeta> blocks_meta = new ArrayList<IBasicBlockMeta>();
	
	public static List<BasicItemJSON> items = new ArrayList<BasicItemJSON>();
//	public static List<IBasicItemMeta> items_meta = new ArrayList<IBasicItemMeta>();
	
	public static void registerBlockJson(Block b)
	{
		blocks.add(new BasicBlockJSON(b));
	}
	
	public static void registerItemJson(Item i)
	{
		items.add(new BasicItemJSON(i));
	}
	
	public static void registerBlockMetaJson(Block b)
	{
		
	}
	
	public static void registerItemMetaJson(Item i)
	{
		
	}
	
	public static void genJSONS() throws IOException
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		
		LoaderGen.checkRootFile();
		
		for(BasicItemJSON i : items)
		{
			JSONObject json = getJSONItem(getParentModel(i.getObject()), i, 0);
			ResourceLocation loc = i.getResourceLocation();
			File file = new File(LoaderGen.root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
			JavaUtil.saveIfJSON(json, file);
		}
		for(BasicBlockJSON i : blocks)
		{
			JSONObject json = getJSONItem(getParentModel(i.getObject()), i, 0);
			ResourceLocation loc = i.getResourceLocation();
			File file = new File(LoaderGen.root,loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + ".json");
			JavaUtil.saveIfJSON(json, file);
		}
	}
	
	public static String getParentModel(Block b)
	{
		return "block/generated";
	}
	
	public static String getParentModel(Item item) 
	{
		if(item instanceof ItemTool)
			return "item/handheld";
		else if(item instanceof ItemShield)
			return "item/shield";
		else if(item instanceof ItemSkull)
			return "item/skull";
			
		return "item/generated";
	}

	public static JSONObject getJSONItem(String parent, IBasicItem i, int meta)
	{
		Item item = (Item)i;
		JSONObject json = new JSONObject();
		json.put("parent", parent);
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = item.getRegistryName();
		if(meta > 0)
		{
			textures.put("layer0", loc.getResourceDomain() + ":items/" + i.getTextureName() + "_" + meta);
		}
		else
			textures.put("layer0", loc.getResourceDomain() + ":items/" + i.getTextureName());
		json.put("textures", textures);
		return json;
	}

	
}
