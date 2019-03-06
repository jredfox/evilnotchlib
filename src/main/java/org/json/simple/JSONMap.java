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
	
	public JSONMap(int capacity)
	{
		super(capacity);
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
		map = (Map) JSONUtil.getValidJsonValue(map);
		super.putAll(map);
	}
	
	public Long getLong(Object key)
	{
		return JavaUtil.getLong((Number)this.get(key));
	}

	public Integer getInt(Object key)
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
	
	public Double getDouble(Object key)
	{
		return JavaUtil.getDouble((Number)this.get(key));
	}
	
	public Float getFloat(Object key)
	{
		return JavaUtil.getFloat((Number)this.get(key));
	}
	
	public boolean getBoolean(Object key)
	{
		return (boolean) this.get(key);
	}
	
	public char getChar(Object key)
	{
		return this.getString(key).charAt(0);
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
	
	/**
	 * @param non primitive object arrays are converted into valid json arrays no recursion this converts data into one JSONArray
	 */
	public Object putStaticArray(String key, Object[] value)
	{
		JSONArray json = new JSONArray();
		for(Object obj : value)
		{
			json.add(JSONUtil.getValidJsonValue(obj));
		}
		return this.put(key, json);
	}
	
	public Object putStaticArray(String key, long[] value)
	{
		JSONArray json = new JSONArray();
		for(long obj : value)
		{
			json.add(obj);
		}
		return this.put(key, json);
	}
	
	public Object putStaticArray(String key, int[] value)
	{
		JSONArray json = new JSONArray();
		for(int obj : value)
		{
			json.add(obj);
		}
		return this.put(key, json);
	}
	
	public Object putStaticArray(String key, short[] value)
	{
		JSONArray json = new JSONArray();
		for(short obj : value)
		{
			json.add(obj);
		}
		return this.put(key, json);
	}
	
	public Object putStaticArray(String key, byte[] value)
	{
		JSONArray json = new JSONArray();
		for(byte obj : value)
		{
			json.add(obj);
		}
		return this.put(key, json);
	}
	
	public Object putStaticArray(String key, double[] value)
	{
		JSONArray json = new JSONArray();
		for(double obj : value)
		{
			json.add(obj);
		}
		return this.put(key, json);
	}
	
	public Object putStaticArray(String key, float[] value)
	{
		JSONArray json = new JSONArray();
		for(float obj : value)
		{
			json.add(obj);
		}
		return this.put(key, json);
	}
	
	public Object putStaticArray(String key, boolean[] value)
	{
		JSONArray json = new JSONArray();
		for(boolean obj : value)
		{
			json.add(obj);
		}
		return this.put(key, json);
	}
	
	public Object putStaticArray(String key, char[] value)
	{
		JSONArray json = new JSONArray();
		for(char obj : value)
		{
			json.add("" + obj);
		}
		return this.put(key, json);
	}
	
	/**
	 * fetches the json array and converts back to static array. This is unoptimized only call it when needed
	 */
	public String[] getStringArray(String key)
	{
		JSONArray arr = this.getJSONArray(key);
		String[] value = new String[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getString(index);
		}
		return value;
	}
	
	public long[] getLongArray(String key)
	{
		JSONArray arr = this.getJSONArray(key);
		long[] value = new long[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getLong(index);
		}
		return value;
	}
	
	public int[] getIntArray(String key)
	{
		JSONArray arr = this.getJSONArray(key);
		int[] value = new int[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getInt(index);
		}
		return value;
	}
	
	public short[] getShortArray(String key)
	{
		JSONArray arr = this.getJSONArray(key);
		short[] value = new short[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getShort(index);
		}
		return value;
	}
	
	public byte[] getByteArray(String key)
	{
		JSONArray arr = this.getJSONArray(key);
		byte[] value = new byte[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getByte(index);
		}
		return value;
	}
	
	public double[] getDoubleArray(String key)
	{
		JSONArray arr = this.getJSONArray(key);
		double[] value = new double[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getDouble(index);
		}
		return value;
	}
	
	public float[] getFloatArray(String key)
	{
		JSONArray arr = this.getJSONArray(key);
		float[] value = new float[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getFloat(index);
		}
		return value;
	}
	
	public boolean[] getBooleanArray(String key)
	{
		JSONArray arr = this.getJSONArray(key);
		boolean[] value = new boolean[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getBoolean(index);
		}
		return value;
	}
	
	public char[] getCharArray(String key)
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
