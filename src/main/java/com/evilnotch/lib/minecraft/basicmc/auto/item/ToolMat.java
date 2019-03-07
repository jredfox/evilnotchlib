package com.evilnotch.lib.minecraft.basicmc.auto.item;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.ralleytn.simple.json.JSONObject;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.IEnumContainer;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

public class ToolMat implements IEnumContainer{
	
	/**
	 * a preconfigured versions of this armor enums gets cleared in post init
	 */
	public static HashMap<ResourceLocation,ToolMat> toolenums = new HashMap();
	
    public ResourceLocation id;
    public String enumName;
    
    public int harvestLevel;
    public int maxUses;
    public float efficiency;
    public float attackDamage;
    public int enchantability;

    public ToolMat(ResourceLocation id,int harvestLevel, int maxUses, float efficiency, float damageVsEntity, int enchantability)
    {
        this.id = id;
        this.enumName =  id.toString().replaceAll(":", "_");
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = damageVsEntity;
        this.enchantability = enchantability;
    }

	@Override
    public ToolMaterial getEnum()
	{
		return EnumHelper.addToolMaterial(this.enumName, this.harvestLevel, this.maxUses, this.efficiency, this.attackDamage, this.enchantability);
    }
	
    @Override
    public String toString()
    {
    	return "\"" + this.enumName + "\" = [" + this.harvestLevel + ", " + this.maxUses + ", " + this.efficiency + "f, " + this.attackDamage + "f, " + this.enchantability + "]";
    }

	public static ToolMaterial getMat(ToolMat mat, boolean config)
	{
		if(!config)
			return mat.getEnum();
		if(!toolenums.containsKey(mat.id))
		{
			ToolMat.toolenums.put(mat.id, mat);
			return mat.getEnum();
		}
		return toolenums.get(mat.id).getEnum();
	}
	
	public static void parseToolMats() 
	{
		File tool = new File(Config.cfg.getParent(),"auto/properties/toolmats.json");
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
	
	public static void saveToolMats()
	{
		if(ToolMat.toolenums.isEmpty())
			return;
		
		JSONObject toolJson = new JSONObject();
		File tool = new File(Config.cfg.getParent(),"auto/properties/toolmats.json");
		for(Map.Entry<ResourceLocation,ToolMat> pair : ToolMat.toolenums.entrySet())
		{
			toolJson.put(pair.getKey(), saveToolJSON(pair.getValue()));
		}
		try 
		{
			JavaUtil.saveJSONSafley(toolJson, tool);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		toolJson.clear();
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
	
	private static ToolMat parseToolMat(ResourceLocation loc, JSONObject json)
	{
		int harvestLevel = json.getInt("harvestLevel");
		int maxUses = json.getInt("maxUses");
		float efficiency = json.getFloat("efficiency");
		float attackDamage = json.getFloat("attackDamage");
		int enchantability = json.getInt("enchantability");
		return new ToolMat(loc, harvestLevel, maxUses, efficiency, attackDamage, enchantability);
	}

}
