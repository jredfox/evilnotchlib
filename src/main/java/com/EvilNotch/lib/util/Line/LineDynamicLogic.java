package com.EvilNotch.lib.util.Line;

import java.util.ArrayList;
import java.util.HashMap;

import com.EvilNotch.lib.util.ICopy;

import net.minecraft.util.ResourceLocation;

public class LineDynamicLogic implements ILine{
	public static boolean init = false;
    public HashMap<Integer,ArrayList<ILine> > lineLogic = new HashMap();
    protected char seperator;
    protected char quote;
    protected char[] invalid;
    protected char lbracket;
    protected char rbracket;
    protected String orLogic = "||";
    public boolean fancyLines = false;
    public static final ArrayList<String> nbts = new ArrayList<>();//bracket registry prevents detection and parsing issues
    
    public LineDynamicLogic(String s)
    {
    	this(s,':','"','#',',');
    }
    public LineDynamicLogic(String s,char sep,char q,char...invalid)
    {
    	this(s,sep,q,'[',']',"||",false,invalid);
    }
    
    public LineDynamicLogic(String s,char sep,char q,char lbracket, char rbracket,boolean fancyLine,char...invalid)
    {
    	this(s,sep,q,lbracket,rbracket,"||",false,invalid);
    }
    /**
     * Breaks up string into separate lines and each index is one && || logic Doesn't need to extend line since it's an array of lines
     * @param s
     */
    public LineDynamicLogic(String s,char sep,char q,char lbracket, char rbracket,String orLogic,boolean fancyLine,char...invalid)
    {
    	this.seperator = sep;
    	this.quote = q;
    	this.invalid = invalid;
        this.lineLogic = new HashMap();//Initiates the array
        this.lbracket = lbracket;
        this.rbracket = rbracket;
        this.fancyLines = fancyLine;
        this.orLogic = orLogic;
        init();//before first parsing is all that matters
        parse(s,sep,q,lbracket,rbracket,fancyLine,invalid);
    }
	/**
	 * Internal do not call/override unless you know what you are doing and know what this is
	 */
	protected void init(){
		if(init)
			return;
		nbts.add("{}");
		nbts.add("[]");
		nbts.add("()");
		nbts.add("<>");
		init = true;
	}
    @Override
    public void parse(String str, char sep, char q,char...invalid){
    	parse(str,sep,q,'[',']',false,invalid);
    }
    /**
     * doesn't check for \\|\\| symbol you have to watch which strings you import into LineDynamicLogic yourself as it's unoptimized to do otherwise
     */
	public void parse(String str, char sep, char q,char lbracket, char rbracket,boolean fancyLine, char...invalid) {
        if(str.contains(this.orLogic))
        {
            String[] parts = LineBase.split(str, this.orLogic);
            int index = 0;
            for(String s : parts)
            {
            	populateLines(s,index,sep,q,lbracket,rbracket,fancyLine,invalid);
                index++;
            }
        }
        else
           populateLines(str,0,sep,q,lbracket,rbracket,fancyLine,invalid);
	}

    protected void populateLines(String s, int index,char sep, char q,char lbracket,char rbracket, boolean fancyLine, char...invalid) {
        String[] subs = getSubs(s);
        ArrayList<ILine> lines = new ArrayList();
        for(String sub : subs)
        {
            if(!LineBase.toWhiteSpaced(sub).equals(""))
                lines.add(getLine(sub,sep,q,lbracket,rbracket,fancyLine,invalid));
        }
        this.lineLogic.put(index,lines);
	}
    /**
     * filters out all , with the copyright symbol if it's not in nbt or any defined nbt
     */
    protected String[] getSubs(String line) {
    	String str = "";
    	boolean insideQuote = false;
    	for(int i=0;i<line.length();i++)
    	{
    		String ch = line.substring(i, i+1);
    		if(ch.equals("" + this.quote))
    			insideQuote = !insideQuote;
    		
    		//ignore nbt when parsing
    		if(containsNBT(ch))
    		{
    			for(String s : nbts)
    			{
    				char lbracket = s.charAt(0);
    				if(ch.indexOf(lbracket) == -1)
    					continue;
    				int rindex = getIndexRBracket(i, line,lbracket,s.charAt(1));
    				String nbt = line.substring(i, rindex+1);
    				str += nbt;
    				i += nbt.length();//skip rbracket that's why it's index+1 and if i is too high it will stop iterating
    			}
    			i--;//index is already the right one so continue will force an i++ so I do an i-- to keep it right
    			continue;
    		}
    		if(ch.equals(",") && !insideQuote)
    			ch = "\u00A9";
    		str += ch;
    	}
		return str.split("\u00A9");
	}
	/**
     * Object oriented so people using this library can override with adding new lines easily
     */
	protected ILine getLine(String sub, char sep, char q,char lbracket,char rbracket,boolean fancyLine, char...invalid) {
		return getLineFromString(sub,sep,q,lbracket,rbracket,this.orLogic,fancyLine,invalid);
	}
	public static ILine getLineFromString(String s) 
    {
    	return getLineFromString(s,':','"','[',']',"||",false,'#');
    }
	
