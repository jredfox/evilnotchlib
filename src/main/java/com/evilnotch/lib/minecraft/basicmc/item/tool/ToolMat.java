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
	
	/**
	 * a registry for all tool material used by this lib
	 */
	public static HashMap<ResourceLocation,ToolMaterial> toolenums = new HashMap();
	
    public int harvestLevel;
    public int maxUses;
    public float efficiency;
    public float attackDamage;
    public int enchantability;
    
    public ResourceLocation id;
    public String enumName;

    public ToolMat(ResourceLocation id,int harvestLevel, int maxUses, float efficiency, float damageVsEntity, int enchantability)
    {
        this.id = id;
        this.enumName =  id.toString().replaceAll(":", "_");
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = damageVsEntity;
        this.enchantability = enchantability;
        if(!toolenums.containsKey(id))
        	toolenums.put(this.id, this.getEnum());
    }

	@Override
    public ToolMaterial getEnum()
	{
		return toolenums.get(this.id);
    }
	
    @Override
    public String toString()
    {
    	return "\"" + this.enumName + "\" = [" + this.harvestLevel + ", " + this.maxUses + ", " + this.efficiency + "f, " + this.attackDamage + "f, " + this.enchantability + "]";
    }

}
