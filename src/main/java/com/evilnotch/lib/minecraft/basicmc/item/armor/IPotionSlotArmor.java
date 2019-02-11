package com.evilnotch.lib.minecraft.basicmc.item.armor;

import net.minecraft.potion.PotionEffect;

public interface IPotionSlotArmor {
	
	public PotionEffect[] getPotionEffects(ArmorSlotType slot);
	
	default public boolean hasPotionEffects(ArmorSlotType slot){
		return getPotionEffects(slot) != null;
	}
	
	/**
	 * unlike vanilla you have the ability for full meaning all armor is on
	 * @author jredfox
	 */
	public enum ArmorSlotType{
		BOOTS(),
		LEGS(),
		CHEST(),
		HELMET(),
		FULL();
	}

}
