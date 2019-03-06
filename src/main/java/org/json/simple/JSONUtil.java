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
	 * fixes any objects before inserting them into a json. Doesn't support static or dynamic arrays
	 */
	public static Object getValidJsonValue(Object value) 
	{
		if(value instanceof Object[] || value instanceof long[] || value instanceof int[] || value instanceof short[] || value instanceof byte[] || value instanceof double[] || value instanceof float[] || value instanceof boolean[] || value instanceof char[])
			throw new IllegalArgumentException("Use JSONArray Class For Array Objects");
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

}
