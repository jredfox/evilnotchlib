package com.EvilNotch.lib.util.Line;

import com.EvilNotch.lib.util.ICopy;
import com.EvilNotch.lib.util.IObject;

public class Comment implements IObject{
    public int lineIndex = -1;
    public String comment = null;
    public ILine nearestLine = null;
    public boolean isAttactched = false;
    public char start = '#';
    
    public Comment(String comment)
    {
        this(0,comment);
    }
    public Comment(String comment,char c)
    {
        this(0,comment,false,c);
    }
    public Comment(int index, String comment)
    {
        this(index,comment,false);
    }
    public Comment(int index, String comment,boolean attatched)
    {
        this(index,comment,attatched,'#');
    }
    public Comment(int index, String comment,boolean attatched,char c)
    {
    	this(index,comment,attatched,c,null);
    }
    public Comment(int index, String comment,boolean attatched,char c,ILine nearLine)
    {
        this.start = c;
        this.lineIndex = index;
        //fix for when configs don't filter the comment strings properly or even the user
        if(LineBase.toWhiteSpaced(comment).indexOf(c) == 0)
        	comment = comment.substring(comment.indexOf(c)+1, comment.length());
        this.comment = comment;
        this.isAttactched = attatched;
        if(nearLine != null)
        	this.nearestLine = nearLine;//in case line changes keep memory location also more optimized
    }
    
    public boolean equals(Object obj,boolean compareIndex){
    	 if(!(obj instanceof Comment))
             return false;
         Comment c = (Comment)obj;
         boolean index = true;
         if(compareIndex)
        	 index = this.lineIndex == c.lineIndex;
         return index && this.comment.equals(c.comment) && this.start == c.start && this.isAttactched == c.isAttactched;
    }
    @Override
    public boolean equals(Object obj){
       return this.equals(obj, false);
    }
    
    @Override
    public String toString()
    {
        return "" + this.start + this.comment + ":" + this.lineIndex + " attatched:" + this.isAttactched;
    }
	@Override
	public ICopy copy() {
		return new Comment(this.lineIndex,this.comment,this.isAttactched,this.start,this.nearestLine);
	}
	@Override
	public int compareTo(Object arg0) {
		Comment other = (Comment)arg0;
		return this.getIndex().compareTo(other.getIndex());
	}
	protected Integer getIndex() {
		return this.lineIndex;
	}

}
