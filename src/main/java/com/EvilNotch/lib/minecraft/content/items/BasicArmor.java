package com.EvilNotch.lib.minecraft.content.items;

import java.util.ArrayList;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.content.ArmorMat;
import com.EvilNotch.lib.minecraft.content.ArmorSet;
import com.EvilNotch.lib.minecraft.content.LangEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BasicArmor extends ItemArmor implements IBasicItem,IBasicArmor{
	/**
	 * a list of option effects to be applied to the player on tick
	 */
	PotionEffect[] effects = null;
	public boolean hasregister = false;
	public boolean hasmodel = false;
	public boolean haslang = false;
	public boolean hasconfig = false;
	public ArmorSet armorset = null;
	
	/**
	 * the booleans are used for later calls in case people want to call create objects before preinit
	 */
	public BasicArmor(ArmorMat materialIn,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,LangEntry... langlist) {
		this(materialIn,id, renderIndexIn, equipmentSlotIn,(CreativeTabs)null,langlist);
	}
	public BasicArmor(ArmorMat mat,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot slot,CreativeTabs tab,LangEntry... langlist){
		this(mat,id, renderIndexIn, slot,tab,null,true,true,true,true,langlist);
	}
	public BasicArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect[] potion, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,potion,(CreativeTabs)null,langList);
	}
	public BasicArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect[] potion,CreativeTabs tab, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,tab,potion,true,true,true,true,langList);
	}
	/**
	 * legacy support and also if you only wanted one potion effect applied
	 */
	public BasicArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect potion, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,new PotionEffect[]{potion},(CreativeTabs)null,langList);
	}
	public BasicArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect potion,CreativeTabs tab, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,tab,new PotionEffect[]{potion},true,true,true,true,langList);
	}

	public BasicArmor(ArmorMat materialIn,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,CreativeTabs tab,
			PotionEffect[] potions,boolean model,boolean register,boolean lang, boolean config,LangEntry... langlist) {
		super(BasicItem.getMat(materialIn,config), renderIndexIn, equipmentSlotIn);
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		if(potions != null && potions[0] != null)
			this.effects = potions;//don't set the potion list if it's null array or contains null
		
		this.hasregister = register;
		this.hasmodel = model;
		this.haslang = lang;
		this.hasconfig = config;
		
		//autofill
		this.populateLang(langlist, unlocalname,id);
		
		MainJava.items.add(this);
	}
	public void populateLang(LangEntry[] langlist,String unlocalname,ResourceLocation id) {
		if(!this.useLangRegistry())
			return;
		for(LangEntry entry : langlist)
		{
			entry.langId = "item." + unlocalname + ".name";
			entry.loc = id;
			BasicItem.itemlangs.add(entry);
		}
	}
	@Override
	public void setArmorSet(ArmorSet set){
		this.armorset = set;
	}
	@Override
	public ArmorSet getArmorSet(){
		return this.armorset;
	}
	
	@Override
	public void onArmorTick(final World world, final EntityPlayer player, final ItemStack itemStack) 
	{
		super.onArmorTick(world, player, itemStack);
		if(this.effects == null || this.armorset  == null)
			return;
		ItemStack head = player.inventory.armorInventory.get(3);
		//return from ticking if helmet isn't on since I only need one thing happening per tick
		if(head.getItem() != this)
			return;
		ItemStack boots = player.inventory.armorInventory.get(0);
		ItemStack pants = player.inventory.armorInventory.get(1);
		ItemStack chest = player.inventory.armorInventory.get(2);

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
	@Override
	public boolean hasFullArmorSet(ItemStack boots, ItemStack pants, ItemStack chest, ItemStack head) 
	{
		return this.armorset.boots.getItem() == boots.getItem() && this.armorset.leggings.getItem() == pants.getItem() && this.armorset.chestplate.getItem() == chest.getItem() && this.armorset.helmet.getItem() == head.getItem();
	}
	@Override
	public boolean register() {
		return this.hasregister;
	}
	@Override
	public boolean registerModel() {
		return this.hasmodel;
	}
	@Override
	public boolean useLangRegistry() {
		return this.haslang;
	}
	@Override
	public boolean useConfigPropterties() {
		return this.hasconfig;
	}
	@Override
	public PotionEffect[] getPotionEffects() {
		return this.effects;
	}
	@Override
	public boolean containsPotionEffect(PotionEffect potion) {
		for(PotionEffect p : this.getPotionEffects())
			if(p.getPotion() == potion.getPotion())
				return true;
		return false;
	}

}
