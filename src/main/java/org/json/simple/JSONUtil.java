package org.json.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.evilnotch.lib.util.JavaUtil;

public class JSONUtil {

	/**
	 * fixes any objects before inserting them into a json. Doesn't support static or dynamic arrays
	 */
	public static Object getValidJsonValue(Object value) 
	{
		if(JavaUtil.isStaticArray(value))
			throw new IllegalArgumentException("Use JSONArray Objects for non primitive values");
		else if(value instanceof Map && !(value instanceof JSONObject) || value instanceof Collection && !(value instanceof JSONArray))
			throw new IllegalArgumentException("Inserted Maps must be JSONObject and Inserted Collections Must be JSONArray");
		
		return canPut(value) ? value : value.toString();
	}
	
	/**
	 * can the object without modifications be inputted into the json object/json array
	 */
	public static boolean canPut(Object value) 
	{
		return value == null || value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray;
	}
	
	public static JSONArray getJSONArray(Object[] value)
	{
		JSONArray json = new JSONArray();
		for(Object obj : value)
		{
			json.add(JSONUtil.getValidJsonValue(obj));
		}
		return json;
	}
	
	public static JSONArray getJSONArray(long[] value)
	{
		JSONArray json = new JSONArray();
		for(long obj : value)
		{
			json.add(obj);
		}
		return json;
	}
	
	public static JSONArray getJSONArray(int[] value)
	{
		JSONArray json = new JSONArray();
		for(int obj : value)
		{
			json.add(obj);
		}
		return json;
	}
	
	public static JSONArray getJSONArray(short[] value)
	{
		JSONArray json = new JSONArray();
		for(short obj : value)
		{
			json.add(obj);
		}
		return json;
	}
	
	public static JSONArray getJSONArray(byte[] value)
	{
		JSONArray json = new JSONArray();
		for(byte obj : value)
		{
			json.add(obj);
		}
		return json;
	}
	
	public static JSONArray getJSONArray(double[] value)
	{
		JSONArray json = new JSONArray();
		for(double obj : value)
		{
			json.add(obj);
		}
		return json;
	}
	
	public static JSONArray getJSONArray(float[] value)
	{
		JSONArray json = new JSONArray();
		for(float obj : value)
		{
			json.add(obj);
		}
		return json;
	}
	
	public static JSONArray getJSONArray(boolean[] value)
	{
		JSONArray json = new JSONArray();
		for(boolean obj : value)
		{
			json.add(obj);
		}
		return json;
	}
	
	public static JSONArray getJSONArray(char[] value)
	{
		JSONArray json = new JSONArray();
		for(char obj : value)
		{
			json.add("" + obj);
		}
		return json;
	}
	
	public static String[] getStringArray(JSONArray json)
	{
		String[] value = new String[json.size()];
		for(int index=0;index<json.size();index++)
		{
			value[index] = json.getString(index);
		}
		return value;
	}
	
	public static long[] getLongArray(JSONArray arr)
	{
		long[] value = new long[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getLong(index);
		}
		return value;
	}
	
	public static int[] getIntArray(JSONArray arr)
	{
		int[] value = new int[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getInt(index);
		}
		return value;
	}

	public static short[] getShortArray(JSONArray arr) 
	{
		short[] value = new short[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getShort(index);
		}
		return value;
	}

	public static byte[] getByteArray(JSONArray arr) 
	{
		byte[] value = new byte[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getByte(index);
		}
		return value;
	}

	public static double[] getDoubleArray(JSONArray arr) 
	{
		double[] value = new double[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getDouble(index);
		}
		return value;
	}

	public static float[] getFloatArray(JSONArray arr)
	{
		float[] value = new float[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getFloat(index);
		}
		return value;
	}

	public static boolean[] getBooleanArray(JSONArray arr) 
	{
		boolean[] value = new boolean[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getBoolean(index);
		}
		return value;
	}

	public static char[] getCharArray(JSONArray arr) 
	{
		char[] value = new char[arr.size()];
		for(int index=0;index<arr.size();index++)
		{
			value[index] = arr.getChar(index);
		}
		return value;
	}
}
