package com.evilnotch.lib.main.skin;

import java.util.HashMap;
import java.util.Map;

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
	public String elytra;//elytra can have it's own texture separate from the cape texture
	public Map<String, String> meta;
	public boolean isEmpty;
	
	public SkinEntry(String uuid, String username, long cacheTime, String skin, String cape, String model, String elytra)
	{
		this(uuid, username, cacheTime, skin, cape, model, elytra, null);
	}
	
	public SkinEntry(String uuid, String username, long cacheTime, String skin, String cape, String model, String elytra, JSONObject jmeta)
	{
		this.uuid = uuid.replace("-", "");
		this.user = username != null ? username.toLowerCase() : "";
		this.isEmpty = this.uuid.isEmpty() && this.user.isEmpty();
		this.cacheTime = cacheTime;
		this.skin =   JavaUtil.safeString(skin);
		this.cape =   JavaUtil.safeString(cape);
		this.model =  JavaUtil.safeString(model);
		this.elytra = JavaUtil.safeString(elytra);
		if(jmeta != null)
		{
			this.meta = new HashMap(jmeta.size() + 2);
			for(Object k : jmeta.keySet())
			{
				String key = (String) k;
				if(key.equals("model"))
					continue;//Skip default vanilla model
				this.meta.put(key, jmeta.getString(key));
			}
		}
		else
			this.meta = new HashMap(1);
	}
	
	public SkinEntry(JSONObject json)
	{
		this(json.getString("uuid"), json.getString("user"), json.getLong("cacheTime"), json.getString("skin"),  json.getString("cape"), json.getString("model"), json.getString("elytra"), json.getJSONObject("meta"));
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
		json.put("elytra", this.elytra);
		if(!this.meta.isEmpty())
		{
			JSONObject meta = new JSONObject();
			for(Map.Entry<String, String> p : this.meta.entrySet())
			{
				String key = p.getKey();
				if(key.equals("model"))
					continue;//Skip default vanilla model
				meta.put(key, p.getValue());
			}
			json.put("meta", meta);
		}
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
			if(jskin == null)
			{
				jskin = new JSONObject();
				jskin.put("url", "");
				textures.put("SKIN", jskin);
			}
			JSONObject jmeta = jskin.getJSONObject("metadata");
			String skin = jskin.getString("url");
			String model = jmeta != null ? jmeta.getString("model") : "";
			String cape = textures.containsKey("CAPE") ? textures.getJSONObject("CAPE").getString("url") : "";
			String elytra = textures.containsKey("ELYTRA") ? textures.getJSONObject("ELYTRA").getString("url") : "";
			SkinEntry entry = new SkinEntry(uuid, user, System.currentTimeMillis(), skin, cape, model, elytra, jmeta);
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
		SkinEntry e = new SkinEntry(uuid, user, System.currentTimeMillis(), "", "", "", "");
		e.isEmpty = true;
		return e;
	}
	
	/**
	 * Do not modify the skin cache itself instead modify a copy for say like a custom cape override
	 */
	@Override
	public SkinEntry copy()
	{
		SkinEntry s = new SkinEntry(this.uuid, this.user, this.cacheTime, this.skin, this.cape, this.model, this.elytra);
		s.isEmpty = this.isEmpty;
		s.meta.putAll(this.meta);
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
		
		//add the player model seems to only support alex (slim)
		JSONObject meta = new JSONObject();
		//add all custom metadata into the payload
		for(Map.Entry<String, String> entry : this.meta.entrySet())
			meta.put(entry.getKey(), entry.getValue());
		//add the player model seems to only support alex (slim)
		if(this.hasModel())
			meta.put("model", this.model);
		//Apply the metadata to the JSON only if it's not empty
		if(!meta.isEmpty())
			jskin.put("metadata", meta);
		
		jskin.put("url", this.skin);
		textures.put("SKIN", jskin);
		
		if(!this.cape.isEmpty())
		{
			JSONObject jcape = new JSONObject();
			jcape.put("url", this.cape);
			textures.put("CAPE", jcape);
		}
		
		if(!this.elytra.isEmpty())
		{
			JSONObject jelytra = new JSONObject();
			jelytra.put("url", this.elytra);
			textures.put("ELYTRA", jelytra);
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
