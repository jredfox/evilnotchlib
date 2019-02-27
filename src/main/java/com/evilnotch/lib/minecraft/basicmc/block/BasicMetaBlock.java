package com.evilnotch.lib.minecraft.basicmc.block;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.evilnotch.lib.main.loader.LoaderBlocks;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.auto.BlockWrapper;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;
import com.evilnotch.lib.minecraft.basicmc.block.item.IMetaName;
import com.evilnotch.lib.minecraft.basicmc.block.item.ItemBlockMeta;
import com.evilnotch.lib.minecraft.basicmc.block.property.IPropertyMeta;
import com.evilnotch.lib.minecraft.basicmc.block.property.IPropertyName;
import com.evilnotch.lib.minecraft.basicmc.client.block.ModelPart;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.block.Block;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class BasicMetaBlock extends BasicBlock implements IMetaName, IBasicBlockMeta{
	
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
	public BasicMetaBlock(Material mat,ResourceLocation id,CreativeTabs tab,BlockProperties props,IProperty pi,LangEntry... lang) {
		this(mat,mat.getMaterialMapColor(),id,tab,props,pi,lang);
	}
	
	/**
	 * uses the itemblock if not null regardless whether which boolean you call
	 */
	public BasicMetaBlock(Material blockMaterialIn, MapColor blockMapColorIn, ResourceLocation id, CreativeTabs tab,BlockProperties props,IProperty prop, LangEntry... langlist) 
	{
		super(blockMaterialIn, blockMapColorIn, id, tab, props,langlist);
		this.property = prop;
		this.populateJSON();
		
		//since vanilla is ignorant as hell by not populating new properties we need to reset the entire block state container and yes it's f****** final
		setStateConstructor(this.property);
	}
	
	/**
	 * supports metadata and block states
	 */
	@Override
	public void populateLang(LangEntry... langlist) 
	{
		LangRegistry.registerMetaLang(this, langlist);
	}
	
	@Override
	public void populateJSON()
	{
		if(!LoaderMain.isClient || this.property == null)
			return;
		JsonGen.registerBlockMetaJson(this, this.getModelPart(), this.property);
	}
	
	public void setStateConstructor(IProperty prop) 
	{
		this.blockState = this.createBlockState();
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
			return super.createBlockState();
		}
		return new BlockStateContainer(this, new IProperty[]{this.property});
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
		return "_" + this.getPropertyName(stack.getItemDamage());
	}
	/**
	 * metadata of itemstack to proper unlocalized suffix name
	 */
	public String getPropertyName(int meta) 
	{
		if(this.property instanceof PropertyInteger)
			return "" + meta;
		else if(this.property instanceof PropertyBool)
		{
			return "" + (meta == 0 ? false : true);
		}
		else if(this.property instanceof PropertyDirection)
		{
			return EnumFacing.getFront(meta).getName();
		}
		else if(this.property instanceof IPropertyName)
		{
			IPropertyName p = (IPropertyName)this.property;
			return ((IStringSerializable)p.getValue(meta)).getName();
		}
		return null;
	}

	@Override
	public ModelPart getModelPart() 
	{
		return ModelPart.cube_all;
	}
	
	public IProperty getProperty() 
	{
		return this.property;
	}
	
	@Override
	public ItemBlock getItemBlock()
	{
		if(this.itemblock == null)
		{
			ItemBlock b = new ItemBlockMeta(this);
			b.setRegistryName(this.getRegistryName());
			this.itemblock = b;
		}
		return this.itemblock;
	}
	
}
