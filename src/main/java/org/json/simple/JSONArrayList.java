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
	
	/**
	 * @param non primitive object arrays are converted into valid json arrays no recursion this converts data into one JSONArray
	 */
	public boolean putStaticArray(Object[] value)
	{
		JSONArray json = new JSONArray();
		for(Object obj : value)
		{
			json.add(JSONUtil.getValidJsonValue(obj));
		}
		return this.add(json);
	}
	
	public boolean putStaticArray(long[] value)
	{
		JSONArray json = new JSONArray();
		for(long obj : value)
		{
			json.add(obj);
		}
		return this.add(json);
	}
	
	public boolean putStaticArray(int[] value)
	{
		JSONArray json = new JSONArray();
		for(int obj : value)
		{
			json.add(obj);
		}
		return this.add(json);
	}
	
	public boolean putStaticArray(short[] value)
	{
		JSONArray json = new JSONArray();
		for(short obj : value)
		{
			json.add(obj);
		}
		return this.add(json);
	}
	
	public boolean putStaticArray(byte[] value)
	{
		JSONArray json = new JSONArray();
		for(byte obj : value)
		{
			json.add(obj);
		}
		return this.add(json);
	}
	
	public boolean putStaticArray(double[] value)
	{
		JSONArray json = new JSONArray();
		for(double obj : value)
		{
			json.add(obj);
		}
		return this.add(json);
	}
	
	public boolean putStaticArray(float[] value)
	{
		JSONArray json = new JSONArray();
		for(float obj : value)
		{
			json.add(obj);
		}
		return this.add(json);
	}
	
	public boolean putStaticArray(boolean[] value)
	{
		JSONArray json = new JSONArray();
		for(boolean obj : value)
		{
			json.add(obj);
		}
		return this.add(json);
	}
	
	public boolean putStaticArray(char[] value)
	{
		JSONArray json = new JSONArray();
		for(char obj : value)
		{
			json.add("" + obj);
		}
		return this.add(json);
	}
	
	/**
	 * @param non primitive object arrays are converted into valid json arrays no recursion this converts data into one JSONArray
	 */
	public void putStaticArray(int index, Object[] value)
	{
		JSONArray json = new JSONArray();
		for(Object obj : value)
		{
			json.add(JSONUtil.getValidJsonValue(obj));
		}
		this.add(index, json);
	}
	
	public void putStaticArray(int index, long[] value)
	{
		JSONArray json = new JSONArray();
		for(long obj : value)
		{
			json.add(obj);
		}
		this.add(index, json);
	}
	
	public void putStaticArray(int index, int[] value)
	{
		JSONArray json = new JSONArray();
		for(int obj : value)
		{
			json.add(obj);
		}
		this.add(index, json);
	}
	
	public void putStaticArray(int index, short[] value)
	{
		JSONArray json = new JSONArray();
		for(short obj : value)
		{
			json.add(obj);
		}
		this.add(index, json);
	}
	
	public void putStaticArray(int index, byte[] value)
	{
		JSONArray json = new JSONArray();
		for(byte obj : value)
		{
			json.add(obj);
		}
		this.add(index, json);
	}
	
	public void putStaticArray(int index, double[] value)
	{
		JSONArray json = new JSONArray();
		for(double obj : value)
		{
			json.add(obj);
		}
		this.add(index, json);
	}
	
	public void putStaticArray(int index, float[] value)
	{
		JSONArray json = new JSONArray();
		for(float obj : value)
		{
			json.add(obj);
		}
		this.add(index, json);
	}
	
	public void putStaticArray(int index, boolean[] value)
	{
		JSONArray json = new JSONArray();
		for(boolean obj : value)
		{
			json.add(obj);
		}
		this.add(index, json);
	}
	
	public void putStaticArray(int index, char[] value)
	{
		JSONArray json = new JSONArray();
		for(char obj : value)
		{
			json.add("" + obj);
		}
		this.add(index, json);
	}
	
	/**
	 * fetches the json array and converts back to static array. This is unoptimized only call it when needed
	 */
	public String[] getStringArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		String[] value = new String[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getString(index);
		}
		return value;
	}
	
	public long[] getLongArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		long[] value = new long[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getLong(index);
		}
		return value;
	}
	
	public int[] getIntArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		int[] value = new int[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getInt(index);
		}
		return value;
	}
	
	public short[] getShortArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		short[] value = new short[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getShort(index);
		}
		return value;
	}
	
	public byte[] getByteArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		byte[] value = new byte[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getByte(index);
		}
		return value;
	}
	
	public double[] getDoubleArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		double[] value = new double[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getDouble(index);
		}
		return value;
	}
	
	public float[] getFloatArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		float[] value = new float[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getFloat(index);
		}
		return value;
	}
	
	public boolean[] getBooleanArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		boolean[] value = new boolean[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getBoolean(index);
		}
		return value;
	}
	
	public char[] getCharArray(int key)
	{
		JSONArray arr = this.getJSONArray(key);
		char[] value = new char[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getChar(index);
		}
		return value;
	}

}
