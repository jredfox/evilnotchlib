package org.json.simple;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.evilnotch.lib.util.JavaUtil;

import net.minecraftforge.common.util.EnumHelper;

/**
 * this is a json map that allows only primitive and other json values into the map
 * @author jredfox
 */
public class JSONMap extends LinkedHashMap{
	
	public JSONMap()
	{
		super();
	}
	
	public JSONMap(Map map) 
	{
		super(getConstructorMap(map));
	}
	
	public JSONMap(int capacity)
	{
		super(capacity);
	}
	
	public static Map getConstructorMap(Map map)
	{
		fixMap(map);
		return map;
	}

	@Override
	public Object put(Object key, Object value)
	{
		if(!canPut(value))
			value = value.toString();
		return super.put(key, value);
	}

	@Override
	public Object putIfAbsent(Object key, Object value)
	{
		if(!canPut(value))
			value = value.toString();
		
		return super.putIfAbsent(key, value);
	}
	
	@Override
	public void putAll(Map map)
	{
		if(map.isEmpty())
			return;
		fixMap(map);
		super.putAll(map);
	}
	
	/**
	 * converts the map to usable json objects before inputting into the super map
	 */
	public static void fixMap(Map map) 
	{
		Set<Map.Entry> set = map.entrySet();
		for(Map.Entry pair : set)
		{
			if(!canPut(pair.getValue()))
				map.put(pair.getKey(), pair.getValue().toString());
		}
	}
	
	/**
	 * can the object without modifications be inputted into the json object/json array
	 */
	public static boolean canPut(Object value) 
	{
		return value == null || value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray;
	}
	
	public Float getFloat(Object key)
	{
		return JavaUtil.getFloat((Number)this.get(key));
	}
	
	public Double getDouble(Object key)
	{
		return JavaUtil.getDouble((Number)this.get(key));
	}
	
	public Long getLong(Object key)
	{
		return JavaUtil.getLong((Number)this.get(key));
	}

	public Integer getInteger(Object key)
	{
		return JavaUtil.getInt((Number)this.get(key));
	}
	
	public Short getShort(Object key)
	{
		return JavaUtil.getShort((Number)this.get(key));
	}
	
	public Byte getByte(Object key)
	{
		return JavaUtil.getByte((Number)this.get(key));
	}
	
	public String getString(Object key)
	{
		return (String) this.get(key);
	}
	
	public JSONObject getJSONObject(Object key)
	{
		return (JSONObject)this.get(key);
	}
	
	public JSONArray getJSONArray(Object key)
	{
		return (JSONArray)this.get(key);
	}
}
