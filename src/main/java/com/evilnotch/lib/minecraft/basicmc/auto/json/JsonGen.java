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
import com.evilnotch.lib.minecraft.basicmc.auto.BasicBlockJSONMeta;
import com.evilnotch.lib.minecraft.basicmc.auto.BasicItemJSON;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicBlock;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicBlockMeta;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicItem;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicItemMeta;
import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;
import com.evilnotch.lib.minecraft.basicmc.client.block.StateMapperSupreme;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PairString;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class JsonGen {
	
	public static List<BasicBlockJSON> blocks = new ArrayList<BasicBlockJSON>();
	public static List<BasicBlockJSONMeta> blocks_meta = new ArrayList<BasicBlockJSONMeta>();
	
	public static List<BasicItemJSON> items = new ArrayList<BasicItemJSON>();
	public static List<BasicItemJSONMeta> items_meta = new ArrayList<BasicItemJSONMeta>();
	
	public static void registerBlockJson(Block b)
	{
		registerBlockJson(b, ModelPart.cube_all);
	}
	
	public static void registerBlockJson(Block b, ModelPart part)
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		blocks.add(new BasicBlockJSON(b, part));
	}
	
	public static void registerItemJson(Item i)
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		items.add(new BasicItemJSON(i));
	}
	
	public static void registerBlockMetaJson(Block b, IProperty p)
	{
		registerBlockMetaJson(b, ModelPart.cube_all, p);
	}
	
	public static void registerBlockMetaJson(Block b, ModelPart part, IProperty p)
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		blocks_meta.add(new BasicBlockJSONMeta(b, part, p));
	}
	
	public static void registerItemMetaJson(Item i, int maxMeta)
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		items_meta.add(new BasicItemJSONMeta(i, maxMeta));
	}
	
	public static void genJSONS() throws IOException
	{
		if(!LoaderMain.isDeObfuscated)
			return;
		
		LoaderGen.checkRootFile();
		
		for(BasicItemJSON i : items)
		{
			if(MinecraftUtil.isModCompiled(i.getResourceLocation().getResourceDomain()))
				continue;
			JSONObject json = getJSONItem(getParentModel(i.getObject()), i, 0);
			ResourceLocation loc = i.getResourceLocation();
			File file = new File(LoaderGen.root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + ".json");
			JavaUtil.saveJSON(json, file);
		}
		for(BasicItemJSONMeta item : items_meta)
		{
			if(MinecraftUtil.isModCompiled(item.getResourceLocation().getResourceDomain()))
				continue;
			for(int index=0;index<=item.maxMeta;index++)
			{
				JSONObject json = getJSONItem(getParentModel(item.getObject()), item, index);
				ResourceLocation loc = item.getResourceLocation();
				File file = new File(LoaderGen.root,loc.getResourceDomain() + "/models/item/" + loc.getResourcePath() + "_" + index + ".json");
				JavaUtil.saveJSON(json, file);
			}
		}
		
		for(BasicBlockJSON i : blocks)
		{
			if(MinecraftUtil.isModCompiled(i.getResourceLocation().getResourceDomain()))
				continue;
			
			for(IBlockState state : i.getObject().blockState.getValidStates())
			{
				JSONObject json = getJSONBlock(i, null);
				ResourceLocation loc = i.getResourceLocation();
				File file = new File(LoaderGen.root, loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + ".json");
				JavaUtil.saveJSON(json, file);
			}
		}
		for(BasicBlockJSONMeta i : blocks_meta)
		{
			if(MinecraftUtil.isModCompiled(i.getResourceLocation().getResourceDomain()))
				continue;
			
			for(IBlockState state : i.getObject().blockState.getValidStates())
			{
				JSONObject json = getJSONBlock(i, state);
				ResourceLocation loc = i.getResourceLocation();
				File file = new File(LoaderGen.root, loc.getResourceDomain() + "/models/block/" + loc.getResourcePath() + "_" + BlockApi.getBlockStateNameJSON(state, i.getProperty()) + ".json");
				JavaUtil.saveJSON(json, file);
			}
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
		textures.put("layer0", loc.getResourceDomain() + ":items/" + item.getTextureName() + (meta > 0 ? "_" + meta : "") );
		json.put("textures", textures);
		return json;
	}
	
	public static JSONObject getJSONBlock(IBasicBlock block, IBlockState state)
	{
		JSONObject json = new JSONObject();
		json.put("parent", block.getModelPart().parent);
		Block b = (Block) block.getObject();
		
		JSONObject textures = new JSONObject();
		ResourceLocation loc = block.getResourceLocation();

		for(PairString s : block.getModelPart().getParts())
		{
			String side = s.getValue();
			textures.put(side, loc.getResourceDomain() + ":blocks/" + block.getTextureName() + (state != null ? "_" + BlockApi.getBlockStateNameJSON(state, ((IBasicBlockMeta)block).getProperty() ) : "") + (side.equals("all") ? "" : "_" + side) );
		}
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
