package com.evilnotch.lib.main.skin;

import org.ralleytn.simple.json.JSONObject;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.ICopy;

public class SkinEntry implements ICopy {
	
	public String uuid;
	public String user;
	public long cacheTime;
	public String skin;
	public String cape;
	public String skinhash;
	public String capehash;
	
	public SkinEntry(String uuid, String username, long cacheTime, String skin, String cape, String skinmd5, String capemd5)
	{
		this.uuid = uuid.replace("-", "");
		this.user = username.toLowerCase();
		this.cacheTime = cacheTime;
		this.skin = skin;
		this.cape = cape;
		this.skinhash = skinmd5.toLowerCase();
		this.capehash = capemd5.toLowerCase();
	}
	
	public SkinEntry(JSONObject json)
	{
		this(json.getString("uuid"), json.getString("user"), json.getLong("cacheTime"), json.getString("skin"),  json.getString("cape"), json.getString("skinhash"), json.getString("capehash"));
	}
	
	public JSONObject serialize()
	{
		JSONObject json = new JSONObject();
		json.put("uuid", this.uuid);
		json.put("user", this.user);
		json.put("cacheTime", this.cacheTime);
		json.put("skin", this.skin);
		json.put("cape", this.cape);
		json.put("skinhash", this.skinhash);
		json.put("capehash", this.capehash);
		return json;
	}
	
	/**
	 * Do not modify the skin cache itself instead modify a copy for say like a custom cape override
	 */
	@Override
	public SkinEntry copy()
	{
		return new SkinEntry(this.uuid, this.user, this.cacheTime, this.skin, this.cape, this.skinhash, this.capehash);
	}
	
	public JSONObject encodeJSON()
	{
		JSONObject json = new JSONObject();
		json.put("timestamp", System.currentTimeMillis());
		json.put("profileId", this.uuid);
		json.put("profileName", this.user);
		json.put("signatureRequired", false);
		
		JSONObject textures = new JSONObject();
		json.put("textures", textures);
		
		JSONObject jskin = new JSONObject();
		jskin.put("url", this.skin);
		textures.put("SKIN", jskin);
		
		JSONObject jcape = new JSONObject();
		jcape.put("url", this.cape);
		textures.put("CAPE", jcape);
		
		return json;
	}
	
	public String encode()
	{
		return JavaUtil.toBase64(encodeJSON().toString());
	}
	
}
