package com.evilnotch.lib.minecraft.basicmc.item.tool;

import java.util.HashMap;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.loader.LoaderGen;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.simple.IEnumContainer;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
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
			LoaderGen.addMat(mat);
		}
		System.out.println(mat.id);
		return toolenums.get(mat.id).getEnum();
	}

}
