package com.evilnotch.lib.minecraft.basicmc.item;

import com.evilnotch.lib.main.loader.LoaderItems;
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

public class BasicItem extends Item implements IBasicItem<Item>{
	
	public BasicItem(ResourceLocation id,LangEntry...langlist){
		this(id,null,langlist);
	}
	
	public BasicItem(ResourceLocation id,CreativeTabs tab,LangEntry... langlist)
	{
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		//autofill
		this.populateLang(langlist);//not just client side I18l or something uses it on server side for translations
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
	
	/**
	 * configures tool material before sending it to the constructor
	 * If config is turned off just returns default enum
	 */
	public static ToolMaterial getMat(ToolMat mat,boolean config) {
		if(config)
		{
			ToolMat cached = ToolMat.toolmats.get(mat.enumName);//grab preconfigured toolmat if available
			if(cached != null)
				return cached.getEnum();
			if(LoaderItems.cfgTools != null)
			{
				LineArray line = new LineArray(mat.toString());
//				System.out.println("Before:" + line);
				LoaderItems.cfgTools.addLine(line);
				line = (LineArray) LoaderItems.cfgTools.getUpdatedLine(line);//critical line this lets the config override the default line
				mat = new ToolMat(line);
				ToolMat.toolmats.put(mat.enumName, mat);
//				System.out.println("After: " + line);
			}
		}
		return mat.getEnum();
	}
	/**
	 * configures armor material before sending it to the constructor
	 * If config is turned off just returns default enum
	 */
	public static ArmorMaterial getMat(ArmorMat mat,boolean config) 
	{
		if(config)
		{
			ArmorMat cached = ArmorMat.armormats.get(mat.enumName);
			if(cached != null)
				return cached.getEnum();
			if(LoaderItems.cfgArmors != null)
			{
				LineArray line = new LineArray(mat.toString());
				LoaderItems.cfgArmors.addLine(line);
				line = (LineArray) LoaderItems.cfgArmors.getUpdatedLine(line);//critical line this lets the config override the default line
//				System.out.println("Before:" + mat);
				mat = new ArmorMat(line);
//				System.out.println("After: " + mat);
				ArmorMat.armormats.put(mat.enumName, mat);
			}
		}
		return mat.getEnum();
	}
	
	@Override
	public Item getObject() 
	{
		return this;
	}

}
