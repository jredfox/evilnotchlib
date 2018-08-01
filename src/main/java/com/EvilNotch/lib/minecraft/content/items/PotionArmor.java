package com.EvilNotch.lib.minecraft.content.items;

import com.EvilNotch.lib.minecraft.content.ArmorMat;
import com.EvilNotch.lib.minecraft.content.LangEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

/**
 * A Armor Class for Potions Made to sync with other Armor Potion Effects if they are the same type
 * @author jredfox
 */
public class PotionArmor extends BasicArmor{

	/**
	 * the booleans are used for later calls in case people want to call create objects before preinit
	 */
	public PotionArmor(ArmorMat materialIn,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,LangEntry... langlist) {
		this(materialIn,id, renderIndexIn, equipmentSlotIn,(CreativeTabs)null,langlist);
	}
	public PotionArmor(ArmorMat mat,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot slot,CreativeTabs tab,LangEntry... langlist){
		this(mat,id, renderIndexIn, slot,tab,null,true,true,true,true,langlist);
	}
	public PotionArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect potion, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,potion,(CreativeTabs)null,langList);
	}
	public PotionArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect potion,CreativeTabs tab, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,tab,potion,true,true,true,true,langList);
	}

	public PotionArmor(ArmorMat materialIn,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,CreativeTabs tab,
			PotionEffect potion,boolean model,boolean register,boolean lang, boolean config,LangEntry... langlist) {
		
		super(materialIn,id, renderIndexIn, equipmentSlotIn,tab,potion,model,register,lang, config,langlist);
	}

	/**
	 * used to see if this armor set is full
	 * potion effects check this to apply the effect so for Potion armor if same type or is full set return true
	 */
	@Override
	public boolean hasFullArmorSet(ItemStack boots, ItemStack pants, ItemStack chest, ItemStack head) 
	{
		if(!(boots.getItem() instanceof IBasicArmor) || !(pants.getItem() instanceof IBasicArmor)|| !(chest.getItem() instanceof IBasicArmor) || !(head.getItem() instanceof IBasicArmor))
			return false;
		
		IBasicArmor b = (IBasicArmor) boots.getItem();
		IBasicArmor p = (IBasicArmor)pants.getItem();
		IBasicArmor c = (IBasicArmor) chest.getItem();
		IBasicArmor h = (IBasicArmor) head.getItem();
		
		if(!b.hasPotionEffect() || !p.hasPotionEffect() || !c.hasPotionEffect() || !h.hasPotionEffect())
			return super.hasFullArmorSet(boots, pants, chest, head);
		
		Potion pot = this.getPotionEffect().getPotion();
		return b.getPotionEffect().getPotion() == pot && p.getPotionEffect().getPotion() == pot && c.getPotionEffect().getPotion() == pot && h.getPotionEffect().getPotion() == pot;
	}
}
