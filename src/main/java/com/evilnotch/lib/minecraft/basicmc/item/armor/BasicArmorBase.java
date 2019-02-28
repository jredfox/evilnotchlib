package com.evilnotch.lib.minecraft.basicmc.item.armor;

import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.minecraft.basicmc.auto.IAutoItem;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JSONProxy;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BasicArmorBase extends ItemArmor implements IBasicArmor, IAutoItem{
	
	public ArmorSet armorset = null;

	/**
	 * the booleans are used for later calls in case people want to call create objects before preinit
	 */
	public BasicArmorBase(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot,LangEntry... langlist) 
	{
		this(id, mat, renderIndexIn, slot, (CreativeTabs)null, langlist);
	}
	
	public BasicArmorBase(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot,CreativeTabs tab,LangEntry... langlist)
	{
		this(id, mat, renderIndexIn, slot, tab, true, langlist);
	}
	
	public BasicArmorBase(ResourceLocation id, ArmorMat mat, int renderIndexIn, EntityEquipmentSlot slot, CreativeTabs tab, boolean config, LangEntry... langlist) 
	{
		super(ArmorMat.getMat(mat, config), renderIndexIn, slot);
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		//autofill
		this.register();
		this.populateLang(langlist);
		this.populateJSON();
	}
	
	@Override
	public boolean hasFullArmorSet(ItemStack boots, ItemStack pants, ItemStack chest, ItemStack head) 
	{
		return this.armorset.boots.getItem() == boots.getItem() && this.armorset.leggings.getItem() == pants.getItem() && this.armorset.chestplate.getItem() == chest.getItem() && this.armorset.helmet.getItem() == head.getItem();
	}
	
	@Override
	public void setArmorSet(ArmorSet set)
	{
		this.armorset = set;
	}
	
	@Override
	public ArmorSet getArmorSet()
	{
		return this.armorset;
	}
	
	public void register()
	{
		if(this.canRegister())
			LoaderItems.items.add(this);
	}
	
	public void populateLang(LangEntry... langs)
	{
		if(this.canRegisterLang())
			LangRegistry.registerLang(this, langs);
	}
	
	public void populateJSON()
	{
		if(this.canRegisterJSON())
			JSONProxy.registerItemJson(this);
	}
	
	@Override
	public boolean canRegister() 
	{
		return true;
	}
	
	@Override
	public boolean canRegisterLang()
	{
		return true;
	}
	
	@Override
	public boolean canRegisterJSON() 
	{
		return true;
	}

}