    /**
     * Returns the line from a string that is a line to be parsed
     * Doesn't check if line is possible string check that before expecting no exceptions
     */
    public static ILine getLineFromString(String s,char sep,char q,char lbracket,char rbracket,String orLogic,boolean fancy, char...invalid) 
    {
    	if(isPosibleDynamicLogic(s, sep, q))
    		return new LineDynamicLogic(s,sep,q,lbracket,rbracket,orLogic,fancy,invalid);
    	
    	LineEnhanced line = new LineEnhanced(s,sep,q,lbracket,rbracket,fancy,invalid);
    	if(line.heads.size() > 1)
    		return line;
    	else if(line.hasHead())
        	return new LineItemStack(line);
        
    	LineItemStackBase stack = new LineItemStackBase(line);
        boolean meta = stack.hasMeta || stack.hasStrMeta;
        
        if(stack.getHead() == null && stack.NBT != null || stack.getHead() == null && meta)
            return new LineItemStackBase(line);
        if(stack.getHead() == null && stack.NBT == null && !meta)
            return new LineBase(line);
        return null;
    }
    /**
     * returns if string is possible line used by configs thus the customized comment and wrapper
     */
    public static boolean isStringPossibleLine(String strline,String comment,char sep,char q)
    {
        String whitespaced = LineBase.toWhiteSpaced(strline);
        boolean notLine = !whitespaced.contains("" + sep) && !whitespaced.contains("" + q);
        if(notLine || whitespaced.equals("") || whitespaced.indexOf(comment) == 0)
            return false;
        return true;
    }
    public static boolean isStringPossibleLine(String strline,String comment,String wrapperH, String wrapperT,char sep,char q)
    {
        String whitespaced = LineBase.toWhiteSpaced(strline);
        if(whitespaced.equals(LineBase.toWhiteSpaced(wrapperH)) || whitespaced.equals(LineBase.toWhiteSpaced(wrapperT)) )
            return false;
        
        return isStringPossibleLine(strline,comment,sep,q);
    }
    /**
     * legacy support
     */
    public static boolean isStringPossibleLine(String strline)
    {
        return isStringPossibleLine(strline,"#",':','"');
    }
    public static boolean isPosibleDynamicLogic(String strline)
    {
    	return isPosibleDynamicLogic(strline,':','"');
    }
    public static boolean isPosibleDynamicLogic(String strline, char sep, char q)
    {
    	String w = removeNBT(LineBase.toWhiteSpaced(strline));
        boolean isLine = w.contains("" + sep) || w.contains("" + q);
        boolean init = isLine && strline.contains("\\|\\|") || isLine && strline.contains("||");
    	if(init)
    		return true;
    	return containsMultipleLines(w,q) && isLine;
    }
    /**
     * Doesn't check nbt and line heads Use isPossibleDynamicLogic() for the boolean 
     * this just checks it after the string has been filtered with removeNBT() and also doesn't check for || symbols
     */
	public static boolean containsMultipleLines(String str,char q) {
		boolean insideQuote = false;
		for(int i = 0;i<str.length();i++)
		{
			String ch = str.substring(i, i+1);
			if(ch.equals("" + q))
				insideQuote = !insideQuote;
			if(!insideQuote && ch.equals(","))
				return true;
		}
		return false;
	}
	public static String removeNBT(String line) {
		if(!containsNBT(line))
			return line;
		
    	//isnt't looped currently since if more then one nbt then it has to be a possible line dynamic logic
		for(String s : nbts)
		{
			char c = s.charAt(0);
			int lindex = line.indexOf(c);
			if(lindex == -1)
				continue;
			int rindex = getIndexRBracket(lindex,line,c,s.charAt(1));
			String nbt = line.substring(lindex, rindex+1);
			line = line.replace(nbt, "");
		}
   		return line;
	}
    public static boolean containsNBT(String line) {
    	for(String s : nbts)
    		if(line.indexOf(s.charAt(0)) != -1)
    			return true;
		return false;
	}
	protected static int getIndexRBracket(int lindex,String str,char lbracket,char rbracket) {
    	int lb = 0;
    	for(int i=lindex;i<str.length();i++)
    	{
    		String ch = str.substring(i, i+1);
    		if(ch.equals("" + lbracket))
    			lb++;
    		else if(ch.equals("" + rbracket))
    			lb--;
    		str += ch;
    		if(lb == 0)
    			return i;
    	}
		return -1;
	}
	/**
     * Used for display print out all values regardless of null for toString
     */
    public String getString()
    {
        String str = "";
        for(ArrayList<ILine> list : this.lineLogic.values())
        {
            for(ILine line : list)
                str += line.getString() + ", ";
            
            str = str.substring(0, str.length()-2);//get rid of last comment after parsing row
            str += " || ";//add possible next row
        }
        return this.lineLogic.size() > 0 ? str = str.substring(0, str.length()-4) : str;
    }
    /**
     * Used for printing out everything
     */
    @Override
    public String toString()
    {
        String str = "";
        for(ArrayList<ILine> list : this.lineLogic.values())
        {
            for(ILine line : list)
                    str += line.toString() + ", ";
            str = str.substring(0, str.length()-2);//get rid of last comment after parsing row
            str += " || ";
        }
        return this.lineLogic.size() > 0 ? str = str.substring(0, str.length()-4) : str;
    }
    @Override
    public boolean equals(Object obj)
    {
    	return equals(obj,true);
    }
    public boolean equals(Object obj,boolean compareHeads)
    {
        if(!(obj instanceof LineDynamicLogic))
            return false;
        LineDynamicLogic dynamic = (LineDynamicLogic)obj;
        if(this.lineLogic.size() != dynamic.lineLogic.size() )
            return false;//If not same length return so no stressfull for loops
        
        for(int i=0;i<this.lineLogic.size();i++)
        {
            ArrayList<ILine> lines1 = this.lineLogic.get(i);
            ArrayList<ILine> lines2 = dynamic.lineLogic.get(i);
            if(lines1.size() != lines2.size())
                return false;
            for(int j=0;j<lines1.size();j++)
            {
                ILine line1 = lines1.get(j);
                ILine line2 = lines2.get(j);
                if(!line1.equals(line2,compareHeads))
                    return false;
            }
        }
        return true;
    }
	@Override
	public String getModPath() {
		ILine line = this.getLineBase();
		if(line != null)
			return line.getModPath();
		
		return null;
	}
	protected ILine getFirstLine() {
		ArrayList<ILine> list = this.lineLogic.get(0);
		if(list == null || list.size() == 0)
			return null;
		return list.get(0);
	}
	@Override
	public String getModid() {
		ILine line = this.getLineBase();
		if(line != null)
			return line.getModid();
		return null;
	}
	@Override
	public String getName() {
		ILine line = this.getLineBase();
		if(line != null)
			return line.getName();
		return null;
	}
	@Override
	public ILine getLineBase() {
		return getFirstLine();
	}
	@Override
	public Object getHead() {
		ILine line = this.getLineBase();
		if(line != null)
			return line.getHead();
		
		return null;
	}
	
