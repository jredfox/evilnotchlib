package com.evilnotch.lib.minecraft.basicmc.auto.block;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class BlockProperty {
	
	public ResourceLocation propId = null;
	public Material mat = null;
	public String harvestTool = null;
	public float blockHardness = 0f;
	public float blastResistance = 0f;
	public int harvestLvl = -1;
	public SoundType sound = null;
	public int flameEncoragement = -1;
	public int flamability = -1;
	public float slipperiness = 0.6F;
	public int lightValue = 0;
	
	public int lightOpacity = 0;
	public boolean translucent = false;
	public boolean useNeighborBrightness = false;
	public boolean enableStats = true;
	public float blockParticleGravity = 1.0F;
		
	/**
	 * Construct default with only default values you manipulate what you want
	 */
	public BlockProperty()
	{
		
	}
	
	public BlockProperty(ResourceLocation propId, ResourceLocation idMat)
	{
		this.propId = propId;
		this.mat = GeneralRegistry.getMatFromReg(idMat);
	}
	
	/**
	 * Basic constructor
	 * loc is the resource location of the block
	 * and mat is the material of that block
	 */
	public BlockProperty(ResourceLocation propId, ResourceLocation idMat, String tool, float hard, float resist, int lvl)
	{
		this(propId, idMat, tool, hard, resist, lvl, null);
	}
	
	public BlockProperty(ResourceLocation propId,ResourceLocation idMat,String tool,float hard,float resist,int lvl, SoundType sound)
	{
		this(propId, idMat, tool, hard, resist, lvl, sound, -1, -1);
	}
	
	public BlockProperty(ResourceLocation propId, ResourceLocation idMat, String tool, float hard, float resist, int lvl, SoundType sound,int flamE,int flame)
	{
		this(propId, idMat, tool, hard, resist, lvl, sound, flamE, flame, 0.6F, 0);
	}
	
	public BlockProperty(ResourceLocation propId, ResourceLocation idMat, String tool, float hard, float resist, int lvl, SoundType sound, int flameE, int flame, float slip, int light)
	{
		this(propId, idMat,tool, hard,resist, lvl, sound, flameE, flame, slip, light, 0, false, false, true, 1.0F);
	}
	
	public BlockProperty(ResourceLocation propId, ResourceLocation idMat, String tool, float hard, float resist, int lvl, SoundType sound, int flameE, int flame, float slip, int light, int lightOpacity, boolean translucent, boolean useNeighborBrightness, boolean enableStats, float blockParticleGravity)
	{
		this.propId = propId;
		this.mat = GeneralRegistry.getMatFromReg(idMat);
		this.harvestTool = tool;
		this.blockHardness = hard;
		this.blastResistance = resist;
		this.harvestLvl = lvl;
		this.sound = sound;
		this.flameEncoragement = flameE;
		this.flamability = flame;
		this.slipperiness = slip;
		this.lightValue = light;
		
		this.lightOpacity = lightOpacity;
		this.translucent = translucent;
		this.useNeighborBrightness = useNeighborBrightness;
		this.enableStats = enableStats;
		this.blockParticleGravity = blockParticleGravity;
	}
	
	public static Map<ResourceLocation,BlockProperty> propReg = new HashMap();
	public static void saveBlockProps()
	{
		JSONObject json = new JSONObject();
		for(Map.Entry<ResourceLocation, BlockProperty> pair : propReg.entrySet())
		{
			String loc = pair.getKey().toString();
			BlockProperty prop = pair.getValue();
			JSONObject p = new JSONObject();
			savePropertyToJSON(p, prop);
			json.put(loc, p);
		}
		
		try 
		{
			JavaUtil.saveJSONSafley(json, new File(Config.cfg.getParent(),"auto/properties/blockproperties.json"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		propReg.clear();
	}
	
	public static void savePropertyToJSON(JSONObject json, BlockProperty prop)
	{
		json.put("mat", GeneralRegistry.getMaterialLoc(prop.mat));
		json.put("harvestTool", prop.harvestTool);
		json.put("blockHardness", prop.blockHardness);
		json.put("blastResistance", prop.blastResistance);
		json.put("harvestLvl", prop.harvestLvl);
		json.put("soundType", MinecraftUtil.getSoundTypeJSON(prop.sound));
		json.put("flameEncoragement", prop.flameEncoragement);
		json.put("flamability", prop.flamability);
		json.put("slipperiness", prop.slipperiness);
		json.put("lightValue", prop.lightValue);
		json.put("lightOpacity", prop.lightOpacity);
		json.put("translucent", prop.translucent);
		json.put("useNeighborBrightness", prop.useNeighborBrightness);
		json.put("enableStats", prop.enableStats);
		json.put("blockParticleGravity", prop.blockParticleGravity);
	}
	
	public static void parseProperties()
	{
		File f = new File(Config.cfg.getParent(),"auto/properties/blockproperties.json");
		if(!f.exists())
			return;
		
		JSONObject json = JavaUtil.getJson(f);
		for(Object obj : json.entrySet())
		{
			Map.Entry<String, JSONObject> pair = (Map.Entry<String, JSONObject>)obj;
			ResourceLocation loc = new ResourceLocation(pair.getKey());
			propReg.put(loc, getProperty(loc, pair.getValue() ));
		}
	}
	
	/**
	 * parse from the disk
	 */
	public static BlockProperty getProperty(ResourceLocation propId, JSONObject json)
	{
		ResourceLocation mat = new ResourceLocation(json.getString("mat"));
		String harvestTool = json.getString("harvestTool");
		float blockHardness = json.getFloat("blockHardness");
		float blastResistance = json.getFloat("blastResistance");
		int harvestLvl = json.getInt("harvestLvl");
		SoundType type = MinecraftUtil.getSoundType(json.getJSONObject("soundType"));
		int flameEncoragement = json.getInt("flameEncoragement");
		int flamability = json.getInt("flamability");
		float slipperiness = json.getFloat("slipperiness");
		int lightValue = json.getInt("lightValue");
		
		int lightOpacity = json.getInt("lightOpacity");
		boolean translucent = json.getBoolean("translucent");
		boolean useNeighborBrightness = json.getBoolean("useNeighborBrightness");
		boolean enableStats = json.getBoolean("enableStats");
		float blockParticleGravity = json.getFloat("blockParticleGravity");
		
		return new BlockProperty(propId, mat, harvestTool, blockHardness, blastResistance, harvestLvl, type,
				flameEncoragement, flamability, slipperiness, lightValue, 
				lightOpacity, translucent, useNeighborBrightness, enableStats, blockParticleGravity);
	}

	public static BlockProperty getProperty(BlockProperty props, boolean config) 
	{
		if(!config)
			return props;
		
		ResourceLocation loc = props.propId;
		if(propReg.containsKey(loc))
		{
			System.out.println(props);
			return propReg.get(loc);
		}
		
		propReg.put(props.propId, props);
		return props;
	}
	
	@Override
	public String toString()
	{
		JSONObject json = new JSONObject();
		savePropertyToJSON(json, this);
		json.put("propId", this.propId.toString());
		return "" + json;
	}
	
}
