package com.evilnotch.lib.minecraft.content.items;

import com.evilnotch.lib.minecraft.content.LangEntry;
import com.evilnotch.lib.minecraft.content.blocks.item.IMetaName;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class BasicItemMeta extends BasicItem{
	
	public int maxMeta;
	
	public BasicItemMeta(ResourceLocation id,int maxMeta,LangEntry...langlist){
		this(id,maxMeta,null,langlist);
	}
	public BasicItemMeta(ResourceLocation id,int maxMeta,CreativeTabs tab,LangEntry...langlist){
		this(id,maxMeta,tab,true,true,true,langlist);
	}
	
	public BasicItemMeta(ResourceLocation id,int maxMeta,CreativeTabs tab,boolean model,boolean register,boolean lang,LangEntry... langlist)
	{
		super(id,tab,model,register,lang,langlist);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.maxMeta = maxMeta;
	}
	@Override
	public void populateLang(LangEntry[] langlist,String unlocalname,ResourceLocation id) {
		if(!this.useLangRegistry())
			return;
		for(LangEntry entry : langlist)
		{
			entry.langId = "item." + unlocalname + "_" + entry.meta + ".name";
			entry.loc = id;
			itemlangs.add(entry);
		}
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
	public int getMaxMeta() {
		return this.maxMeta;
	}

}
