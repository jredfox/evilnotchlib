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
	public String model;//1.12.2 and below seems to only support alex empty or default
	public boolean isEmpty;
	
	public SkinEntry(String uuid, String username, long cacheTime, String skin, String cape, String model)
	{
		this.uuid = uuid.replace("-", "");
		this.user = username.toLowerCase();
		this.isEmpty = this.uuid.isEmpty() && this.user.isEmpty();
		this.cacheTime = cacheTime;
		this.skin =  JavaUtil.safeString(skin);
		this.cape =  JavaUtil.safeString(cape);
		this.model = JavaUtil.safeString(model);
	}
	
	public SkinEntry(JSONObject json)
	{
		this(json.getString("uuid"), json.getString("user"), json.getLong("cacheTime"), json.getString("skin"),  json.getString("cape"), json.getString("model"));
	}
	
	public JSONObject serialize()
	{
		JSONObject json = new JSONObject();
		json.put("uuid", this.uuid);
		json.put("user", this.user);
		json.put("cacheTime", this.cacheTime);
		json.put("skin", this.skin);
		json.put("cape", this.cape);
		json.put("model", this.model);
		return json;
	}
	
	public static SkinEntry fromPayload(String uuid, String user, String base64payload)
	{
		if(SkinCache.isSkinEmpty(base64payload))
			return SkinEntry.emptySkin(uuid, user);
		
		try
		{
			JSONObject decoded = JavaUtil.toJsonFrom64(base64payload);
			JSONObject textures = decoded.getJSONObject("textures");
			JSONObject jskin = textures.getJSONObject("SKIN");
			String skin = jskin.getString("url");
			String model = jskin.containsKey("metadata") ? jskin.getJSONObject("metadata").getString("model") : "";
			String cape = textures.containsKey("CAPE") ? textures.getJSONObject("CAPE").getString("url") : "";
			SkinEntry entry = new SkinEntry(uuid, user, System.currentTimeMillis(), skin, cape, model);
			return entry;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return SkinEntry.emptySkin(uuid, user);
	}
	
	public static SkinEntry emptySkin(String uuid, String user)
	{
		return new SkinEntry(uuid, user, System.currentTimeMillis(), "", "", "");
	}
	
	/**
	 * Do not modify the skin cache itself instead modify a copy for say like a custom cape override
	 */
	@Override
	public SkinEntry copy()
	{
		SkinEntry s = new SkinEntry(this.uuid, this.user, this.cacheTime, this.skin, this.cape, this.model);
		s.isEmpty = this.isEmpty;
		return s;
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
		//add the player model seems to only support alex
		if(this.hasModel())
		{
			JSONObject meta = new JSONObject();
			meta.put("model", this.model);
			jskin.put("metadata", meta);
		}
		jskin.put("url", this.skin);
		textures.put("SKIN", jskin);
		
		if(!this.cape.isEmpty())
		{
			JSONObject jcape = new JSONObject();
			jcape.put("url", this.cape);
			textures.put("CAPE", jcape);
		}
		
		return json;
	}
	
	public boolean hasModel() 
	{
		return !this.model.isEmpty();
	}

	public String encode()
	{
		return !this.isEmpty ? JavaUtil.toBase64(this.encodeJSON().toString()) : "";
	}
	
}
