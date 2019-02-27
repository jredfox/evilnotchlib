package com.evilnotch.lib.minecraft.basicmc.item.armor;

import java.util.HashMap;
import java.util.List;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.loader.LoaderFields;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.simple.IEnumContainer;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;

public class ArmorMat implements IEnumContainer{
	
	 /**
	  * A registry for custom armor material
	  */
	 public static HashMap<ResourceLocation,ArmorMaterial> armorenums = new HashMap();
	
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
         if(!armorenums.containsKey(id))
        	 armorenums.put(this.id, this.getEnum());
     }
	
	 public ArmorMat(ResourceLocation id, ResourceLocation texture, int durability, int[] damageReduction, int enchlvl, ResourceLocation soundEventIN, float tough)
     {
         this(id, texture, durability, damageReduction, enchlvl, MinecraftUtil.getSoundEvent(soundEventIN), tough);
     }
	 
	 @Override
	 public ArmorMaterial getEnum()
	 {
		return armorenums.get(this.id);
	 }
	 
	 @Override
	 public String toString(){
		 return "\"" + this.enumName + "\" = [\"" + this.textureName + "\", " + this.durability + ", [" + JavaUtil.getIntsAsString(this.damageReduction) + "], " + this.enchantability + ", \"" + this.soundEvent.getRegistryName() + "\", " + this.toughness + "f]";
	 }

}
