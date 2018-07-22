package com.EvilNotch.lib.minecraft.content.blocks.test;

import com.EvilNotch.lib.minecraft.content.blocks.property.IPropertyMeta;

import net.minecraft.util.IStringSerializable;

public enum EnumCheese implements IStringSerializable,IPropertyMeta{
	
	swiss("swiss",0),
	amrican("american",1);
	
	public String name;
	public int id;
	
	private EnumCheese(String str,int index){
		this.name = str;
		this.id = index;
	}

	@Override
	public Integer getMetaData() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
