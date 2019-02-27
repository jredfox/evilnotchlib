package com.evilnotch.lib.minecraft.basicmc.item.tool;

import java.util.HashMap;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.simple.IEnumContainer;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

public class ToolMat implements IEnumContainer{
	
	public static final ToolMaterial blankToolMat = EnumHelper.addToolMaterial(new ResourceLocation(MainJava.MODID,"blanktool").toString(),0,0,0,0,0);
	
	 /**
	  * A hashmap between enum string name and tool material ENUM NAMES MUST BE UNIQUE
	  * Recommended to use modid but, replace all ":" with "_"
	  */
	public static HashMap<String,ToolMaterial> toolenums = new HashMap();
	 /**
	  * Cache for retrieving pre-configured ToolMat
	  */
	public static HashMap<String,ToolMat> toolmats = new HashMap();
	
	 /** The level of material this tool can harvest (3 = DIAMOND, 2 = IRON, 1 = STONE, 0 = WOOD/GOLD) */
    public int harvestLevel;
    /** The number of uses this material allows. (wood = 59, stone = 131, iron = 250, diamond = 1561, gold = 32) */
    public int maxUses;
    /** The strength of this tool material against blocks which it is effective against. */
    public float efficiency;
    /** Damage versus entities. */
    public float attackDamage;
    /** Defines the natural enchantability factor of the material. */
    public int enchantability;
    //Added by forge for custom Tool materials.
    public ItemStack repairMaterial = ItemStack.EMPTY;
    public String enumName;

    public ToolMat(ResourceLocation enumName,int harvestLevel, int maxUses, float efficiency, float damageVsEntity, int enchantability)
    {
        this.enumName =  enumName.toString().replaceAll(":", "_");
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = damageVsEntity;
        this.enchantability = enchantability;
    }
    
    public ToolMat(LineArray line) {
		this.enumName = line.getId();
		this.harvestLevel = line.getInt(0);
		this.maxUses =  line.getInt(1);
		this.efficiency = line.getFloat(2);
		this.attackDamage = line.getFloat(3);
		this.enchantability = line.getInt(4);
	}

	@Override
    public ToolMaterial getEnum()
	{
		if(!toolenums.containsKey(this.enumName))
		{
			ToolMaterial mat = EnumHelper.addToolMaterial(this.enumName, this.harvestLevel, this.maxUses, this.efficiency, this.attackDamage, this.enchantability);
			toolenums.put(this.enumName, mat);
			return mat;
		}
		return toolenums.get(this.enumName);
    }
	
    @Override
    public String toString(){
    	return "\"" + this.enumName + "\" = [" + this.harvestLevel + "," + this.maxUses + "," + this.efficiency + "f," + this.attackDamage + "f," + this.enchantability + "]";
    }

}
