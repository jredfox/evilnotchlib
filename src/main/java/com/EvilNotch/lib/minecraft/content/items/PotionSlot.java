package com.EvilNotch.lib.minecraft.content.items;

import net.minecraft.potion.PotionEffect;

public class PotionSlot
{
	public PotionEffect[] pb = null;
	public PotionEffect[] pl = null;
	public PotionEffect[] pc = null;
	public PotionEffect[] ph = null;
	public PotionEffect[] pf = null;
	
	public PotionSlot(PotionEffect[] potion_boots,PotionEffect[] potion_leggings,PotionEffect[] potion_chestplate,PotionEffect[] potion_helmet,PotionEffect[] potion_full)
	{
		this.pb = potion_boots;
		this.pl = potion_leggings;
		this.pc = potion_chestplate;
		this.ph = potion_helmet;
		this.pf = potion_full;
	}
}
