package com.evilnotch.lib.minecraft.basicmc.item.armor;

import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

/**
 * A Armor Class for Potions Made to sync with other Armor Potion Effects if they are the same type
 * @author jredfox
 */
public class BasicPotionArmor extends BasicArmor{
	
	/**
	 * a list of option effects to be applied to the player on tick
	 */
	public PotionEffect[] effects = null;
	
	public BasicPotionArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect[] effects, LangEntry... langlist) 
	{
		this(id, mat, renderIndexIn, slot, effects, (CreativeTabs)null, langlist);
	}
	
	public BasicPotionArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect[] effects, CreativeTabs tab, LangEntry... langlist)
	{
		this(id, mat, renderIndexIn, slot, effects, tab, true, langlist);
	}
	
	public BasicPotionArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect effect, LangEntry... langlist) 
	{
		this(id, mat, renderIndexIn, slot, new PotionEffect[]{effect}, (CreativeTabs)null, langlist);
	}
	
	public BasicPotionArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect effect, CreativeTabs tab, LangEntry... langlist)
	{
		this(id, mat, renderIndexIn, slot, new PotionEffect[]{effect}, tab, true, langlist);
	}
	
	public BasicPotionArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionEffect[] effects, CreativeTabs tab, boolean config, LangEntry... langlist) 
	{
		super(id, mat, renderIndexIn, slot, effects, tab, config, langlist);
	}

	/**
	 * used to see if this armor set is full
	 * potion effects check this to apply the effect so for Potion armor if same type or is full set return true
	 */
	@Override
	public boolean hasFullArmorSet(ItemStack boots, ItemStack pants, ItemStack chest, ItemStack head) 
	{
		if(!(boots.getItem() instanceof IPotionArmor) || !(pants.getItem() instanceof IPotionArmor)|| !(chest.getItem() instanceof IPotionArmor) || !(head.getItem() instanceof IPotionArmor))
			return false;
		
		IPotionArmor b = (IPotionArmor) boots.getItem();
		IPotionArmor p = (IPotionArmor) pants.getItem();
		IPotionArmor c = (IPotionArmor) chest.getItem();
		IPotionArmor h = (IPotionArmor) head.getItem();
		
		if(!b.hasPotionEffects() || !p.hasPotionEffects() || !c.hasPotionEffects() || !h.hasPotionEffects())
			return super.hasFullArmorSet(boots, pants, chest, head);
		
		return hasPotionEffects(b, p, c, h);
	}
	
	/**
	 * compares if all potions from this is in objects boots,pants,chest,helmet
	 */
	public boolean hasPotionEffects(IPotionArmor b, IPotionArmor p, IPotionArmor c, IPotionArmor h) 
	{
		for(PotionEffect potion : this.effects)
		{
			if(!b.containsPotionEffect(potion) || !p.containsPotionEffect(potion) || !c.containsPotionEffect(potion) || !h.containsPotionEffect(potion))
				return false;
		}
		return true;
	}
}
