package com.evilnotch.lib.minecraft.basicmc.auto.test;

import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.block.BasicMetaBlock;
import com.evilnotch.lib.minecraft.basicmc.block.BlockProperties;
import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class MultiSidedGrass extends BasicMetaBlock{
	
	public MultiSidedGrass(ResourceLocation id,IProperty pi,LangEntry... lang) {
		this(Material.ROCK,id,pi,lang);
	}
	
	/**
	 * lang name is whatever is "displayname","lang" 
	 * where lang is langauage type like "en_us" everyting else has been done for you
	 */
	public MultiSidedGrass(Material blockMaterialIn,ResourceLocation id,IProperty pi,LangEntry... lang) {
		this(blockMaterialIn,id,null,pi,lang);
	}
	public MultiSidedGrass(Material mat,ResourceLocation id,CreativeTabs tab,IProperty pi,LangEntry... lang) {
		this(mat,id,tab,null,pi,lang);
	}
	public MultiSidedGrass(Material mat,ResourceLocation id,CreativeTabs tab,BlockProperties props,IProperty pi,LangEntry... lang) {
		this(mat,mat.getMaterialMapColor(),id,tab,props,pi,lang);
	}
	
	public MultiSidedGrass(Material mat,ResourceLocation id,CreativeTabs tab,BlockProperties props,IProperty pi,ItemBlock ib,LangEntry... lang) {
		this(mat,mat.getMaterialMapColor(),id,tab,props,pi,lang);
	}
	
	/**
	 * uses the itemblock if not null regardless whether which boolean you call
	 */
	public MultiSidedGrass(Material mat, MapColor mc, ResourceLocation id, CreativeTabs tab,BlockProperties props,IProperty prop, LangEntry... langlist) 
	{
		super(mat,mc,id,tab,props,prop,langlist);
	}
	
	@Override
	public ModelPart getModelPart()
	{
		return ModelPart.cube_bottom_top;
	}

}
