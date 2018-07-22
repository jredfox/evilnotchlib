package com.EvilNotch.lib.minecraft.content.blocks;

import java.util.Collection;

import com.EvilNotch.lib.minecraft.content.LangEntry;
import com.EvilNotch.lib.minecraft.content.blocks.BasicBlock.Properties;
import com.EvilNotch.lib.minecraft.content.blocks.item.IMetaName;
import com.EvilNotch.lib.minecraft.content.blocks.item.ItemBlockMeta;
import com.EvilNotch.lib.minecraft.content.blocks.property.PropertyMetaEnum;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class BasicMetaBlock extends BasicBlock implements IMetaName{
	
	public IProperty property;
	
	public BasicMetaBlock(ResourceLocation id,PropertyInteger pi,LangEntry... lang) {
		this(Material.ROCK,id,pi,lang);
	}
	
	/**
	 * lang name is whatever is "displayname","lang" 
	 * where lang is langauage type like "en_us" everyting else has been done for you
	 */
	public BasicMetaBlock(Material blockMaterialIn,ResourceLocation id,PropertyInteger pi,LangEntry... lang) {
		this(blockMaterialIn,id,null,pi,lang);
	}
	public BasicMetaBlock(Material mat,ResourceLocation id,CreativeTabs tab,PropertyInteger pi,LangEntry... lang) {
		this(mat,id,tab,null,pi,lang);
	}
	public BasicMetaBlock(Material mat,ResourceLocation id,CreativeTabs tab,Properties props,PropertyInteger pi,LangEntry... lang) {
		this(mat,mat.getMaterialMapColor(),id,tab,true,true,true,true,null,false,props,pi,lang);
	}

	public BasicMetaBlock(Material blockMaterialIn, MapColor blockMapColorIn, ResourceLocation id, CreativeTabs tab,
			boolean model, boolean register, boolean lang, boolean config, ItemBlock itemblock, boolean useItemBlock,
			Properties props,IProperty prop, LangEntry[] langlist) {
		super(blockMaterialIn, blockMapColorIn, id, tab, model, register, lang, config, itemblock, useItemBlock, props,
				langlist);
		this.property = prop;
		
		this.itemblock = new ItemBlockMeta(this);
		this.itemblock.setRegistryName(id);
		
        this.setDefaultState(this.blockState.getBaseState().withProperty(prop, getValue(prop)));
	}
	public Comparable getValue(IProperty prop) 
	{
		if(prop instanceof PropertyInteger)
		{
			return (Comparable) JavaUtil.getFirst(prop.getAllowedValues());
		}
		else if(prop instanceof PropertyBool)
		{
			return new Boolean(false);
		}
		else if(prop instanceof PropertyDirection)
		{
			return EnumFacing.NORTH;
		}
		else if(prop instanceof PropertyMetaEnum)
		{
			return (Comparable) JavaUtil.getFirst(prop.getAllowedValues());
		}
		return null;
	}

	@Override
	public BlockStateContainer createBlockState(){
		return new BlockStateContainer(this,new IProperty[]{this.property});
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return (Integer)state.getValue(this.property);
	}
	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(this.property, meta);
	}
	@Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
		return new ItemStack(Item.getItemFromBlock(this), 1, (Integer)state.getValue(this.property) );
    }
	@Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
		Collection<Integer> metas = this.property.getAllowedValues();
		for(int i : metas)
		{
			items.add(new ItemStack(this,1,i));
		}
    }
	@Override
	public int damageDropped(IBlockState state){
		return this.getMetaFromState(state);
	}
	/**
	 * gets appended in the name
	 */
	@Override
	public String getSpecialName(ItemStack stack) {
		return "_" + stack.getItemDamage();
	}
	

}
