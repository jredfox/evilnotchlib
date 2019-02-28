package com.evilnotch.lib.minecraft.basicmc.item.armor;

import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BasicArmor extends BasicArmorBase implements IPotionArmor{
	/**
	 * a list of option effects to be applied to the player on tick
	 */
	public PotionEffect[] effects = null;
	
	public BasicArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, LangEntry... langlist) 
	{
		this(id, mat, renderIndexIn, slot, (CreativeTabs)null, langlist);
	}
	
	public BasicArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, CreativeTabs tab, LangEntry... langlist) 
	{
		this(id, mat, renderIndexIn, slot, (PotionEffect[])null, tab, langlist);
	}
	
	public BasicArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect[] effects, LangEntry... langlist) 
	{
		this(id, mat, renderIndexIn, slot, effects, (CreativeTabs)null, langlist);
	}
	
	public BasicArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect[] effects, CreativeTabs tab, LangEntry... langlist)
	{
		this(id, mat, renderIndexIn, slot, effects, tab, true, langlist);
	}
	
	public BasicArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect effect, LangEntry... langlist) 
	{
		this(id, mat, renderIndexIn, slot, new PotionEffect[]{effect}, (CreativeTabs)null, langlist);
	}
	
	public BasicArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect effect, CreativeTabs tab, LangEntry... langlist)
	{
		this(id, mat, renderIndexIn, slot, new PotionEffect[]{effect}, tab, true, langlist);
	}
	
	public BasicArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect[] effects, CreativeTabs tab, boolean config, LangEntry... langlist) 
	{
		super(id, mat, renderIndexIn, slot, tab, config, langlist);
		if(effects != null)
			this.effects = effects;//don't set the potion list if it's null array or contains null
	}

	
	@Override
	public PotionEffect[] getPotionEffects() 
	{
		return this.effects;
	}
	
	@Override
	public boolean containsPotionEffect(PotionEffect potion) 
	{
		if(!this.hasPotionEffects())
			return potion == null;
		
		for(PotionEffect p : this.getPotionEffects())
			if(p.getPotion() == potion.getPotion())
				return true;
		
		return false;
	}
	
	@Override
	public void onArmorTick(final World world, final EntityPlayer player, final ItemStack itemStack) 
	{
		super.onArmorTick(world, player, itemStack);
		
		if(this.effects == null)
			return;
		
		ItemStack boots = player.inventory.armorInventory.get(0);
		ItemStack pants = player.inventory.armorInventory.get(1);
		ItemStack chest = player.inventory.armorInventory.get(2);
		ItemStack head = player.inventory.armorInventory.get(3);

		if(!this.hasFullArmorSet(boots, pants, chest, head))
			return;
		
		for(PotionEffect p : this.effects)
		{
			if (!player.isPotionActive(p.getPotion())) 
			{
				player.addPotionEffect(p); // Apply a copy of the PotionEffect to the player
			}
		}
	}

}
