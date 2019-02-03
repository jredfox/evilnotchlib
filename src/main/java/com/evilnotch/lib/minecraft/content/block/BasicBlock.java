package com.evilnotch.lib.minecraft.content.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.evilnotch.lib.api.BlockApi;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.loader.LoaderBlocks;
import com.evilnotch.lib.minecraft.content.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.content.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.content.client.block.ModelPart;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.config.ConfigBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public class BasicBlock extends Block implements IBasicBlock{
	
	public boolean hasregister = false;
	public boolean hasmodel = false;
	public boolean haslang = false;
	public boolean hasconfig = false;
	public ItemBlock itemblock = null;
	public BlockProperties blockprops = null;
	public List<String> cacheStates;
	
	public BasicBlock(ResourceLocation id,LangEntry... lang) {
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
		
		this.hasregister = register;
		this.hasmodel = model;
		this.haslang = lang;
		this.hasconfig = config;
		
		//autofill
		populateLang(langlist,unlocalname,id);
		
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
	
	protected void fillProperties(BlockProperties props) 
	{
		if(props != null)
		{
			if(this.useConfigPropterties())
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

	public void populateLang(LangEntry[] langlist,String unlocalname,ResourceLocation id) 
	{
		if(!this.useLangRegistry())
			return;
		for(LangEntry entry : langlist)
		{
			entry.langId = "tile." + unlocalname + ".name";
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
	@Override
	public ItemBlock getItemBlock() {
		return this.itemblock;
	}
	@Override
	public boolean hasItemBlock() {
		return this.getItemBlock() != null;
	}

	@Override
	public List<String> getModelStates() {
		return JavaUtil.asArray(new Object[]{"inventory","normal"});
	}
	
	@Override
	public List<String> getBlockStatesNames()
	{
		if(cacheStates != null)
		{
			return cacheStates;
		}
		List<String> list = new ArrayList();
		IProperty prop = this.getStateProperty();
		if(prop == null)
			list.add("normal");
		else
		{
			if(prop instanceof PropertyInteger)
			{
				PropertyInteger i = (PropertyInteger)prop;
				Collection<Integer> ints = i.getAllowedValues();
				for(Integer index : ints)
					list.add(prop.getName() + "=" + index);
			}
			else if (prop instanceof PropertyBool)
			{
				PropertyBool p = (PropertyBool)prop;
				list.add(p.getName() + "=" + true);
				list.add(p.getName() + "=" + false);
				PropertyDirection d;
			}
			else if (prop instanceof PropertyEnum)
			{
				//this also covers PropertyDirection
				PropertyEnum p = (PropertyEnum)prop;
				Collection <IStringSerializable> enums = p.getAllowedValues();
				for(IStringSerializable e : enums)
				{
					list.add(p.getName() + "=" + e.getName());
				}
			}
		}
		cacheStates = list;
		return list;
	}
	@Nullable
	@Override
	public IProperty getStateProperty() {
		return null;
	}

	@Override
	public Set<Integer> getValuesOfProperty(IProperty p) {
		return JavaUtil.asSet(0);
	}

	@Override
	public HashMap<Integer, ModelResourceLocation> getModelMap() 
	{
		HashMap<Integer, ModelResourceLocation> map = new HashMap();
		map.put(0, new ModelResourceLocation(this.getRegistryName().toString(),"inventory"));
		return map;
	}
	
	@Override
	public ModelPart getModelPart() {
		return ModelPart.cube_all;
	}

	@Override
	public BlockProperties getBlockProperties() {
		return this.blockprops;
	}

}
