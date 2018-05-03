package com.EvilNotch.lib.util.Line;

import java.io.File;
import java.util.ArrayList;

public class ConfigEnhanced extends ConfigBase{
	
    public ConfigEnhanced(File file)
    {
        this(file,new ArrayList<Comment>() );
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments)
    {
        this(file,initComments,"");
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header)
    {
        this(file,initComments,header,'#');
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header,char commentStart)
    {
        this(file,initComments,header,commentStart,'<','>','/',true,':','"');
    }
    /**
     * Custom Constructor for line enhanced only
     */
    public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header,char commentStart,char lbracket,char rbracket,boolean fancyLine){
    	this(file,initComments,header,commentStart,'<','>','/',true,':','"',lbracket,rbracket,fancyLine);
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote)
    {
    	this(file,initComments,header,commentStart,lhwrap,rhwrap,hslash,comments,lsep,lquote,'[',']',false);
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote,char lbracket,char rbracket,boolean fancyLine)
    {
    	this(file,initComments,header,commentStart,lhwrap,rhwrap,hslash,comments,lsep,lquote,lbracket,rbracket,fancyLine,"||");
    }
    public ConfigEnhanced(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote,char lbracket,char rbracket,boolean fancyLine,String orLogic)
    {
    	super(file,initComments,header,commentStart,lhwrap,rhwrap,hslash,comments,lsep,lquote,lbracket,rbracket,fancyLine,orLogic);
    }
	
	@Override
	protected ILine getLine(String str){
		str = this.removeComments(str);
		return new LineEnhanced(str,this.lineSeperator,this.lineQuote,this.lbracket,this.rbracket,this.fancyLines,this.commentStart);
	}

}
