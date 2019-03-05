package org.json.simple;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.evilnotch.lib.util.JavaUtil;

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
		JSONUtil.fixMap(map);
		return map;
	}

	@Override
	public Object put(Object key, Object value)
	{
		value = JSONUtil.getValidJsonValue(value);
		return super.put(key, value);
	}

	@Override
	public Object putIfAbsent(Object key, Object value)
	{
		value = JSONUtil.getValidJsonValue(value);
		return super.putIfAbsent(key, value);
	}
	
	@Override
	public void putAll(Map map)
	{
		if(map.isEmpty())
			return;
		JSONUtil.fixMap(map);
		super.putAll(map);
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
