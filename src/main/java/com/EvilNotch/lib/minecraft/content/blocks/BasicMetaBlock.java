package com.EvilNotch.lib.minecraft.content.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.minecraft.content.LangEntry;
import com.EvilNotch.lib.minecraft.content.blocks.item.IMetaName;
import com.EvilNotch.lib.minecraft.content.blocks.item.ItemBlockMeta;
import com.EvilNotch.lib.minecraft.content.blocks.property.IPropertyMeta;
import com.EvilNotch.lib.minecraft.content.blocks.property.IPropertyName;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class BasicMetaBlock extends BasicBlock implements IMetaName{
	
	public IProperty property = null;
	
	public BasicMetaBlock(ResourceLocation id,IProperty pi,LangEntry... lang) {
		this(Material.ROCK,id,pi,lang);
	}
	
	/**
	 * lang name is whatever is "displayname","lang" 
	 * where lang is langauage type like "en_us" everyting else has been done for you
	 */
	public BasicMetaBlock(Material blockMaterialIn,ResourceLocation id,IProperty pi,LangEntry... lang) {
		this(blockMaterialIn,id,null,pi,lang);
	}
	public BasicMetaBlock(Material mat,ResourceLocation id,CreativeTabs tab,IProperty pi,LangEntry... lang) {
		this(mat,id,tab,null,pi,lang);
	}
	public BasicMetaBlock(Material mat,ResourceLocation id,CreativeTabs tab,Properties props,IProperty pi,LangEntry... lang) {
		this(mat,mat.getMaterialMapColor(),id,tab,true,true,true,true,null,props,pi,lang);
	}
	
	public BasicMetaBlock(Material mat,ResourceLocation id,CreativeTabs tab,Properties props,IProperty pi,ItemBlock ib,LangEntry... lang) {
		this(mat,mat.getMaterialMapColor(),id,tab,true,true,true,true,ib,props,pi,lang);
	}
	
	/**
	 * uses the itemblock if not null regardless whether which boolean you call
	 */
	public BasicMetaBlock(Material blockMaterialIn, MapColor blockMapColorIn, ResourceLocation id, CreativeTabs tab,boolean model, boolean register, boolean lang, boolean config, ItemBlock itemblock,Properties props,IProperty prop, LangEntry... langlist) 
	{
		super(blockMaterialIn, blockMapColorIn, id, tab, model, register, lang, config, itemblock, false, props,langlist);
		this.property = prop;
		
		if(itemblock == null)
			this.itemblock = new ItemBlockMeta(this);
		else
			this.itemblock = itemblock;
		
		this.itemblock.setRegistryName(id);
		System.out.println("Constructor Call:" + this.property);
		
		//since vanilla is ignorant as hell by not populating new properties we need to reset the entire block state container and yes it's f****** final
		setStateConstructor(this.property);
	}
	/**
	 * supports metadata and block states
	 */
	@Override
	public void populateLang(LangEntry[] langlist,String unlocalname,ResourceLocation id) 
	{
		for(LangEntry entry : langlist)
		{
			entry.langId = "tile." + unlocalname + "_" + entry.meta + ".name";
			entry.loc = id;
			blocklangs.add(entry);
		}
	}
	
	public void setStateConstructor(IProperty prop) {
		ReflectionUtil.setFinalObject(this, this.createBlockState(), Block.class, FieldAcess.blockstate);
        this.setDefaultState(this.blockState.getBaseState().withProperty(prop, getDefaultValue(prop)));
	}

	public Comparable getDefaultValue(IProperty prop) 
	{
		if(prop instanceof PropertyInteger)
		{
			return (Comparable) JavaUtil.getFirst(prop.getAllowedValues());
		}
		else if(prop instanceof PropertyBool)
		{
			return Boolean.valueOf(false);
		}
		else if(prop instanceof PropertyDirection)
		{
			return EnumFacing.NORTH;
		}
		else if(prop instanceof IPropertyName)
		{
			return (Comparable) JavaUtil.getFirst(prop.getAllowedValues());
		}
		return null;
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		if(this.property == null)
		{
//			System.out.println("Property hasn't yet been constructed yet!");
			return super.createBlockState();
		}
		return new BlockStateContainer(this,new IProperty[]{this.property});
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(this.property instanceof PropertyInteger)
		{
			return (Integer)state.getValue(this.property);
		}
		else if(this.property instanceof PropertyBool)
		{
			Boolean b = (Boolean) state.getValue(this.property);
			return !b ? 0 : 1;
		}
		else if(this.property instanceof PropertyDirection)
		{
			EnumFacing facing = (EnumFacing) state.getValue(this.property);
			return facing.getIndex();
		}
		else if(this.property instanceof IPropertyName)
		{
			IPropertyMeta p = (IPropertyMeta) state.getValue(this.property);
			return p.getMetaData();
		}
		return -1;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState state = this.getDefaultState();
		
		if(this.property instanceof PropertyInteger)
		{
			return state.withProperty(this.property, meta);
		}
		else if(this.property instanceof PropertyBool)
		{
			boolean b = meta == 0 ? false : true;
			return state.withProperty(this.property, Boolean.valueOf(b));
		}
		else if(this.property instanceof PropertyDirection)
		{
			return state.withProperty(this.property, EnumFacing.getFront(meta));
		}
		else if(this.property instanceof IPropertyName)
		{
			IPropertyName p = (IPropertyName) this.property;
			return state.withProperty(this.property, p.getValue(meta));
		}
		return null;
	}
	
	@Override
    public ItemStack getSilkTouchDrop(IBlockState state)
    {
		return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state) );
    }
	@Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
		Set<Integer> metas = getValuesOfProperty(this.property);
		for(int i : metas)
		{
			items.add(new ItemStack(this,1,i));
		}
    }
	@Override
	public Set<Integer> getValuesOfProperty(IProperty p)
	{
		Set<Integer> set = new HashSet();
		if(this.property instanceof PropertyInteger)
		{
			Collection<Integer> li = this.property.getAllowedValues();
			for(Integer i : li)
				set.add(i);
		}
		else if(this.property instanceof PropertyBool)
		{
			return (Set<Integer>) JavaUtil.asSet(0,1);
		}
		else if(this.property instanceof PropertyDirection)
		{
			for(EnumFacing f : EnumFacing.VALUES)
				set.add(f.getIndex());
		}
		else if(this.property instanceof IPropertyName)
		{
			Set<IPropertyMeta> li = (Set<IPropertyMeta>) this.property.getAllowedValues();
			for(IPropertyMeta m : li)
				set.add(m.getMetaData());
		}
		return set;
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
	
	@Override
	public IProperty getStateProperty(){
		return this.property;
	}
	@Override
	public HashMap<Integer, ModelResourceLocation> getModelMap() 
	{
		HashMap<Integer, ModelResourceLocation> map = new HashMap();
		Set<Integer> set = this.getValuesOfProperty(this.getStateProperty());
		for(int i : set)
		{
			if(this.property instanceof PropertyInteger)
			{
				map.put(i, new ModelResourceLocation(this.getRegistryName() + "_" + i,this.property.getName() + "=" + i));
			}
			else if(this.property instanceof PropertyBool)
			{
				boolean b = i == 0 ? false : true;
				map.put(i, new ModelResourceLocation(this.getRegistryName() + "_" + b,this.property.getName() + "=" + b));
			}
			else if(this.property instanceof PropertyDirection)
			{
				EnumFacing f = EnumFacing.getFront(i);
				map.put(i, new ModelResourceLocation(this.getRegistryName() + "_" + f.getName(),this.property.getName() + "=" + f.getName()));
			}
			else if(this.property instanceof IPropertyName)
			{
				IPropertyName p = (IPropertyName) this.property;
				IStringSerializable getter = (IStringSerializable) p.getValue(i);
				map.put(i, new ModelResourceLocation(this.getRegistryName() + "_" + getter.getName(),this.property.getName() + "=" + getter.getName()));
			}
		}
		return map;
	}
}
