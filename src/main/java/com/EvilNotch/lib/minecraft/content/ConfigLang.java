package com.EvilNotch.lib.minecraft.content;

import java.io.File;
import java.util.ArrayList;

import com.EvilNotch.lib.util.Line.Comment;
import com.EvilNotch.lib.util.Line.ConfigBase;
import com.EvilNotch.lib.util.Line.ILine;
import com.EvilNotch.lib.util.Line.LineBase;
import com.EvilNotch.lib.util.Line.LineDynamicLogic;

public class ConfigLang extends ConfigBase{

	public ConfigLang(File f){
		this(f,new ArrayList<Comment>(),"",'#','<','>','/',false,':','"','[',']',false,"||");
	}

	protected ConfigLang(File file, ArrayList<Comment> initComments, String header, char commentStart, char lhwrap,
			char rhwrap, char hslash, boolean comments, char lsep, char lquote, char lbracket, char rbracket,
			boolean fancyLine, String orLogic) {
		
		super(file, initComments, header, commentStart, lhwrap, rhwrap, hslash, comments, lsep, lquote, lbracket, rbracket,
				fancyLine, orLogic);
	}
	@Override
	protected boolean isStringPossibleLine(String strline, String comment, String wrapperH, String wrapperT,char sep, char q) 
	{
		strline = LineBase.toWhiteSpaced(strline);
		if(strline.indexOf(comment) == 0 || strline.equals(""))
			return false;
		return strline.indexOf('=') > 0;
	}
	@Override
	protected boolean isStringPossibleLine(String strline, String comment, char sep, char q) {
		strline = LineBase.toWhiteSpaced(strline);
		if(strline.indexOf(comment) == 0 || strline.equals(""))
			return false;
		return strline.indexOf('=') > 0;
	}
	
	@Override 
	protected ILine getLine(String strline)
	{
		strline = removeComments(strline);
		return new LangLine(strline);
	}

}
