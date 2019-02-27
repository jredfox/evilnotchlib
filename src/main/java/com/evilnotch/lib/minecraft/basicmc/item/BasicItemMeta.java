package com.evilnotch.lib.minecraft.basicmc.item;

import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class BasicItemMeta extends BasicItem implements IBasicItemMeta{
	
	public int maxMeta;
	private boolean constructedMeta = false;
	
	public BasicItemMeta(ResourceLocation id,int maxMeta,LangEntry...langlist){
		this(id,maxMeta,null,langlist);
	}
	
	public BasicItemMeta(ResourceLocation id,int maxMeta,CreativeTabs tab,LangEntry... langlist)
	{
		super(id,tab,langlist);
		this.constructedMeta = true;
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.maxMeta = maxMeta;
		this.populateJSON();
	}
	
	@Override
	public void populateJSON() 
	{
		if(!constructedMeta)
			return;
		JsonGen.registerItemMetaJson(this, this.getMaxMeta());
	}

	@Override
	public void populateLang(LangEntry... langs)
	{
		LangRegistry.registerMetaLang(this, langs);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack){
		return super.getUnlocalizedName() + "_" + stack.getItemDamage();
	}
	
	@Override
	public int getMetadata(int meta){
		return meta;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
	    if(!this.isInCreativeTab(tab))
	    	return;
		for(int i=0;i<=this.maxMeta;i++)
		{
			items.add(new ItemStack(this,1,i));
		}
    }

	@Override
	public int getMaxMeta() 
	{
		return this.maxMeta;
	}

}
