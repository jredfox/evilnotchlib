package com.evilnotch.lib.util.line.comment;

public interface ICommentAttatch extends IComment{
	
	/**
	 * is it directly attached to the object such as a line or header
	 */
	public boolean isAttatched();
	/**
	 * tmp index that gets stored for later attatching
	 */
	public int getTmpIndex();
	/**
	 * set your tmp index
	 */
	public void setTmpIndex(int i);

}
