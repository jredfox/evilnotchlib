package com.EvilNotch.lib.util.Line;

import java.io.File;
import java.util.ArrayList;

public class ConfigDynamic extends ConfigBase{
	
    public ConfigDynamic(File file)
    {
        this(file,new ArrayList<Comment>() );
    }
    public ConfigDynamic(File file, ArrayList<Comment> initComments)
    {
        this(file,initComments,"");
    }
    public ConfigDynamic(File file, ArrayList<Comment> initComments, String header)
    {
        this(file,initComments,header,'#');
    }
    public ConfigDynamic(File file, ArrayList<Comment> initComments, String header,char commentStart)
    {
        this(file,initComments,header,commentStart,'<','>','/',true,':','"');
    }
    public ConfigDynamic(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote)
    {
    	this(file,initComments,header,commentStart,lhwrap,rhwrap,hslash,comments,lsep,lquote,'[',']',false);
    }
    public ConfigDynamic(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote,char lbracket,char rbracket,boolean fancyLine)
    {
    	this(file,initComments,header,commentStart,lhwrap,rhwrap,hslash,comments,lsep,lquote,lbracket,rbracket,fancyLine,"||");
    }
    public ConfigDynamic(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote,char lbracket,char rbracket,boolean fancyLine,String orLogic)
    {
    	super(file,initComments,header,commentStart,lhwrap,rhwrap,hslash,comments,lsep,lquote,lbracket,rbracket,fancyLine,orLogic);
    }
	
	@Override
	protected ILine getLine(String str){
		str = this.removeComments(str);
		return new LineDynamicLogic(str,this.lineSeperator,this.lineQuote,this.lbracket,this.rbracket,this.orLogic,this.fancyLines,this.commentStart);
	}

}
