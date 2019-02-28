package com.evilnotch.lib.minecraft.basicmc.block;

import com.evilnotch.lib.main.loader.LoaderBlocks;
import com.evilnotch.lib.minecraft.basicmc.auto.BlockWrapper;
import com.evilnotch.lib.minecraft.basicmc.auto.IAutoBlock;
import com.evilnotch.lib.minecraft.basicmc.auto.block.BlockProperty;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JSONProxy;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.client.model.ModelPart;
import com.evilnotch.lib.minecraft.util.BlockUtil;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class BasicBlock extends Block implements IAutoBlock{
	
	public ItemBlock itemblock = null;
	public BlockProperty blockprops = null;
	public boolean hasConfig = true;
	
	public BasicBlock(ResourceLocation id, LangEntry... lang) 
	{
		this(id, Material.ROCK, lang);
	}
	
	/**
	 * lang name is whatever is "displayname","lang" 
	 * where lang is langauage type like "en_us" everyting else has been done for you
	 */
	public BasicBlock(ResourceLocation id, Material mat, LangEntry... lang) 
	{
		this(id, mat, null, lang);
	}
	
	public BasicBlock(ResourceLocation id, Material mat, CreativeTabs tab, LangEntry... lang) 
	{
		this(id, mat, tab, null, lang);
	}
	
	public BasicBlock(ResourceLocation id, Material mat, CreativeTabs tab, BlockProperty props, LangEntry... lang) 
	{
		this(id, mat, mat.getMaterialMapColor(), tab, props, true, lang);
	}
	
	/**
	 * MUST BE CALLED DURING PREINIT OR LATER
	 */
	public BasicBlock(ResourceLocation id, Material blockMaterialIn, MapColor blockMapColorIn, CreativeTabs tab, BlockProperty props, boolean hasConfig, LangEntry... langlist) 
	{
		super(blockMaterialIn, blockMapColorIn);
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		this.itemblock = this.getItemBlock();
		
		this.populateLang(langlist);
		this.populateJSON();
		this.register();
		this.hasConfig = hasConfig;
		
		//set properties of the block
		fillProperties(props);
	}
	
	public void register() 
	{
		if(this.canRegister())
			LoaderBlocks.blocks.add(new BlockWrapper(this, this.getItemBlock()));
	}

	public void populateLang(LangEntry... langlist) 
	{
		if(this.canRegisterLang())
			LangRegistry.registerLang(this, langlist);
	}
	
	public void populateJSON()
	{
		if(this.canRegisterJSON())
			JSONProxy.registerBlockJson(this, this.getModelPart());
	}
	
	protected void fillProperties(BlockProperty props) 
	{
		if(props != null)
		{
			props = BlockProperty.getProperty(props, this.hasConfig);
			this.blockprops = props;
			
			this.blockHardness = props.blockHardness;
			this.blockResistance = props.blastResistance;

			this.setHarvestLevel(props.harvestTool, props.harvestLvl);
			
			if(props.mat != null)
			{
				BlockUtil.setMaterial(this, props.mat, true);
			}
			
			if(props.sound != null)
				this.setSoundType(props.sound);
			
			Blocks.FIRE.setFireInfo(this, props.flameEncoragement, props.flamability);
			this.setLightLevel(props.lightValue);
			this.slipperiness = props.slipperiness;
			this.translucent = props.translucent;
			this.setLightOpacity(props.lightOpacity);
			this.useNeighborBrightness = props.useNeighborBrightness;
			this.enableStats = props.enableStats;
			this.blockParticleGravity = props.blockParticleGravity;
		}
	}
	
	public ItemBlock getItemBlock()
	{
		if(!this.hasItemBlock())
			return null;
		
		if(this.itemblock == null)
		{
			ItemBlock b = new ItemBlock(this);
			b.setRegistryName(this.getRegistryName());
			this.itemblock = b;
		}
		return this.itemblock;
	}
	
	public ModelPart getModelPart()
	{
		return ModelPart.cube_all;
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

	@Override
	public boolean hasItemBlock()
	{
		return true;
	}
	
}
