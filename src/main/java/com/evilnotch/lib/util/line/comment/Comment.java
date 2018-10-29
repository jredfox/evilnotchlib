package com.evilnotch.lib.util.line.comment;

import com.evilnotch.lib.util.line.util.LineUtil;

public class Comment implements ICommentAttatch{
	
	public char cStart;
	public String comment;
	public boolean attatched;
	/**
	 * the initial line index it's parsed upon won't be updated nor synced
	 */
	public int index;
	
	/**
	 * simple constructor for non attatcheable comments
	 */
	public Comment(String comment){
		this(LineUtil.commentDefault,comment);
	}
	public Comment(char c,String comment){
		this(c,comment,-1);
	}
	
	public Comment(char c,String comment,int index)
	{
		this(c,comment,false,index);
	}
	public Comment(char c,String comment,boolean attatched,int index)
	{
		this.cStart = c;
		this.comment = comment.substring(comment.indexOf(c)+1, comment.length());
		this.attatched = attatched;
		this.index = index;
	}
	@Override
	public char getCommentStart()
	{
		return this.cStart;
	}
	@Override
	public String getComment() 
	{
		return this.comment;
	}
	@Override
	public boolean isAttatched() {
		return this.attatched;
	}
	@Override
	public int getTmpIndex() {
		return this.index;
	}
	@Override
	public void setTmpIndex(int i) {
		this.index = i;
	}
	
	@Override
	public String toString()
	{
		return this.cStart + this.comment;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Comment))
			return false;
		Comment c = (Comment)obj;
		return this.cStart == c.cStart && this.comment.equals(c.comment) && this.attatched == c.attatched;
	}
}
