package com.EvilNotch.lib.minecraft.content;

public class SkinData {
	
	public String uuid;
	public String value;
	public String signature;
	public String username;
	
	public SkinData(String u,String v, String s,String n)
	{
		this.uuid = u;
		this.value = v;
		this.signature = s;
		this.username = n;
	}
	
	public SkinData(String u, String[] p,String n) {
		this.uuid = u;
		this.value = p[0];
		this.signature = p[1];
		this.username = n;
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
