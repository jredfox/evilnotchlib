package com.evilnotch.lib.minecraft.content.recipe;

import com.evilnotch.lib.minecraft.nbt.NBTPathApi;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ItemStackNBT {
	
	public Item item;
	public NBTPathApi.CompareType[] types;
	public NBTPathApi[] apis;
	public boolean allMeta;
	
	public ItemStackNBT(NBTPathApi.CompareType type,NBTTagCompound nbt,Item item)
	{
		this(type,nbt,item,true);
	}
	
	public ItemStackNBT(NBTPathApi.CompareType type,NBTTagCompound nbt,ItemStack stack)
	{
		this(type,nbt,stack.getItem(),stack.getItemDamage() == OreDictionary.WILDCARD_VALUE);
	}
	
	public ItemStackNBT(NBTPathApi.CompareType type,NBTTagCompound nbt,Item item,boolean allMeta)
	{
		this.types = new NBTPathApi.CompareType[]{type};
		this.apis = new NBTPathApi[]{new NBTPathApi(nbt)};
		this.item = item;
		this.allMeta = allMeta;
	}
	/**
	 * use this constructor for multiple compare types for multiple tags
	 */
	public ItemStackNBT(NBTPathApi.CompareType[] types,NBTTagCompound[] nbts,Item item,boolean allMeta)
	{
		this.types = types;
		this.apis = new NBTPathApi[nbts.length];
		for(int i=0;i<nbts.length;i++)
		{
			this.apis[i] = new NBTPathApi(nbts[i]);
		}
		this.item = item;
		this.allMeta = allMeta;
	}

}
