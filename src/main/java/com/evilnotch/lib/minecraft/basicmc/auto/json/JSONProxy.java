package com.evilnotch.lib.minecraft.basicmc.auto.json;

import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;

public class JSONProxy {

	public static void registerItemJson(Item item) 
	{
		if(LoaderMain.isClient)
		{
			JsonGen.registerItemJson(item);
		}
	}
	
	public static void registerItemMetaJson(Item item, int maxMeta)
	{
		if(LoaderMain.isClient)
		{
			JsonGen.registerItemMetaJson(item, maxMeta);
		}
	}
	
	public static void registerBlockJson(Block block) 
	{
		if(LoaderMain.isClient)
		{
			JsonGen.registerBlockJson(block, ModelPart.cube_all);
		}
	}

	public static void registerBlockJson(Block block, ModelPart modelPart) 
	{
		if(LoaderMain.isClient)
		{
			JsonGen.registerBlockJson(block, modelPart);
		}
	}
	
	public static void registerBlockMetaJson(Block b, IProperty p)
	{
		if(LoaderMain.isClient)
		{
			JsonGen.registerBlockMetaJson(b, ModelPart.cube_all, p);
		}
	}

	public static void registerBlockMetaJson(Block block, ModelPart modelPart, IProperty prop) 
	{
		if(LoaderMain.isClient)
		{
			JsonGen.registerBlockMetaJson(block, modelPart, prop);
		}
	}
}
