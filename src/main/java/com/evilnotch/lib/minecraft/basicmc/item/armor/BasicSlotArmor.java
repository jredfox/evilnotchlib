package com.evilnotch.lib.minecraft.basicmc.item.armor;

import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BasicSlotArmor extends BasicArmorBase implements IPotionSlotArmor{
	
	/**
	 * this and potions arrays inside can be null put not potion effects in the array
	 */
	public PotionSlot slot;
	
	public BasicSlotArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionSlot effects, LangEntry... langlist) 
	{
		this(id, mat, renderIndexIn, slot, effects, (CreativeTabs)null, langlist);
	}
	
	public BasicSlotArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionSlot effects, CreativeTabs tab, LangEntry... langlist)
	{
		this(id, mat, renderIndexIn, slot, effects, tab, true, langlist);
	}
	
	public BasicSlotArmor(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, PotionSlot effects, CreativeTabs tab, boolean config, LangEntry... langlist) 
	{
		super(id, mat, renderIndexIn, slot, tab, config, langlist);
		this.slot = effects;
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) 
	{
		super.onArmorTick(world, player, stack);
		
		if(this.slot == null)
			return;
		
		if(this.armorset.boots.getItem() == this)
		{
			this.tickBoots(world,player,stack);
		}
		if(this.armorset.leggings.getItem() == this)
		{
			this.tickLegs(world,player,stack);
		}
		if(this.armorset.chestplate.getItem() == this)
		{
			this.tickChest(world,player,stack);
		}
		if(this.armorset.helmet.getItem() == this)
		{
			this.tickHelmet(world,player,stack);
		}
		else
		{
			return;//return if it's not the helmet ticking no need to check below code more then once
		}
		
		ItemStack boots = player.inventory.armorInventory.get(0);
		ItemStack pants = player.inventory.armorInventory.get(1);
		ItemStack chest = player.inventory.armorInventory.get(2);
		ItemStack head = player.inventory.armorInventory.get(3);
		
		if(this.hasFullArmorSet(boots, pants, chest, head))
		{
			this.tickFull(world,player,stack);
		}
	}
	
	/**
	 * called when boots are on
	 */
	public void tickBoots(World world, EntityPlayer player, ItemStack stack)
	{
		if(this.slot.pb != null)
		{
			for(PotionEffect p : this.slot.pb)
				if(!player.isPotionActive(p.getPotion()))
					player.addPotionEffect(p);
		}
	}
	
	/**
	 * called when leggings are on
	 */
	public void tickLegs(World world, EntityPlayer player, ItemStack stack)
	{
		if(this.slot.pl != null)
		{
			for(PotionEffect p : this.slot.pl)
				if(!player.isPotionActive(p.getPotion()))
					player.addPotionEffect(p);
		}
	}
	
	/**
	 * called when chest is on
	 */
	public void tickChest(World world, EntityPlayer player, ItemStack stack)
	{
		if(this.slot.pc != null)
		{
			for(PotionEffect p : this.slot.pc)
				if(!player.isPotionActive(p.getPotion()))
					player.addPotionEffect(p);
		}
	}
	
	/**
	 * called when helmet is on
	 */
	public void tickHelmet(World world, EntityPlayer player, ItemStack stack)
	{
		if(this.slot.ph != null)
		{
			for(PotionEffect p : this.slot.ph)
				if(!player.isPotionActive(p.getPotion()))
					player.addPotionEffect(p);
		}
	}
	
	/**
	 * called when everything is on will fire all other methods first though
	 */
	public void tickFull(World world, EntityPlayer player, ItemStack stack)
	{
		if(this.slot.pf != null)
		{
			for(PotionEffect p : this.slot.pf)
				if(!player.isPotionActive(p.getPotion()))
					player.addPotionEffect(p);
		}
	}
	
	@Override
	public PotionEffect[] getPotionEffects(ArmorSlotType slot) 
	{
		if(slot == ArmorSlotType.BOOTS)
			return this.slot.pb;
		else if(slot == ArmorSlotType.LEGS)
			return this.slot.pl;
		else if(slot == ArmorSlotType.CHEST)
			return this.slot.pc;
		else if(slot == ArmorSlotType.HELMET)
			return this.slot.ph;
		else if(slot == ArmorSlotType.FULL)
			return this.slot.pf;
		return null;
	}

}
