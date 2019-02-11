package com.evilnotch.lib.minecraft.basicmc.item.tool;

import java.util.ArrayList;

import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;
import com.evilnotch.lib.minecraft.basicmc.item.IBasicItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.ResourceLocation;

public class ItemBasicPickaxe extends ItemPickaxe implements IBasicItem{
	
	public ArrayList<LangEntry> langs = new ArrayList();
	public boolean hasregister = false;
	public boolean hasmodel = false;
	public boolean haslang = false;
	public boolean hasconfig = false;
	
	public ItemBasicPickaxe(ToolMat mat,ResourceLocation id,LangEntry...langList){
		this(mat,id,null,langList);
	}
	public ItemBasicPickaxe(ToolMat mat,ResourceLocation id,CreativeTabs tab,LangEntry...langList){
		this(mat,id,tab,true,true,true,true,langList);
	}

	public ItemBasicPickaxe(ToolMat material,ResourceLocation id,CreativeTabs tab,boolean model,boolean register,boolean lang, boolean config,LangEntry... langlist) {
		super(BasicItem.getMat(material,config));
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		this.hasregister = register;
		this.hasmodel = model;
		this.haslang = lang;
		this.hasconfig = config;
		
		//autofill
		this.populateLang(langlist, unlocalname,id);
		
		LoaderItems.items.add(this);
	}
	public void populateLang(LangEntry[] langlist,String unlocalname,ResourceLocation id) {
		if(!this.useLangRegistry())
		{
			System.out.println("returning no lang:" + langlist[0] + " hasLang:" + this.haslang);
			return;
		}
		for(LangEntry entry : langlist)
		{
			entry.langId = "item." + unlocalname + ".name";
			entry.loc = id;
			LangRegistry.add(entry);
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
