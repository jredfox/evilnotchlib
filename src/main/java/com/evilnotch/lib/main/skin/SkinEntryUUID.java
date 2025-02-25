package com.evilnotch.lib.main.skin;

import org.ralleytn.simple.json.JSONObject;

import com.evilnotch.lib.util.JavaUtil;

public class SkinEntryUUID extends SkinEntry {
	
	public SkinEntryUUID(String uuid, String username)
	{
		super(uuid, username, System.currentTimeMillis(), "", "", "", "");
	}
	
	@Override
	public JSONObject encodeJSON()
	{
		JSONObject json = new JSONObject();
		json.put("profileId", this.uuid);
		json.put("profileName", this.user);
		return json;
	}
	
	public String encode()
	{
		return JavaUtil.toBase64(this.encodeJSON().toString());
	}
}
