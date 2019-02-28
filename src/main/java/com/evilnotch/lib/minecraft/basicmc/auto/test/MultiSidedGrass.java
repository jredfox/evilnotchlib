package com.evilnotch.lib.minecraft.basicmc.auto.test;

import com.evilnotch.lib.minecraft.basicmc.auto.block.BlockProperty;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.block.BasicMetaBlock;
import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;

public class MultiSidedGrass extends BasicMetaBlock{
	
	public MultiSidedGrass(ResourceLocation id, IProperty ip, LangEntry... lang) 
	{
		this(id, ip, Material.ROCK, lang);
	}
	
	/**
	 * lang name is whatever is "displayname","lang" 
	 * where lang is langauage type like "en_us" everyting else has been done for you
	 */
	public MultiSidedGrass(ResourceLocation id, IProperty ip, Material mat, LangEntry... lang) 
	{
		this(id, ip, mat, null, lang);
	}
	
	public MultiSidedGrass(ResourceLocation id, IProperty ip, Material mat, CreativeTabs tab,LangEntry... lang) 
	{
		this(id, ip, mat, tab, null, lang);
	}
	
	public MultiSidedGrass(ResourceLocation id, IProperty ip, Material mat, CreativeTabs tab, BlockProperty props,LangEntry... lang) 
	{
		this(id, ip, mat, mat.getMaterialMapColor(), tab, props, true, lang);
	}
	
	/**
	 * uses the itemblock if not null regardless whether which boolean you call
	 */
	public MultiSidedGrass(ResourceLocation id, IProperty prop, Material mat, MapColor color, CreativeTabs tab, BlockProperty props, boolean b, LangEntry... langlist) 
	{
		super(id, prop, mat, color, tab, props, b, langlist);
	}
	
	@Override
	public ModelPart getModelPart()
	{
		return ModelPart.cube_bottom_top;
	}

}
