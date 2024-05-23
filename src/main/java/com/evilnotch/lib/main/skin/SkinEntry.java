package com.evilnotch.lib.main.skin;

import org.ralleytn.simple.json.JSONObject;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.ICopy;

public class SkinEntry implements ICopy {
	
	public static final String EMPTY_SKIN_ENCODE = "eyJ0aW1lc3RhbXAiOjE3MTYwMTgzNDU4MjksInByb2ZpbGVJZCI6IjQ4YjliMzAwYTM1NzM2N2ViNjZhYThmOGZlZGRlNGNmIiwicHJvZmlsZU5hbWUiOiIkTlVMTCIsInNpZ25hdHVyZVJlcXVpcmVkIjpmYWxzZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiIifX19";
	
	public String uuid;
	public String user;
	public long cacheTime;
	public String skin;
	public String cape;
	public boolean isEmpty;
	
	public SkinEntry(String uuid, String username, long cacheTime, String skin, String cape)
	{
		this.uuid = uuid.replace("-", "");
		this.user = username.toLowerCase();
		this.isEmpty = this.uuid.isEmpty() && this.user.isEmpty();
		this.cacheTime = cacheTime;
		this.skin = skin;
		this.cape = JavaUtil.safeString(cape);
	}
	
	public SkinEntry(JSONObject json)
	{
		this(json.getString("uuid"), json.getString("user"), json.getLong("cacheTime"), json.getString("skin"),  json.getString("cape"));
	}
	
	public JSONObject serialize()
	{
		JSONObject json = new JSONObject();
		json.put("uuid", this.uuid);
		json.put("user", this.user);
		json.put("cacheTime", this.cacheTime);
		json.put("skin", this.skin);
		json.put("cape", this.cape);
		return json;
	}
	
	/**
	 * Do not modify the skin cache itself instead modify a copy for say like a custom cape override
	 */
	@Override
	public SkinEntry copy()
	{
		return new SkinEntry(this.uuid, this.user, this.cacheTime, this.skin, this.cape);
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
		
		if(!this.cape.isEmpty())
		{
			JSONObject jcape = new JSONObject();
			jcape.put("url", this.cape);
			textures.put("CAPE", jcape);
		}
		
		return json;
	}
	
	public String encode()
	{
		return !this.isEmpty ? JavaUtil.toBase64(encodeJSON().toString()) : EMPTY_SKIN_ENCODE;
	}
	
}
