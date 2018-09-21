package com.evilnotch.lib.util.line;

import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.util.line.comment.ICommentAttatch;
import com.evilnotch.lib.util.line.comment.ICommentStorage;

public abstract class LineComment implements ILine,ICommentStorage{
	
	/**
	 * list of comments attached to the list
	 */
	public List<ICommentAttatch> comments = new ArrayList<ICommentAttatch>();
	
	@Override
	public void addComment(ICommentAttatch c) {
		this.comments.add(c);
	}

	@Override
	public void removeComment(ICommentAttatch c) {
		this.comments.remove(c);
	}

	@Override
	public List<ICommentAttatch> getComments() {
		return this.comments;
	}

}
