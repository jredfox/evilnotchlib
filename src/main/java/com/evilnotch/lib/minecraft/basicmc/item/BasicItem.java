package com.evilnotch.lib.minecraft.basicmc.item;

import com.evilnotch.lib.main.loader.LoaderItems;
import com.evilnotch.lib.minecraft.basicmc.auto.IAutoItem;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JSONProxy;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.item.armor.ArmorMat;
import com.evilnotch.lib.minecraft.basicmc.item.tool.ToolMat;
import com.evilnotch.lib.util.line.LineArray;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;

public class BasicItem extends Item implements IAutoItem{
	
	public BasicItem(ResourceLocation id, LangEntry...langlist)
	{
		this(id, null, langlist);
	}
	
	public BasicItem(ResourceLocation id, CreativeTabs tab, LangEntry... langlist)
	{
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		//autofill
		this.register();
		this.populateLang(langlist);//not just client side I18l or something uses it on server side for translations
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
