package com.evilnotch.lib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
/**
 * this is a class all about comparing logic on deep comparison of nbttagcompounds,nbttaglists and more as well as merging nbt
 * @author jredfox
 */
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
				paths.put(path, NBTTagCompound.class);
			for(String name : nbt.getKeySet())
			{
				NBTBase tag = nbt.getTag(name);
				if(JavaUtil.isStringNum(name))
					name = "nbtpathapi-name:" + name;//prevent this from being incompatible later
				this.parseNBT(tag,path + slash + name);
			}
		}
		else if(param_nbt instanceof NBTTagList)
		{
			if(secondCall)
				paths.put(path, NBTTagList.class);
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
				this.paths.put(path,NBTTagByteArray.class);
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
				this.paths.put(path, NBTTagIntArray.class);
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
				this.paths.put(path, NBTTagLongArray.class);
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
	/**
	 * this method isn't for comparing logic it's for storage in arrays. 
	 * The amount of paths size() and paths have to be equal
	 * use equalsLogic() instead
	 */
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
		else
		{
			for(String s : this.paths.keySet())
			{
				Object tag = this.paths.get(s);
				Object otherTag = other.paths.get(s);
				if(tag instanceof Number)
				{
					if(!compareValue(type,(Number)tag,(Number)otherTag))
						return false;
				}
				else if(!tag.equals(otherTag))
					return false;//for strings and empty nbtbases
			}
			return true;
		}
	}
	/**
	 * compare two values based upon CompareType logic
	 */
	public boolean compareValue(CompareType type,Number num, Number num2) 
	{
		if(type == CompareType.greaterThenEqual)
		{
			return greaterThenEqual(num, num2);
		}
		else if(type == CompareType.lessThenEqualTo)
		{
			return lessThenEqual(num, num2);
		}
		else if(type == CompareType.greaterThen)
		{
			return greaterThen(num, num2);
		}
		else if(type == CompareType.lessThen)
		{
			return lessThen(num, num2);
		}
		return false;
	}
	
	public boolean lessThenEqual(Number num, Number num2) 
	{
		if(num instanceof Integer)
		{
			return (int)num <= (int)num2;
		}
		else if(num instanceof Short)
		{
			return (short)num <= (short)num2; 
		}
		else if(num instanceof Byte)
		{
			return (byte)num <= (byte)num2; 
		}
		else if(num instanceof Long)
		{
			return (long)num <= (long)num2; 
		}
		else if(num instanceof Double)
		{
			return (double)num <= (double)num2; 
		}
		else if(num instanceof Float)
		{
			return (float)num <= (float)num2; 
		}
		return false;
	}
	public boolean lessThen(Number num, Number num2) 
	{
		if(num instanceof Integer)
		{
			return (int)num < (int)num2;
		}
		else if(num instanceof Short)
		{
			return (short)num < (short)num2; 
		}
		else if(num instanceof Byte)
		{
			return (byte)num < (byte)num2; 
		}
		else if(num instanceof Long)
		{
			return (long)num < (long)num2; 
		}
		else if(num instanceof Double)
		{
			return (double)num < (double)num2; 
		}
		else if(num instanceof Float)
		{
			return (float)num < (float)num2; 
		}
		return false;
	}
	public boolean greaterThenEqual(Number num, Number num2) 
	{
		if(num instanceof Integer)
		{
			return (int)num >= (int)num2;
		}
		else if(num instanceof Short)
		{
			return (short)num >= (short)num2; 
		}
		else if(num instanceof Byte)
		{
			return (byte)num >= (byte)num2; 
		}
		else if(num instanceof Long)
		{
			return (long)num >= (long)num2; 
		}
		else if(num instanceof Double)
		{
			return (double)num >= (double)num2; 
		}
		else if(num instanceof Float)
		{
			return (float)num >= (float)num2; 
		}
		return false;
	}
	public boolean greaterThen(Number num, Number num2) 
	{
		if(num instanceof Integer)
		{
			return (int)num > (int)num2;
		}
		else if(num instanceof Short)
		{
			return (short)num > (short)num2; 
		}
		else if(num instanceof Byte)
		{
			return (byte)num > (byte)num2; 
		}
		else if(num instanceof Long)
		{
			return (long)num > (long)num2; 
		}
		else if(num instanceof Double)
		{
			return (double)num > (double)num2; 
		}
		else if(num instanceof Float)
		{
			return (float)num > (float)num2; 
		}
		return false;
	}
	/**
	 * merge two NBTPathApi's the other's primitive values will get overriden
	 */
	public void merge(NBTPathApi api)
	{
		for(String s : api.paths.keySet())
		{
			this.paths.put(s, api.paths.get(s));
		}
	}
	/**
	 * different from merge no matter what path and what tag if it exists it won't get overriden
	 */
	public void copySafley(NBTPathApi api)
	{
		for(String s : api.paths.keySet())
		{
			if(!this.paths.containsKey(s))
			{
				this.paths.put(s, api.paths.get(s));
			}
		}
	}
	/**
	 * once your done with comparing and merging nbt's then you might want to compile it back again
	 */
	public NBTTagCompound compile()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		for(String path : this.paths.keySet())
		{
			this.mdkrs(nbt,path);
		}
		return nbt;
	}
	public void mdkrs(NBTTagCompound nbt,String p) 
	{
		String[] dir = p.split("/");
		NBTBase base = nbt;
		String path = dir[0];
		for(int i=0;i<dir.length;i++)
		{
			String cPath = dir[i];
			if(i != 0)
				path += "/" + cPath;
			NBTBase tag = nbt.getTag(cPath);
			if(tag == null)
			{
				nbt.setTag(cPath, this.getCompiledObject(this.paths.get(path)));
				tag = nbt.getTag(cPath);
			}
			if(tag instanceof NBTTagCompound)
				nbt = (NBTTagCompound)tag;
		}
	}
	/**
	 * get the compiled object from the decompiled path primitive/string/class value
	 */
	public NBTBase getCompiledObject(Object obj) 
	{
		if(obj instanceof Integer)
		{
			return new NBTTagInt((Integer) obj);
		}
		else if(obj instanceof Byte)
		{
			return new NBTTagByte((Byte) obj);
		}
		else if(obj instanceof Short)
		{
			return new NBTTagShort((Short) obj);
		}
		else if(obj instanceof Long)
		{
			return new NBTTagLong((Long) obj);
		}
		else if(obj instanceof Double)
		{
			return new NBTTagDouble((Double) obj);
		}
		else if(obj instanceof Float)
		{
			return new NBTTagFloat((Float) obj);
		}
		else if(obj instanceof String)
		{
			return new NBTTagString((String)obj);
		}
		else if(obj instanceof Class)
		{
			if(NBTTagCompound.class.equals(obj))
				return new NBTTagCompound();
			else if(NBTTagList.class.equals(obj))
				return new NBTTagList();
			//TODO: find work around around these
			else if(NBTTagByteArray.class.equals(obj))
				return new NBTTagByteArray(new byte[0]);
			else if(NBTTagIntArray.class.equals(obj))
				return new NBTTagIntArray(new int[0]);
			else if(NBTTagLongArray.class.equals(obj))
				return new NBTTagLongArray(new long[0]);
		}
		return null;
	}
	/**
	 * what logic is the nbtpath api going to be running upon when comparing logic
	 * @author jredfox
	 */
	public static enum CompareType
	{
		hasTags(),
		equals(),
		greaterThen(),
		greaterThenEqual(),
		lessThen(),
		lessThenEqualTo()
	}
	
	@Override
	public String toString()
	{
		return this.paths.keySet().toString();
	}

}
