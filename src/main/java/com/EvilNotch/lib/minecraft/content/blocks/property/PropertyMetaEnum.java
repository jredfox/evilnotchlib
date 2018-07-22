package com.EvilNotch.lib.minecraft.content.blocks.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public class PropertyMetaEnum<T extends Enum<T> & IStringSerializable & IPropertyMeta> extends PropertyEnum implements IPropertyName{

	public PropertyMetaEnum(String name, Class<T> valueClass, Collection<T> allowedValues) {
		super(name, valueClass, allowedValues);
	}

	@Override
	public Set<Integer> getMetaDataValues()
	{
		Collection<T> list = this.getAllowedValues();
		Set<Integer> set = new HashSet();
		for(IPropertyMeta t : list)
		{
			set.add(t.getMetaData());
		}
		return set;
	}
}
