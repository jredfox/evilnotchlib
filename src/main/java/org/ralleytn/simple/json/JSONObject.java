/*
 * $Id: JSONObject.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package org.ralleytn.simple.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import org.ralleytn.simple.json.internal.Util;

import com.evilnotch.lib.util.simple.ICopy;

/**
 * A JSON object. Key value pairs are ordered. JSONObject supports java.util.Map interface.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 * @author jredfox -fixes<dragonofthelakeabcd@gmail.com>
 */
public class JSONObject extends JSONMap implements ICopy {
	
	private static final long serialVersionUID = -503443796854799292L;
	
	/**
	 * Constructs an empty {@linkplain JSONObject}
	 * @since 1.0.0
	 */
	public JSONObject() {}

	/**
	 * Allows creation of a {@linkplain JSONObject} from a {@linkplain Map}. After that, both the
	 * generated {@linkplain JSONObject} and the {@linkplain Map} can be modified independently.
	 * @param map the {@linkplain Map} from which the {@linkplain JSONObject} should be created
	 * @since 1.0.0
	 */
	public JSONObject(Map map) 
	{
		super(map);
	}
	
	public JSONObject(int capcity)
	{
		super(capcity);
	}
	
	/**
	 * Constructs a {@linkplain JSONObject} from JSON data.
	 * @param json the JSON data
	 * @throws JSONParseException if the JSON data is invalid
	 * @since 1.0.0
	 */
	public JSONObject(String json) throws JSONParseException {
		
		super(new JSONParser().parseJSONObject(json));
	}
	
	/**
	 * Constructs a {@linkplain JSONObject} with JSON data from a {@linkplain Reader}.
	 * @param reader the {@linkplain Reader} with the JSON data
	 * @throws IOException if an I/O error occurred
	 * @throws JSONParseException if the JSON is invalid
	 * @since 1.0.0
	 */
	public JSONObject(Reader reader) throws IOException, JSONParseException {
		
		super(new JSONParser().parseJSONObject(reader));
	}
	
	/**
	 * Writes this {@linkplain JSONObject} on a given {@linkplain Writer}.
	 * @param writer the {@linkplain Writer}
	 * @throws IOException if an I/O error occurred
	 * @since 1.0.0
	 */
	public void write(Writer writer) throws IOException {
		
		Util.write(this, writer);
	}
	
	/**
	 * @return a new {@linkplain JSONObject} without any {@code null} values
	 * @since 1.1.0
	 * @jredfox made java 7 compliant without recurse option. 
	 * @jredfox All JSONObject and JSONArray children will be copied to prevent unintended or undefined behavior
	 */
	public JSONObject compact() {
		
		JSONObject object = new JSONObject();
		for(Map.Entry<Object, Object> e : this.entrySet())
		{
			Object value = e.getValue();
			if(value != null)
				object.put(e.getKey(), value instanceof ICopy ? ((ICopy) value).copy() : value);
		}
		
		return object;
	}
	
	/**
	 * Removes any {@code null} values from the current JSONObject without recurse
	 * @jredfox added method to remove null keys without a need for a new JSONObject. Still no recurse support as interaction between JSONArray JSONObject are not easily explainable on how it should behave
	 */
	public void compactValues()
	{
		Iterator<Object> it = this.values().iterator();
		while(it.hasNext())
		{
			if(it.next() == null)
				it.remove();
		}
	}
	
	public void merge(JSONObject other)
	{
		for(java.util.Map.Entry<Object, Object> entry : other.entrySet())
		{
			String key = (String) entry.getKey();
			Object otherIndex = entry.getValue();
			Object thisIndex = this.get(key);
			if(thisIndex instanceof JSONObject && otherIndex instanceof JSONObject)
			{
				JSONObject json = (JSONObject) thisIndex;
				json.merge((JSONObject) otherIndex);
			}
			else if(thisIndex instanceof JSONArray && otherIndex instanceof JSONArray)
			{
				JSONArray arr = (JSONArray) thisIndex;
				arr.merge((JSONArray) otherIndex);
			}
			else
			{
				this.put(key, otherIndex instanceof ICopy ? ((ICopy) otherIndex).copy() : otherIndex);
			}
		}
	}

	@Override
	public boolean equals(Object o) 
	{
		return o instanceof JSONObject && this.size() == ((JSONObject)o).size() && super.equals(o);
	}
	
	@Override
	public JSONObject copy()
	{
		JSONObject json = new JSONObject(this.size() + 3);
		for(java.util.Map.Entry<Object, Object> entry : this.entrySet())
		{
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			if(value instanceof ICopy)
				json.put(key, ((ICopy) value).copy());
			else
				json.put(key, value);
		}
		return json;
	}
	
	/**
	 * @return a {@linkplain String} representation of this {@linkplain JSONObject}.
	 * @since 1.0.0
	 */
	@Override
	public String toString() {
		
		try(StringWriter writer = new StringWriter()) {
			
			Util.write(this, writer);
			return writer.toString();
			
		} catch(IOException exception) {

			exception.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @param rootName the name of the root element
	 * @return this JSON Object in XML
	 * @since 1.1.0
	 * @jredfox made java 7 compliant
	 */
	public String toXML(String rootName) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append('<');
		builder.append(rootName);
		builder.append('>');
		
		for (Map.Entry<Object, Object> e : this.entrySet()) {
			Object key = e.getKey();
			Object value = e.getValue();

			if (value instanceof JSONObject) {
				builder.append(((JSONObject) value).toXML(key.toString()));
			} else if (value instanceof JSONArray) {
				builder.append(((JSONArray) value).toXML(key.toString()));
			} else {

				builder.append('<');
				builder.append(key);
				builder.append('>');

				if (value != null) {

					builder.append(String.valueOf(value));
				}

				builder.append("</");
				builder.append(key);
				builder.append('>');
			}
		}
		
		builder.append("</");
		builder.append(rootName);
		builder.append('>');
		
		return builder.toString();
	}

}
