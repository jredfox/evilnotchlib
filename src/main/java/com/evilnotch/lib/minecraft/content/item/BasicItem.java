package com.evilnotch.lib.minecraft.content.item;

import java.util.ArrayList;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.content.item.armor.ArmorMat;
import com.evilnotch.lib.minecraft.content.item.tool.ToolMat;
import com.evilnotch.lib.minecraft.content.lang.LangEntry;
import com.evilnotch.lib.util.line.LineArray;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;

public class BasicItem extends Item implements IBasicItem{
	
	public boolean hasregister = false;
	public boolean hasmodel = false;
	public boolean haslang = false;
	
	public static ArrayList<LangEntry> itemlangs = new ArrayList();//is static so each object doesn't have a new arraylist optimized for finding and going through
	
	public BasicItem(ResourceLocation id,LangEntry...langlist){
		this(id,null,langlist);
	}
	public BasicItem(ResourceLocation id,CreativeTabs tab,LangEntry...langlist){
		this(id,tab,true,true,true,langlist);
	}
	
	public BasicItem(ResourceLocation id,CreativeTabs tab,boolean model,boolean register,boolean lang,LangEntry... langlist)
	{
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		this.hasregister = register;
		this.hasmodel = model;
		this.haslang = lang;
		
		//autofill
		populateLang(langlist,unlocalname,id);//not just client side I18l or something uses it on server side for translations
		
		MainJava.items.add(this);
	}
	
	public void populateLang(LangEntry[] langlist,String unlocalname,ResourceLocation id) {
		if(!this.useLangRegistry())
			return;
		for(LangEntry entry : langlist)
		{
			entry.langId = "item." + unlocalname + ".name";
			entry.loc = id;
			itemlangs.add(entry);
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

	/**
	 * nothing to config here
	 */
	@Override
	public boolean useConfigPropterties() {
		return false;
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
			if(MainJava.cfgTools != null)
			{
				LineArray line = new LineArray(mat.toString());
//				System.out.println("Before:" + line);
				MainJava.cfgTools.addLine(line);
				line = (LineArray) MainJava.cfgTools.getUpdatedLine(line);//critical line this lets the config override the default line
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
			if(MainJava.cfgArmors != null)
			{
				LineArray line = new LineArray(mat.toString());
				MainJava.cfgArmors.addLine(line);
				line = (LineArray) MainJava.cfgArmors.getUpdatedLine(line);//critical line this lets the config override the default line
//				System.out.println("Before:" + mat);
				mat = new ArmorMat(line);
//				System.out.println("After: " + mat);
				ArmorMat.armormats.put(mat.enumName, mat);
			}
		}
		return mat.getEnum();
	}

}
