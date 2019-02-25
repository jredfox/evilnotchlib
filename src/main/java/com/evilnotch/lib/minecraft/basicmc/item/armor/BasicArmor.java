package com.evilnotch.lib.minecraft.basicmc.item.armor;

import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BasicArmor extends BasicArmorBase implements IPotionArmor<ItemArmor>{
	/**
	 * a list of option effects to be applied to the player on tick
	 */
	public PotionEffect[] effects = null;
	
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

	public BasicArmor(ArmorMat mat,ResourceLocation id, int renderIndexIn, EntityEquipmentSlot slot,CreativeTabs tab,
			PotionEffect[] potions,boolean model,boolean register,boolean lang, boolean config,LangEntry... langlist) {
		super(mat,id,renderIndexIn,slot,tab,model,register,lang,config,langlist);
		
		if(potions != null && potions[0] != null)
			this.effects = potions;//don't set the potion list if it's null array or contains null
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
	
	@Override
	public void onArmorTick(final World world, final EntityPlayer player, final ItemStack itemStack) 
	{
		super.onArmorTick(world, player, itemStack);
		if(this.effects == null || this.armorset == null)
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

}
