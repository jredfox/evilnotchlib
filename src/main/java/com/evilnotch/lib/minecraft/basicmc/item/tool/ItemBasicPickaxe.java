package com.evilnotch.lib.minecraft.basicmc.item.tool;

import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.minecraft.basicmc.auto.IAutoItem;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JSONProxy;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.ResourceLocation;

public class ItemBasicPickaxe extends ItemPickaxe implements IAutoItem{
	
	public ItemBasicPickaxe(ResourceLocation id, ToolMat mat,LangEntry...langList)
	{
		this(id, mat, null, true, langList);
	}
	
	public ItemBasicPickaxe(ResourceLocation id, ToolMat mat, CreativeTabs tab, LangEntry... langlist) 
	{
		this(id, mat, tab, true, langlist);
	}

	public ItemBasicPickaxe(ResourceLocation id, ToolMat material, CreativeTabs tab, boolean config, LangEntry... langlist) 
	{
		super(ToolMat.getMat(material, config));
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		//autofill
		this.register();
		this.populateLang(langlist);
		this.populateJSON();
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
