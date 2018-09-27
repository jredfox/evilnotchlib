package com.evilnotch.lib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;

public class NBTPathApi {
	/**
	 * hashmap between string and mostly primitive objects except for identifyers
	 */
	public HashMap<String,Object> paths = new HashMap<>();
	
	public NBTPathApi(NBTTagCompound nbt)
	{
		parseNBT(nbt,"");
	}
	/**
	 * decompile nbt at it's source for comparison
	 */
	public void parseNBT(NBTBase param_nbt,String path)
	{
		String slash = path.isEmpty() ? "" : "/";
		boolean secondCall = !path.isEmpty();
		
		if(param_nbt instanceof NBTTagCompound)
		{
			NBTTagCompound nbt = (NBTTagCompound)param_nbt;
			if(secondCall)
				paths.put(path, new NBTTagCompound());
			for(String name : nbt.getKeySet())
			{
				NBTBase tag = nbt.getTag(name);
				this.parseNBT(tag,path + slash + name);
			}
		}
		else if(param_nbt instanceof NBTTagList)
		{
			if(secondCall)
				paths.put(path, new NBTTagList());
			NBTTagList list = (NBTTagList)param_nbt;
			for(int i=0;i<list.tagCount();i++)
			{
				NBTBase base = list.get(i);
				this.parseNBT(base, path + slash + i);
			}
		}
		else if(param_nbt instanceof NBTTagByteArray || param_nbt instanceof NBTTagIntArray || param_nbt instanceof NBTTagLongArray)
		{
			this.paths.put(path, param_nbt);
		}
		else
			this.paths.put(path,getPrimitive(param_nbt));
	}
	public Object getPrimitive(NBTBase tag) 
	{
		if(tag instanceof NBTTagInt)
		{
			return ((NBTTagInt)tag).getInt();
		}
		else if (tag instanceof NBTTagByte)
		{
			return ((NBTTagByte)tag).getByte();
		}
		else if (tag instanceof NBTTagShort)
		{
			return ((NBTTagShort)tag).getShort();
		}
		else if (tag instanceof NBTTagLong)
		{
			return ((NBTTagLong)tag).getLong();
		}
		else if (tag instanceof NBTTagFloat)
		{
			return ((NBTTagFloat)tag).getFloat();
		}
		else if (tag instanceof NBTTagDouble)
		{
			return ((NBTTagDouble)tag).getDouble();
		}
		return null;
	}
	/**
	 * deep comparison to know whether or not an nbt has all required tags
	 */
	public boolean hasTags(NBTPathApi other)
	{
		if(this.paths.size() != other.paths.size())
			return false;
		for(String s : this.paths.keySet())
			if(!other.paths.keySet().contains(s))
				return false;
		return true;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof NBTPathApi))
			return false;
		
		NBTPathApi api = (NBTPathApi) obj;
		if(!this.hasTags(api))
			return false;
		
		for(String s : this.paths.keySet())
		{
			Object tag = this.paths.get(s);
			Object otherTag = api.paths.get(s);
			if(tag instanceof Number && tag != otherTag)
				return false;
			else
			{
				if(!tag.equals(otherTag))
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * compare nbt based upon logic. the hasTags() operation won't trigger actual comparing
	 */
	public boolean equalsLogic(CompareType type,NBTPathApi other)
	{
		if(!hasTags(other))
			return false;//all method needs the same tags for comparing if they don't have them it's false
		else if(type == CompareType.hasTags)
			return true;
		
		else if(type == CompareType.equals)
			return this.equals(other);
		else if(type == CompareType.greaterThenEqual)
		{
			
		}
		else if(type == CompareType.lessThenEqualTo)
		{
			
		}
		else if(type == CompareType.greaterThen)
		{
			
		}
		else if(type == CompareType.lessThen)
		{
			
		}
		return false;
	}
	
	public static enum CompareType
	{
		hasTags(),
		equals(),
		greaterThenEqual(),
		lessThenEqualTo(),
		greaterThen(),
		lessThen()
	}
	
	@Override
	public String toString()
	{
		return this.paths.keySet().toString();
	}

}
