package org.json.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.evilnotch.lib.util.JavaUtil;

public class JSONArrayList extends ArrayList{
	
	public JSONArrayList()
	{
		super();
	}
	
	public JSONArrayList(Collection col)
	{
		super(fixMap(col));
	}
	
	public JSONArrayList(int capacity)
	{
		super(capacity);
	}

	@Override
	public boolean add(Object obj)
	{
		if(!canPut(obj))
			obj = obj.toString();
		return super.add(obj);
	}
	
	@Override
	public void add(int index, Object obj)
	{
		if(!canPut(obj))
			obj = obj.toString();
		super.add(index, obj);
	}
	
	@Override
	public boolean addAll(Collection map)
	{
		map = fixMap(map);
		return super.addAll(map);
	}
	
	@Override
	public boolean addAll(int index, Collection map)
	{
		map = fixMap(map);
		return super.addAll(index, map);
	}
	
	/**
	 * converts the generic collection into a usable json interface map
	 */
	public static Collection fixMap(Collection map) 
	{
		if(map.isEmpty())
			return map;
		else if(map instanceof Map)
		{
			JSONMap.fixMap((Map)map);
			return map;
		}
		else if(map instanceof List)
		{
			List list = (List)map;
			int index = 0;
			for(Object obj : list)
			{
				if(!canPut(obj))
					list.set(index, obj);
				index++;
			}
			return list;
		}
		else
		{
			//a generic method to always fix it no matter what interface is used by it
			List list = new ArrayList();
			for(Object obj : map)
			{
				if(!canPut(obj))
					obj = obj.toString();
				list.add(obj);
			}
		}
		return map;
	}
	
	/**
	 * can the object without modifications be inputted into the json object/json array
	 */
	public static boolean canPut(Object value) 
	{
		return JSONObject.canPut(value);
	}
	
	public Float getFloat(int key)
	{
		return JavaUtil.getFloat((Number)this.get(key));
	}
	
	public Double getDouble(int key)
	{
		return JavaUtil.getDouble((Number)this.get(key));
	}
	
	public Long getLong(int key)
	{
		return JavaUtil.getLong((Number)this.get(key));
	}

	public Integer getInteger(int key)
	{
		return JavaUtil.getInt((Number)this.get(key));
	}
	
	public Short getShort(int key)
	{
		return JavaUtil.getShort((Number)this.get(key));
	}
	
	public Byte getByte(int key)
	{
		return JavaUtil.getByte((Number)this.get(key));
	}
	
	public String getString(int key)
	{
		return (String) this.get(key);
	}
	
	public JSONObject getJSONObject(int key)
	{
		return (JSONObject)this.get(key);
	}
	
	public JSONArray getJSONArray(int key)
	{
		return (JSONArray)this.get(key);
	}

}
