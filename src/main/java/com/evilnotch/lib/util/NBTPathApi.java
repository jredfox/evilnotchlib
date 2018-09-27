package com.evilnotch.lib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.evilnotch.lib.api.MCPSidedString;
import com.evilnotch.lib.api.ReflectionUtil;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
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
import net.minecraft.nbt.NBTTagString;

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
	 * decompile nbt at it's source for comparison calls it'self recursivley
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
		else if(param_nbt instanceof NBTTagByteArray)
		{
			NBTTagByteArray list = (NBTTagByteArray)param_nbt;
			if(secondCall)
				this.paths.put(path, new NBTTagByteArray(new byte[0]));
			byte[] values = list.getByteArray();
			for(int i=0;i<values.length;i++)
			{
				byte value = values[i];
				this.paths.put(path + slash + i, value);
			}
		}
		else if(param_nbt instanceof NBTTagIntArray)
		{
			NBTTagIntArray list = (NBTTagIntArray)param_nbt;
			if(secondCall)
				this.paths.put(path, new NBTTagIntArray(new int[0]));
			int[] values = list.getIntArray();
			for(int i=0;i<values.length;i++)
			{
				int value = values[i];
				this.paths.put(path + slash + i, value);
			}
		}
		else if(param_nbt instanceof NBTTagLongArray)
		{
			NBTTagLongArray list = (NBTTagLongArray)param_nbt;
			if(secondCall)
				this.paths.put(path, new NBTTagByteArray(new byte[0]));
			long[] values = (long[]) ReflectionUtil.getObject(param_nbt, NBTTagLongArray.class, new MCPSidedString("data","field_193587_b").toString() );
			for(int i=0;i<values.length;i++)
			{
				long value = values[i];
				this.paths.put(path + slash + i, value);
			}
		}
		else if(isPrimitive(param_nbt))
			this.paths.put(path,getPrimitive(param_nbt));
	}
	/**
	 * not as in is data non object but as in is this NBTTagCompound,NBTTagList,NBTArray or simply simple
	 */
	public boolean isPrimitive(NBTBase tag) 
	{
		return tag instanceof NBTPrimitive || tag instanceof NBTTagString;
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
		else if(tag instanceof NBTTagString)
		{
			return ((NBTTagString)tag).getString();
		}
		return null;
	}
	/**
	 * deep comparison to know whether or not an nbt has all required tags
	 */
	public boolean hasTags(NBTPathApi other)
	{
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
		if(this.paths.size() != api.paths.size())
			return false;
		for(String s : this.paths.keySet())
		{
			Object tag = this.paths.get(s);
			Object otherTag = api.paths.get(s);
			if(!tag.equals(otherTag))
				return false;
		}
		
		return true;
	}
	
	/**
	 * compare nbt based upon logic. the hasTags() operation won't trigger actual comparing
	 */
	public boolean equalsLogic(CompareType type,NBTPathApi other)
	{
		if(type == CompareType.hasTags)
		{
			return this.hasTags(other);
		}
		else if(type == CompareType.equals)
		{
			for(String s : this.paths.keySet())
			{
				Object tag = this.paths.get(s);
				Object otherTag = other.paths.get(s);
				if(!tag.equals(otherTag))
					return false;
			}
			return true;
		}
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
	
	/**
	 * Safely merge one nbt to the other one with nbttaglist support primitive values and primitive array indexes will get overriden though
	 */
	public void merge(NBTPathApi api)
	{
		//TODO:
	}
	/**
	 * different from merge no matter what path and what tag if it exists it won't get overriden
	 */
	public void copySafley(NBTPathApi api)
	{
		//TODO:
	}
	
	public NBTTagCompound compile()
	{
		//TODO:
		return null;
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
		return this.paths.toString();
	}

}
