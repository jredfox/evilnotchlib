package com.evilnotch.lib.minecraft.basicmc.item.armor;

import java.util.HashMap;

import org.json.simple.JSONObject;

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
			LoaderGen.addMat(mat);
		}
		return armorenums.get(mat.id).getEnum();
	 }
	 
	 @Override
	 public String toString()
	 {
		 return "\"" + this.enumName + "\" = [\"" + this.textureName + "\", " + this.durability + ", [" + JavaUtil.getIntsAsString(this.damageReduction) + "], " + this.enchantability + ", \"" + this.soundEvent.getRegistryName() + "\", " + this.toughness + "f]";
	 }

}
