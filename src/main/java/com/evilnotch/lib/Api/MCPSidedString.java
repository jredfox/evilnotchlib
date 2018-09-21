package com.evilnotch.lib.Api;

import com.evilnotch.lib.asm.FMLCorePlugin;

public class MCPSidedString {
	
	public String deob;
	public String ob;
	
	/**
	 * self map your string here
	 */
	public MCPSidedString(String deob,String ob)
	{
		this.deob = deob;
		this.ob = ob;
	}
	
	public String getSidedString(){
		return FMLCorePlugin.isObf ? this.ob : this.deob;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof MCPSidedString))
			return false;
		MCPSidedString compare = (MCPSidedString)obj;
		return this.deob.equals(compare.deob) && this.ob.equals(compare.ob);
	}
	/**
	 * is deob hash since ob can have dupe names
	 */
	@Override
	public int hashCode()
	{
		return this.deob.hashCode() + this.ob.hashCode();
	}
	
	@Override
	public String toString()
	{
		return this.getSidedString();
	}
	public String getDisplay(){return this.deob + "=" + this.ob;}
}
