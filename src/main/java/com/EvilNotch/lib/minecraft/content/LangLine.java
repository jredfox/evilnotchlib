package com.EvilNotch.lib.minecraft.content;

import com.EvilNotch.lib.util.ICopy;
import com.EvilNotch.lib.util.Line.ILine;
import com.EvilNotch.lib.util.Line.LineBase;

import net.minecraft.util.ResourceLocation;

public class LangLine implements ILine {
	
	public String id;
	public String head;

	public LangLine(String strline) {
		parse(strline,' ',' ');
	}
	public LangLine(String id,String head){
		this.id = id;
		this.head = head;
	}

	public LangLine(LangEntry lang) {
		this.id = lang.langId;
		this.head = lang.langDisplayName;
	}
	@Override
	public int compareTo(Object o) {
		return this.getComparible().compareTo( ((ILine)o).getComparible() );
	}

	@Override
	public ICopy copy() {
		return new LangLine(this.id,this.head);
	}
	
	@Override
	public boolean equals(Object obj){
		return equals(obj,false);
	}

	@Override
	public boolean equals(Object obj, boolean compareHead) {
		if(!(obj instanceof LangLine))
			return false;
		LangLine compare = (LangLine)obj;
		boolean id = this.id.equals(compare.id);
		boolean display = compareHead ? this.head.equals(compare.head) : true;
		return id && display;
	}

	@Override
	public String getString() {
		String compare = "" + this.id;
		if(id.equals("null"))
			compare = "";
		return compare + "=" + this.head;
	}

	@Override
	public String getComparible() {
		return this.getModPath();
	}

	@Override
	public String getModPath() {
		return this.id;
	}

	@Override
	public ResourceLocation getResourceLocation() {
		return new ResourceLocation("minecraft:unsupported");
	}

	@Override
	public void parse(String str, char sep, char q, char... invalid) {
		String[] section = LineBase.getParts(str, "=");
		this.head = section[1].trim();
		this.id = section[0];
	}

	@Override
	public String getModid() {
		return this.id;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public ILine getLineBase() {
		return this;
	}

	@Override
	public Object getHead() {
		return this.head;
	}

	@Override
	public boolean hasHead() {
		return this.getHead() != null;
	}

	@Override
	public char getSeperator() {
		return ' ';
	}

	@Override
	public char getQuote() {
		return ' ';
	}

	@Override
	public String getInvalid() {
		return "";
	}
	
	@Override 
	public String toString(){return this.getString();}

}
