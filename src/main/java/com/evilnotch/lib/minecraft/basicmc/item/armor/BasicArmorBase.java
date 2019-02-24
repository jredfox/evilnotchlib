package com.evilnotch.lib.minecraft.basicmc.item.armor;

import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BasicArmorBase extends ItemArmor implements IBasicArmor{
	
	public boolean hasregister = false;
	public boolean hasmodel = false;
	public boolean haslang = false;
	public boolean hasconfig = false;
	public ArmorSet armorset = null;

	/**
	 * the booleans are used for later calls in case people want to call create objects before preinit
	 */
	public BasicArmorBase(ArmorMat mat,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot slot,LangEntry... langlist) {
		this(mat,id, renderIndexIn, slot,(CreativeTabs)null,langlist);
	}
	public BasicArmorBase(ArmorMat mat,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot slot,CreativeTabs tab,LangEntry... langlist){
		this(mat,id, renderIndexIn, slot,tab,true,true,true,true,langlist);
	}
	
	public BasicArmorBase(ArmorMat materialIn,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,CreativeTabs tab,boolean model,boolean register,boolean lang, boolean config,LangEntry... langlist) 
	{
		super(BasicItem.getMat(materialIn,config), renderIndexIn, equipmentSlotIn);
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		this.hasregister = register;
		this.hasmodel = model;
		this.haslang = lang;
		this.hasconfig = config;
		
		//autofill
		this.populateLang(id, langlist);
		
		LoaderItems.items.add(this);
	}
	
	public void populateLang(ResourceLocation id, LangEntry... langs) 
	{
		if(!this.useLangRegistry())
			return;
		LangRegistry.registerLang(this, id, langs);
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
	public void setArmorSet(ArmorSet set){
		this.armorset = set;
	}
	@Override
	public ArmorSet getArmorSet(){
		return this.armorset;
	}

}
