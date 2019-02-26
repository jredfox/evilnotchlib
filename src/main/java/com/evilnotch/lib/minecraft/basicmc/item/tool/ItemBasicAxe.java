package com.evilnotch.lib.minecraft.basicmc.item.tool;

import java.util.ArrayList;

import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicItem;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemAxe;
import net.minecraft.util.ResourceLocation;

public class ItemBasicAxe extends ItemAxe implements IBasicItem<ItemAxe> {
	
	public boolean hasregister = false;
	public boolean hasmodel = false;
	public boolean haslang = false;
	public boolean hasconfig = false;
	
	public ItemBasicAxe(ToolMat mat,ResourceLocation id,LangEntry...langlist){
		this(mat,id,null,langlist);
	}
	public ItemBasicAxe(ToolMat mat, ResourceLocation id, CreativeTabs tab,LangEntry... langlist) {
		this(mat,id,tab,true,true,true,true,langlist);
	}

	public ItemBasicAxe(ToolMat material,ResourceLocation id,CreativeTabs tab,boolean model,boolean register,boolean lang, boolean config,LangEntry... langlist) {
		super(BasicItem.getMat(material,config),material.attackDamage,material.efficiency);
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		this.hasregister = register;
		this.hasmodel = model;
		this.haslang = lang;
		this.hasconfig = config;
		
		//autofill
		this.populateLang(langlist);
		this.populateJSON();
		
		LoaderItems.items.add(this);
	}
	
	public void populateLang(LangEntry... langs)
	{
		LangRegistry.registerLang(this, langs);
	}
	
	public void populateJSON()
	{
		JsonGen.registerItemJson(this);
	}
	
	@Override
	public ItemAxe getObject() {
		return this;
	}
	@Override
	public ResourceLocation getResourceLocation() {
		return this.getRegistryName();
	}
}
