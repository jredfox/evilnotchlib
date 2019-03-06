package com.evilnotch.lib.minecraft.basicmc.auto.item;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.loader.LoaderGen;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.IEnumContainer;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;

public class ArmorMat implements IEnumContainer{
	
	 /**
	  * a preconfigured versions of this armor enums gets cleared in post init
	  */
	 public static HashMap<ResourceLocation,ArmorMat> armorenums = new HashMap();
	
	 public ResourceLocation id;
	 public String enumName;//name of this in memory when it gets converted into an enum
	 public String textureName;
	 
     public int durability;
     public int[] damageReduction;
     public int enchantability;
     public SoundEvent soundEvent;
     public float toughness;
	
	 /**
	  * Point of this class is to create a material and then enumify it later
	 */
	 public ArmorMat(ResourceLocation id, ResourceLocation textureName, int durability, int[] damageReduction, int enclvl, SoundEvent soundEventIn, float tough)
     {
         this.id = id;
		 this.enumName = id.toString().replaceAll(":", "_");
         this.textureName = textureName.toString();
         
         this.durability = durability;
         this.damageReduction = damageReduction;
         this.enchantability = enclvl;
         this.soundEvent = soundEventIn;
         this.toughness = tough;
     }
	
	 public ArmorMat(ResourceLocation id, ResourceLocation texture, int durability, int[] damageReduction, int enchlvl, ResourceLocation soundEventIN, float tough)
     {
         this(id, texture, durability, damageReduction, enchlvl, MinecraftUtil.getSoundEvent(soundEventIN), tough);
     }
	 
	 @Override
	 public ArmorMaterial getEnum()
	 {
		return EnumHelper.addArmorMaterial(this.enumName, this.textureName,this.durability,this.damageReduction,this.enchantability,this.soundEvent,this.toughness);
	 }
	 
	 public static ArmorMaterial getMat(ArmorMat mat, boolean config)
	 {
		if(!config)
			return mat.getEnum();
		if(!armorenums.containsKey(mat.id))
		{
			ArmorMat.armorenums.put(mat.id, mat);
			return mat.getEnum();
		}
		return armorenums.get(mat.id).getEnum();
	 }
	 
	 public static void parseArmorMats() 
	 {
		File armor = new File(Config.cfg.getParent(),"auto/properties/armormats.json");
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
	 
	public static ArmorMat parseArmorMat(ResourceLocation loc, JSONObject json)
	{
		 String textureName = json.getString("textureName");
		 int durability = json.getInt("durability");
		 int[] damageReduction = json.getIntArray("damageReduction");
		 int enchantability = json.getInt("enchantability");
		 SoundEvent event = SoundEvent.REGISTRY.getObject(new ResourceLocation(json.getString("soundEvent")));
		 float tough = json.getFloat("toughness");
		 
		 return new ArmorMat(loc, new ResourceLocation(textureName), durability, damageReduction, enchantability, event, tough);
	}
	
	public static void saveToolMats()
	{
		if(ToolMat.toolenums.isEmpty())
			return;
		JSONObject armorJson = new JSONObject();
		File armor = new File(Config.cfg.getParent(),"auto/properties/armormats.json");
		for(Map.Entry<ResourceLocation,ArmorMat> pair : ArmorMat.armorenums.entrySet())
		{
			armorJson.put(pair.getKey(), saveArmorJSON(pair.getValue()));
		}
		try 
		{
			JavaUtil.saveJSONSafley(armorJson, armor);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static JSONObject saveArmorJSON(ArmorMat mat) 
	{
		JSONObject json = new JSONObject();
		json.put("textureName", mat.textureName);
		json.put("durability", mat.durability);
		json.putStaticArray("damageReduction", mat.damageReduction);
		json.put("enchantability", mat.enchantability);
		json.put("soundEvent", SoundEvent.REGISTRY.getNameForObject(mat.soundEvent));
		json.put("toughness", mat.toughness);
		return json;
	}
	 
	 @Override
	 public String toString()
	 {
		 return "\"" + this.enumName + "\" = [\"" + this.textureName + "\", " + this.durability + ", [" + JavaUtil.getIntsAsString(this.damageReduction) + "], " + this.enchantability + ", \"" + this.soundEvent.getRegistryName() + "\", " + this.toughness + "f]";
	 }

}
