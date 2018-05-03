package com.EvilNotch.lib.minecraft.content;

public class LangEntry {
	
	public String langDisplayName = null;
	public String langType = null;
	public String langId = null;
	
	/**
	 * use this one if your manually calling it for advanced constructors in basic items/blocks
	 */
	public LangEntry(String display,String langType){
		this.langDisplayName = display;
		this.langType = langType;
	}
	
	/**
	 * Don't use this one if your using the advanced constructor on basic items/blocks as it auto fills in the id of the lang
	 */
	public LangEntry(String display,String langType,String langid){
		this.langDisplayName = display;
		this.langType = langType;
		this.langId = langid;
	}
	
	@Override
	public String toString(){return this.langId + "=" + this.langDisplayName + " Lang:" + this.langType;}
	
	public String getString(){return this.langId + "=" + this.langDisplayName;}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof LangEntry))
			return false;
		LangEntry lang = (LangEntry)obj;
		return this.langId.equals(lang.langId) && this.langType.equals(lang.langType);
	}

}
