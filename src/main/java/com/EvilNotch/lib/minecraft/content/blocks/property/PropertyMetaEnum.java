package com.EvilNotch.lib.minecraft.content.blocks.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

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
	@Override
	public Enum getValue(int meta) {
		Collection<T> c = this.getAllowedValues();
		for(T t : c)
		{
			if(t.getMetaData() == meta)
				return t;
		}
		return null;
	}
	
    /**
     * Create a new PropertyEnum with all Enum constants of the given class.
     */
    public static <T extends Enum<T> & IStringSerializable & IPropertyMeta> PropertyEnum<T> createProperty(String name, Class<T> clazz)
    {
        return createProperty(name, clazz, Predicates.alwaysTrue());
    }

    /**
     * Create a new PropertyEnum with all Enum constants of the given class that match the given Predicate.
     */
    public static <T extends Enum<T> & IStringSerializable & IPropertyMeta> PropertyEnum<T> createProperty(String name, Class<T> clazz, Predicate<T> filter)
    {
        return createProperty(name, clazz, Collections2.filter(Lists.newArrayList(clazz.getEnumConstants()), filter));
    }

    /**
     * Create a new PropertyEnum with the specified values
     */
    public static <T extends Enum<T> & IStringSerializable & IPropertyMeta> PropertyEnum<T> createProperty(String name, Class<T> clazz, T... values)
    {
        return createProperty(name, clazz, Lists.newArrayList(values));
    }


    /**
     * Create a new PropertyEnum with the specified values
     */
    public static <T extends Enum<T> & IStringSerializable & IPropertyMeta> PropertyEnum<T> createProperty(String name, Class<T> clazz, Collection<T> values)
    {
    	return new PropertyMetaEnum(name,clazz,values);
    }
}
