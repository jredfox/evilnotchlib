package com.evilnotch.lib.minecraft.basicmc.block;

import java.util.List;

import com.evilnotch.lib.api.BlockApi;
import com.evilnotch.lib.main.loader.LoaderBlocks;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicBlock;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.config.ConfigBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BasicBlock extends Block implements IBasicBlock{
	
	public ItemBlock itemblock = null;
	public BlockProperties blockprops = null;
	public boolean hasconfig = true;
	
	public BasicBlock(ResourceLocation id, LangEntry... lang) {
		this(Material.ROCK,id,lang);
	}
	
	/**
	 * lang name is whatever is "displayname","lang" 
	 * where lang is langauage type like "en_us" everyting else has been done for you
	 */
	public BasicBlock(Material blockMaterialIn,ResourceLocation id,LangEntry... lang) {
		this(blockMaterialIn,id,null,lang);
	}
	public BasicBlock(Material mat,ResourceLocation id,CreativeTabs tab,LangEntry... lang) {
		this(mat,id,tab,null,lang);
	}
	public BasicBlock(Material mat,ResourceLocation id,CreativeTabs tab,BlockProperties props,LangEntry... lang) {
		this(mat,mat.getMaterialMapColor(),id,tab,true,true,true,true,null,true,props,lang);
	}
	
	/**
	 * MUST BE CALLED DURING PREINIT OR LATER
	 */
	public BasicBlock(Material blockMaterialIn, MapColor blockMapColorIn,ResourceLocation id,CreativeTabs tab,boolean model,boolean register,boolean lang,boolean config,ItemBlock itemblock,boolean useItemBlock,BlockProperties props,LangEntry... langlist) 
	{
		super(blockMaterialIn, blockMapColorIn);
		this.setRegistryName(id);
		String unlocalname = id.toString().replaceAll(":", ".");
		this.setUnlocalizedName(unlocalname);
		this.setCreativeTab(tab);
		
		this.populateLang(langlist);
		this.populateJSON();
		
		//set properties of the block
		fillProperties(props);
		
		LoaderBlocks.blocks.add(this);
		
		if(useItemBlock)
		{
			if(itemblock == null)
				itemblock = new ItemBlock(this);
			itemblock.setRegistryName(id);
			this.itemblock = itemblock;
		}
	}
	
	public void populateLang(LangEntry... langlist) 
	{
		LangRegistry.registerLang(this, langlist);
	}
	
	public void populateJSON()
	{
		JsonGen.registerBlockJson(this, this.getModelPart());
	}
	
	@SideOnly(Side.CLIENT)
	public ModelPart getModelPart()
	{
		return ModelPart.cube_all;
	}
	
	protected void fillProperties(BlockProperties props) 
	{
		if(props != null)
		{
			if(this.hasconfig)
				props = BasicBlock.getConfiguredBlockProps(this,props);
			this.blockprops = props;
			
			this.blockHardness = props.blockHardness;
			this.blockResistance = props.blastResistance;

			this.setHarvestLevel(props.harvestTool, props.harvestLvl);
			
			if(props.mat != null)
			{
				BlockApi.setMaterial(this, props.mat, true);
			}
			
			if(props.sound != null)
				this.setSoundType(props.sound);
			
			if(props.flamability != -1 && props.flameEncoragement != -1)
				Blocks.FIRE.setFireInfo(this, props.flameEncoragement, props.flamability);
			
			this.setLightLevel(props.lightValue);
			this.slipperiness = props.slipperiness;
			
			//props that are not defautly constructed
			if(props.advanced)
			{
				this.translucent = props.translucent;
				this.setLightOpacity(props.lightOpacity);
				this.useNeighborBrightness = props.useNeighborBrightness;
				this.enableStats = props.enableStats;
				this.blockParticleGravity = props.blockParticleGravity;
			}
		}
	}

	public static BlockProperties getConfiguredBlockProps(Block b,BlockProperties props) 
	{
		LineArray line = new LineArray("\"" + b.getRegistryName() + "\" " + props.toString());
		ConfigBase cfg = LoaderBlocks.cfgBlockProps;
		cfg.addLine(line);
		line = (LineArray) cfg.getUpdatedLine(line);
		return new BlockProperties(line);
	}

	@Override
	public Block getObject() 
	{
		return this;
	}

	@Override
	public ResourceLocation getResourceLocation() 
	{
		return this.getRegistryName();
	}

	@Override
	public ItemBlock getItemBlock()
	{
		ItemBlock b = new ItemBlock(this);
		b.setRegistryName(this.getRegistryName());
		return b;
	}
	
}
