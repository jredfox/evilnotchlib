package com.evilnotch.lib.minecraft.basicmc.auto.lang;

import net.minecraft.util.ResourceLocation;

public class LangEntry {
	
	public String langDisplayName = null;
	public String region = null;
	public String langId = null;
	public String meta = null;
	public ResourceLocation loc = null;
	
	public static final String en_us = "en_us";
	
	/**
	 * use this one if your manually calling it for advanced constructors in basic items/blocks
	 */
	public LangEntry(String region, String display)
	{
		this.region = region;
		this.langDisplayName = display;
	}
	
	public LangEntry(String region, String display, String meta)
	{
		this.region = region;
		this.langDisplayName = display;
		this.meta = meta;
	}
	
	@Override
	public String toString()
	{
		return this.getString();
	}
	
	public String getString()
	{
		return this.langId + "=" + this.langDisplayName;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LangEntry))
			return false;
		LangEntry lang = (LangEntry)obj;
		return this.langId.equals(lang.langId) && this.region.equals(lang.region);
	}

}
