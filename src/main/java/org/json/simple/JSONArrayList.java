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
	
	public JSONArrayList(int capacity)
	{
		super(capacity);
	}

	@Override
	public boolean add(Object obj)
	{
		obj = JSONUtil.getValidJsonValue(obj);
		return super.add(obj);
	}
	
	@Override
	public void add(int index, Object obj)
	{
		obj = JSONUtil.getValidJsonValue(obj);
		super.add(index, obj);
	}
	
	@Override
	public boolean addAll(Collection map)
	{
		map = (Collection) JSONUtil.getValidJsonValue(map);
		return super.addAll(map);
	}
	
	@Override
	public boolean addAll(int index, Collection map)
	{
		map = (Collection) JSONUtil.getValidJsonValue(map);
		return super.addAll(index, map);
	}
	
	public Long getLong(int key)
	{
		return JavaUtil.getLong((Number)this.get(key));
	}

	public Integer getInt(int key)
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
	
	public Double getDouble(int key)
	{
		return JavaUtil.getDouble((Number)this.get(key));
	}
	
	public Float getFloat(int key)
	{
		return JavaUtil.getFloat((Number)this.get(key));
	}
	
	public boolean getBoolean(int key)
	{
		return (boolean) this.get(key);
	}
	
	public char getChar(int key)
	{
		return this.getString(key).charAt(0);
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
