package com.evilnotch.lib.util.line.comment;

import java.util.List;

public interface ICommentStorage {
	
	public void addComment(ICommentAttatch c);
	public void removeComment(ICommentAttatch c);
	public List<ICommentAttatch> getComments();

}
