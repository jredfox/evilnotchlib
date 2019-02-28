package com.evilnotch.lib.main.loader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.item.armor.ArmorMat;
import com.evilnotch.lib.minecraft.basicmc.item.tool.ToolMat;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.LineArray;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class LoaderGen {
	
	public static File root = null;
	public static File root_sync = null;
	
	public static void gen()
	{
		if(LoaderMain.isClient)
		{
			LangRegistry.generateLang();
			try
			{
				JsonGen.genJSONS();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		loadpostinit();
	}
	
	public static void checkRootFile() 
	{
		if(root != null && root_sync != null)
			return;
		File mdk = Config.cfg.getParentFile().getParentFile().getParentFile().getParentFile();
		root = new File(mdk,"src/main/resources/assets");
		root_sync = new File(mdk,"bin/assets");
		if(!root.exists())
			root.mkdirs();
		if(!root_sync.exists())
			root.mkdirs();
	}
	
	/**
	 * input the original root dir of any file src/main/resources/assets/file.* and get bin/assets/file.*
	 */
	public static File getSyncFile(File root)
	{
		return new File(LoaderGen.root_sync, root.getAbsolutePath().substring(LoaderGen.root.getAbsolutePath().length(), root.getAbsolutePath().length()) );
	}
	
	/**
	 * loads armor and tool mats into objects
	 */
	public static JSONObject armorConfig = new JSONObject();
	public static JSONObject toolConfig = new JSONObject();
	public static void loadpreinit() 
	{
		parseToolMats();
		parseArmorMats();
	}
	
	public static void loadpostinit()
	{
		saveArmor();
		saveTool();
	}
	
	private static void parseToolMats() 
	{
		File tool = new File(Config.cfg.getParent(),"configs/toolmats.json");
		
		if(!tool.exists())
			return;
		JSONObject json = JavaUtil.getJson(tool);
		for(Object obj : json.entrySet())
		{
			Map.Entry<String, JSONObject> pair = (Map.Entry<String, JSONObject>)obj;
			ResourceLocation loc = new ResourceLocation(pair.getKey());
			ToolMat.toolenums.put(loc, parseToolMat(loc, pair.getValue()) );
		}
	}

	private static void parseArmorMats() 
	{
		File armor = new File(Config.cfg.getParent(),"configs/armormats.json");
		if(!armor.exists())
			return;
		JSONObject json = JavaUtil.getJson(armor);
		for(Object obj : json.entrySet())
		{
			Map.Entry<String, JSONObject> pair = (Map.Entry<String, JSONObject>)obj;
			ResourceLocation loc = new ResourceLocation(pair.getKey());
			ArmorMat.armorenums.put(loc, parseArmorMat(loc, pair.getValue()) );
		}
	}
	

	public static void addMat(ArmorMat mat)
	{
		ArmorMat.armorenums.put(mat.id, mat);
	}
	
	public static void addMat(ToolMat mat)
	{
		ToolMat.toolenums.put(mat.id, mat);
	}
	
	public static void saveArmor()
	{
		File armor = new File(Config.cfg.getParent(),"configs/armormats.json");
		for(Map.Entry<ResourceLocation,ArmorMat> pair : ArmorMat.armorenums.entrySet())
		{
			armorConfig.put(pair.getKey(), saveArmorJSON(pair.getValue()));
		}
		try 
		{
			JavaUtil.saveJSON(armorConfig, armor);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		armorConfig.clear();
	}
	
	public static void saveTool()
	{
		File tool = new File(Config.cfg.getParent(),"configs/toolmats.json");
		for(Map.Entry<ResourceLocation,ToolMat> pair : ToolMat.toolenums.entrySet())
		{
			toolConfig.put(pair.getKey(), saveToolJSON(pair.getValue()));
		}
		try 
		{
			JavaUtil.saveJSON(toolConfig, tool);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		toolConfig.clear();
	}
	
	private static JSONObject saveArmorJSON(ArmorMat mat) 
	{
		JSONObject json = new JSONObject();
		json.put("textureName", mat.textureName);
		json.put("durability", mat.durability);
		json.put("damageReduction", JavaUtil.staticToArray(mat.damageReduction));
		json.put("enchantability", mat.enchantability);
		json.put("soundEvent", SoundEvent.REGISTRY.getNameForObject(mat.soundEvent));
		json.put("toughness", mat.toughness);
		return json;
	}
	
	private static JSONObject saveToolJSON(ToolMat mat)
	{
		JSONObject json = new JSONObject();
		json.put("harvestLevel", mat.harvestLevel);
		json.put("maxUses", mat.maxUses);
		json.put("efficiency", mat.efficiency);
		json.put("attackDamage", mat.attackDamage);
		json.put("enchantability", mat.enchantability);
		return json;
	}
	
	 public static ArmorMat parseArmorMat(ResourceLocation loc, JSONObject json)
	 {
		 String textureName = (String) json.get("textureName");
		 int durability = (int) ((long) json.get("durability") );
		 int[] damageReduction = (int[]) JavaUtil.getStaticArrayInts(json.get("damageReduction").toString() );
		 int enchantability = (int) (long)json.get("enchantability");
		 SoundEvent event = SoundEvent.REGISTRY.getObject(new ResourceLocation("" + json.get("soundEvent")));
		 float tough = (float) (double)json.get("toughness");
		 
		 return new ArmorMat(loc, new ResourceLocation(textureName), durability, damageReduction, enchantability, event, tough);
	 }
	
	private static ToolMat parseToolMat(ResourceLocation loc, JSONObject json)
	{
		int harvestLevel = (int) (long)json.get("harvestLevel");
		int maxUses = (int) (long)json.get("maxUses");
		float efficiency = (float) (double)json.get("efficiency");
		float attackDamage = (float) (double)json.get("attackDamage");
		int enchantability = (int) (long)json.get("enchantability");
		return new ToolMat(loc, harvestLevel, maxUses, efficiency, attackDamage, enchantability);
	}

}