	public ArrayList<ILine> getLineLogic(int pos){return this.lineLogic.get(pos);}
	
	public ArrayList<ResourceLocation> getResourceLocationsFirstLogic()
	{
		return getResourceLocationsFirstLogic(true);
	}
	
	public ArrayList<ResourceLocation> getResourceLocationsFirstLogic(boolean grabStringHead)
	{
		return getResourceLocations(0,grabStringHead);
	}
	
	public ArrayList<ResourceLocation> getResourceLocations(int position,boolean grabStringHead)
	{
		ArrayList<ILine> lines = this.lineLogic.get(position);
		ArrayList<ResourceLocation> list = new ArrayList();
		for(ILine line : lines)
		{
			list.add(line.getResourceLocation());
			if(line.getHead() instanceof String && grabStringHead)
				list.add(new ResourceLocation(line.getHead().toString()));
		}
		return list;
	}
	@Override
	public ICopy copy() {
		return new LineDynamicLogic(this.getString(),this.seperator,this.quote,this.lbracket,this.rbracket,this.orLogic,this.fancyLines,this.invalid);
	}
	
	@Override
	public int compareTo(Object arg0) {
		ILine line = (ILine)arg0;
		return this.getComparible().compareTo(line.getComparible());
	}
	@Override
	public String getComparible() {
		String str = "";
	    for(ArrayList<ILine> list : this.lineLogic.values())
	    {
	         for(ILine line : list)
	              str += line.getComparible() + ", ";
	            
	         str = str.substring(0, str.length()-2);//get rid of last comment after parsing row
	         str += " || ";//add possible next row
	    }
	    return this.lineLogic.size() > 0 ? str = str.substring(0, str.length()-4) : str;
	}
	@Override
	public boolean hasHead() {
		return this.getHead() != null;
	}
	@Override
	public ResourceLocation getResourceLocation() {
		ILine line = this.getLineBase();
		if(line != null)
			return line.getResourceLocation();
		return null;
	}
	
	//getters
	@Override
    public char getSeperator(){return this.seperator;}
	@Override
    public char getQuote(){return this.quote;}
	@Override
    public String getInvalid(){return new String(this.invalid);}
	
	public char getLBracket(){return this.lbracket;}
    public char getRBracket(){return this.rbracket;}
    public String getOrLogic(){return this.orLogic;}

}
