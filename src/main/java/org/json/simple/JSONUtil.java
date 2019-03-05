package org.json.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtil {
	
	/**
	 * Recursively converts the map to usable json objects before inputting into the super map
	 */
	public static void fixMap(Map map) 
	{
		Set<Map.Entry> set = map.entrySet();
		for(Map.Entry pair : set)
		{
			Object value = pair.getValue();
			
			if(!canPut(value))
				map.put(pair.getKey(), getValidJsonValue(value));
		}
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
			fixMap((Map)map);
			return map;
		}
		else if(map instanceof List)
		{
			List list = (List)map;
			int index = 0;
			for(Object obj : list)
			{
				if(!canPut(obj))
					list.set(index, getValidJsonValue(obj));
				index++;
			}
			return list;
		}
		
		//a generic method to always fix it no matter what interface is used by it
		List list = new ArrayList(map.size());
		for(Object obj : map)
		{
			if(!canPut(obj))
				obj = getValidJsonValue(obj);
			list.add(obj);
		}
		return list;
	}
	
	/**
	 * fixes any objects recursively into the maps/static arrays before use of the arrays
	 */
	public static Object getValidJsonValue(Object value) 
	{
		if(value instanceof Collection)
			return fixMap((Collection)value);
		else if(value instanceof Object[])
		{
			fixStaticArray((Object[])value);
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(Object obj : (Object[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else if(value instanceof long[])
		{
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(long obj : (long[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else if( value instanceof int[])
		{
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(int obj : (int[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else if( value instanceof short[])
		{
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(short obj : (short[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else if( value instanceof byte[])
		{
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(byte obj : (byte[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else if( value instanceof double[])
		{
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(double obj : (double[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else if( value instanceof float[])
		{
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(float obj : (float[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else if( value instanceof boolean[])
		{
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(boolean obj : (boolean[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		else if(value instanceof char[])
		{
			StringBuilder builder = new StringBuilder();
			builder.append('[');
			for(char obj : (char[])value)
			{
				builder.append((builder.length() > 0 ? ","  : "") + obj);
			}
			builder.append(']');
			JSONParser parser = new JSONParser();
			try 
			{
				return parser.parseJSONArray(builder.toString());
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
		return canPut(value) ? value : value.toString();
	}

	public static void fixStaticArray(Object[] list) 
	{
		int index = 0;
		for(Object obj : list)
		{
			Object iniObj = list[index];
			list[index] = getValidJsonValue(iniObj);
			index++;
		}
	}

	/**
	 * can the object without modifications be inputted into the json object/json array
	 */
	public static boolean canPut(Object value) 
	{
		return value == null || value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray;
	}

}
