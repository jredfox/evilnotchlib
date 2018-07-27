package com.EvilNotch.lib.minecraft.content.items;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.content.ArmorMat;
import com.EvilNotch.lib.minecraft.content.LangEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BasicArmor extends ItemArmor implements IBasicItem{
	PotionEffect effect = null;
	public boolean hasregister = false;
	public boolean hasmodel = false;
	public boolean haslang = false;
	public boolean hasconfig = false;
	
	/**
	 * the booleans are used for later calls in case people want to call create objects before preinit
	 */
	public BasicArmor(ArmorMat materialIn,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,LangEntry... langlist) {
		this(materialIn,id, renderIndexIn, equipmentSlotIn,(CreativeTabs)null,langlist);
	}
	public BasicArmor(ArmorMat mat,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot slot,CreativeTabs tab,LangEntry... langlist){
		this(mat,id, renderIndexIn, slot,tab,null,true,true,true,true,langlist);
	}
	public BasicArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect potion, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,potion,(CreativeTabs)null,langList);
	}
	public BasicArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect potion,CreativeTabs tab, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,tab,potion,true,true,true,true,langList);
	}

	public BasicArmor(ArmorMat materialIn,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,CreativeTabs tab,
			PotionEffect potion,boolean model,boolean register,boolean lang, boolean config,LangEntry... langlist) {
		super(BasicItem.getMat(materialIn,config), renderIndexIn, equipmentSlotIn);
		this.effect = potion;
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		//autofill
		this.populateLang(langlist, unlocalname,id);
		
		this.hasregister = register;
		this.hasmodel = model;
		this.haslang = lang;
		this.hasconfig = config;
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
	public void onArmorTick(final World world, final EntityPlayer player, final ItemStack itemStack) {
		super.onArmorTick(world, player, itemStack);
		if(this.effect == null)
			return;
		ItemStack boots = player.inventory.armorInventory.get(0);
		ItemStack pants = player.inventory.armorInventory.get(1);
		ItemStack chest = player.inventory.armorInventory.get(2);
		ItemStack head = player.inventory.armorInventory.get(3);
		if(boots == null || pants == null || chest == null || head == null)
			return;
		if(boots.isEmpty() || pants.isEmpty()|| chest.isEmpty() || head.isEmpty())
			return;
		if (!player.isPotionActive(this.effect.getPotion())) { // If the Potion isn't currently active,
			player.addPotionEffect(new PotionEffect(this.effect)); // Apply a copy of the PotionEffect to the player
		}
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


}
