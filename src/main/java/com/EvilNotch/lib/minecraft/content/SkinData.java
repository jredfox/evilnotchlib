package com.EvilNotch.lib.minecraft.content;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.EvilNotch.lib.util.JavaUtil;

public class SkinData {
	
	public String uuid;
	public String value;
	public String signature;
	public String username;
	protected JSONObject valueJson;
	
	public SkinData(String u,String v, String s,String n)
	{
		this.uuid = u;
		this.value = v;
		this.signature = s;
		this.username = n;
		decompile();
	}

	public SkinData(String u, String[] p,String n) {
		this.uuid = u;
		this.value = p[0];
		this.signature = p[1];
		this.username = n;
		decompile();
	}
	
	public void setValue(String s){
		this.value = s;
		decompile();
	}
	public void setValue(JSONObject json){
		this.valueJson = json;
		recompile();
	}
	public JSONObject getJSON()
	{
		return (JSONObject) this.valueJson.copy();
	}

	public void recompile() {
		this.value = Base64.encodeBase64String(this.valueJson.toJSONString().getBytes());
	}
	public void decompile() {
		this.valueJson = JavaUtil.toJsonFrom64(this.value);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof SkinData))
			return false;
		SkinData other = (SkinData)obj;
		return this.uuid.equals(other.uuid);
	}
}
