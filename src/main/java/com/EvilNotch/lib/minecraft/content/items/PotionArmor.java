package com.evilnotch.lib.minecraft.content.items;

import com.evilnotch.lib.minecraft.content.ArmorMat;
import com.evilnotch.lib.minecraft.content.LangEntry;

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
		this(mat,id, renderIndexIn, slot,tab,new PotionEffect[0],true,true,true,true,langlist);
	}
	public PotionArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect[] potion, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,potion,(CreativeTabs)null,langList);
	}
	public PotionArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect[] potion,CreativeTabs tab, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,tab,potion,true,true,true,true,langList);
	}
	
	/**
	 * legacy support and also if you only wanted one potion effect applied
	 */
	public PotionArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect potion, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,new PotionEffect[]{potion},(CreativeTabs)null,langList);
	}
	/**
	 * legacy support and also if you only wanted one potion effect applied
	 */
	public PotionArmor(ArmorMat mat, ResourceLocation id, int renderIndexIn,EntityEquipmentSlot slot, PotionEffect potion,CreativeTabs tab, LangEntry...langList) 
	{
		this(mat,id,renderIndexIn,slot,tab,new PotionEffect[]{potion},true,true,true,true,langList);
	}

	public PotionArmor(ArmorMat materialIn,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,CreativeTabs tab,
			PotionEffect[] potions,boolean model,boolean register,boolean lang, boolean config,LangEntry... langlist) {
		super(materialIn,id, renderIndexIn, equipmentSlotIn,tab,potions,model,register,lang, config,langlist);
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
		IPotionArmor p = (IPotionArmor)pants.getItem();
		IPotionArmor c = (IPotionArmor) chest.getItem();
		IPotionArmor h = (IPotionArmor) head.getItem();
		
		if(!b.hasPotionEffects() || !p.hasPotionEffects() || !c.hasPotionEffects() || !h.hasPotionEffects())
			return super.hasFullArmorSet(boots, pants, chest, head);
		
		return hasPotionEffects(b,p,c,h);
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
