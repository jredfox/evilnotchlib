package com.evilnotch.lib.minecraft.basicmc.auto.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.evilnotch.lib.api.BlockApi;
import com.evilnotch.lib.main.loader.LoaderBlocks;
import com.evilnotch.lib.main.loader.LoaderGen;
import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.auto.BasicBlockJSON;
import com.evilnotch.lib.minecraft.basicmc.auto.BasicItemJSON;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicBlock;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicItem;
import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;
import com.evilnotch.lib.minecraft.basicmc.client.block.StateMapperSupreme;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class JsonGen {
	
	public static List<BasicBlockJSON> blocks = new ArrayList<BasicBlockJSON>();
//	public static List<IBasicBlockMeta> blocks_meta = new ArrayList<IBasicBlockMeta>();
	
	public static List<BasicItemJSON> items = new ArrayList<BasicItemJSON>();
//	public static List<IBasicItemMeta> items_meta = new ArrayList<IBasicItemMeta>();
	
	public static void registerBlockJson(Block b)
	{
		registerBlockJson(b, ModelPart.cube_all);
	}
	
	public static void registerBlockJson(Block b, ModelPart part)
	{
		blocks.add(new BasicBlockJSON(b, part));
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
			JavaUtil.saveJSONSafley(json, file);
		}
		
		for(BasicBlockJSON i : blocks)
		{
			JSONObject json = getJSONBlock(i,0);
			ResourceLocation loc = i.getResourceLocation();
			File file = new File(LoaderGen.root, loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + ".json");
			JavaUtil.saveJSONSafley(json, file);
		}
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

	public static JSONObject getJSONItem(String parent, IBasicItem<? extends Item> item, int meta)
	{
		JSONObject json = new JSONObject();
		json.put("parent", parent);
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = item.getResourceLocation();
		if(meta > 0)
		{
			textures.put("layer0", loc.getResourceDomain() + ":items/" + item.getTextureName() + "_" + meta);
		}
		else
			textures.put("layer0", loc.getResourceDomain() + ":items/" + item.getTextureName());
		json.put("textures", textures);
		return json;
	}
	
	public static JSONObject getJSONBlock(IBasicBlock block, int meta)
	{
		JSONObject json = new JSONObject();
		json.put("parent", block.getModelPart().parent);
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = block.getResourceLocation();
		if(meta > 0)
		{
			textures.put("all", loc.getResourceDomain() + ":blocks/" + block.getTextureName() + "_" + meta);
		}
		else
			textures.put("all", loc.getResourceDomain() + ":blocks/" + block.getTextureName());
		json.put("textures", textures);
		return json;
	}

	public static void registerModels() 
	{
		for(BasicItemJSON gen : items)
		{
			Item item = gen.getObject();
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(BlockApi.getItemString(item), "inventory"));
		}
		
		for(BasicBlockJSON gen : blocks)
		{
			Block b = gen.getObject();
			Item item = gen.getItem();
			ModelLoader.setCustomModelResourceLocation(item, 0,  new ModelResourceLocation(BlockApi.getBlockString(b), "inventory"));
			ModelLoader.setCustomModelResourceLocation(item, 0,  new ModelResourceLocation(BlockApi.getBlockString(b), "normal"));
		}
	}
	
}
