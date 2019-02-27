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
	
	public static final ArmorMaterial blankArmorMat = EnumHelper.addArmorMaterial(new ResourceLocation(MainJava.MODID,"blanktool").toString(), "", 0, new int[]{0,0,0,0}, 0, null, 0.0f);
	
	 /**
	  * A hashmap between enum string name and armor material ENUM NAMES MUST BE UNIQUE
	  * Recomended to use modid but, replace all ":" with "_"
	  */
	 public static HashMap<String,ArmorMaterial> armorenums = new HashMap();
	 /**
	  * Cache for retrieving pre-configured ArmorMat
	  * Don't add them yourselves it's auto added by calling tool constructors
	  */
	 public static HashMap<String,ArmorMat> armormats = new HashMap();
	
	 public String textureName;
     /**
      * how many hit points before armor breaks
      */
     public int durability;
     /**
      * Holds the damage reduction (each 1 points is half a shield on gui) of each piece of armor (helmet, plate,
      * legs and boots)
      */
     public int[] damageReductionAmountArray;
     /** Return the enchantability factor of the material */
     public int enchantability;
     public SoundEvent soundEvent;
     public float toughness;
     //Added by forge for custom Armor materials.
     public ItemStack repairMaterial = ItemStack.EMPTY;
	 public String enumName;
	
	/**
	 * Point of this class is to create a material and then enumify it later
	*/
	public ArmorMat(ResourceLocation enumName,ResourceLocation textureName, int durability, int[] damageReductionAmountArrayIn, int enchantabilityIn, SoundEvent soundEventIn, float toughnessIn)
    {
		 this.enumName = enumName.toString().replaceAll(":", "_");
         this.textureName = textureName.toString();
         this.durability = durability;
         this.damageReductionAmountArray = damageReductionAmountArrayIn;
         this.enchantability = enchantabilityIn;
         this.soundEvent = soundEventIn;
         this.toughness = toughnessIn;
    }
	public ArmorMat(ResourceLocation enumName,ResourceLocation nameIn, int maxDamageFactorIn, int[] damageReductionAmountArrayIn, int enchantabilityIn, ResourceLocation soundEventIN, float toughnessIn)
    {
         this(enumName,nameIn,maxDamageFactorIn,damageReductionAmountArrayIn, enchantabilityIn, MinecraftUtil.getSoundEvent(soundEventIN), toughnessIn);
    }
	public ArmorMat(LineArray line)
	{
		this.enumName = line.getId();
		this.textureName = (String) line.heads.get(0);
		this.durability = line.getInt(1);
		 
		int[] damageReduce = new int[4];
		List<Object> arr = line.getHeadList(2);
		damageReduce[0] = line.getInt(arr, 0);
		damageReduce[1] = line.getInt(arr, 1);
		damageReduce[2] = line.getInt(arr, 2);
		damageReduce[3] = line.getInt(arr, 3);
		this.damageReductionAmountArray = damageReduce;
		
		this.enchantability = line.getInt(3);
		this.soundEvent = MinecraftUtil.getSoundEvent(new ResourceLocation((String) line.heads.get(4)));
		this.toughness = line.getFloat(5);
	}
	 
	@Override
	public ArmorMaterial getEnum()
	{
		if(!armorenums.containsKey(this.enumName))
		{
			ArmorMaterial mat = EnumHelper.addArmorMaterial(this.enumName, this.textureName,this.durability,this.damageReductionAmountArray,this.enchantability,this.soundEvent,this.toughness);
			armorenums.put(this.enumName, mat);
			return mat;
		}
		return armorenums.get(this.enumName);
	 }
	 
	 @Override
	 public String toString(){
		 return "\"" + this.enumName + "\" = [\"" + this.textureName + "\"," + this.durability + ",[" + JavaUtil.getIntsAsString(this.damageReductionAmountArray) + "]," + this.enchantability + ",\"" + this.soundEvent.getRegistryName() + "\"," + this.toughness + "f]";
	 }

}
