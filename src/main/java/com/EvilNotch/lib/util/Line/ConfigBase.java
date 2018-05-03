package com.EvilNotch.lib.util.Line;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.LineBase;


public class ConfigBase {
    
    public ArrayList<ILine> lines; //warning check before assuming linebase could be linedynamic logic or modded line that uses this interface
    public final File cfgfile;
    public String header = "";
    public boolean first_launch = false;
    public boolean enableComments = true;
    public boolean fancyLines = false;
    
    //comments and wrappers
    protected ArrayList<Comment> init;
    protected ArrayList<Comment> origin;
    protected ArrayList<Comment> comments = null;
    
    protected char commentStart = '#';
    protected char headerLWrap = '<';
    protected char headerRWrap = '>';
    protected char headerSlash = '/';
    protected char lineSeperator = ':';
    protected char lineQuote = '"';
    protected char lbracket = '[';
    protected char rbracket = ']';
    protected String orLogic = "||";
    
    protected ArrayList<String> lineChecker;//optimized to only keep it as string and not reparse it
    protected ArrayList<Comment> initChecker;
    protected ArrayList<Comment> commentChecker;
    
    public ConfigBase(File file)
    {
        this(file,new ArrayList<Comment>() );
    }
    public ConfigBase(File file, ArrayList<Comment> initComments)
    {
        this(file,initComments,"");
    }
    public ConfigBase(File file, ArrayList<Comment> initComments, String header)
    {
        this(file,initComments,header,'#');
    }
    public ConfigBase(File file, ArrayList<Comment> initComments, String header,char commentStart)
    {
        this(file,initComments,header,commentStart,'<','>','/',true,':','"');
    }
    public ConfigBase(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote)
    {
    	this(file,initComments,header,commentStart,lhwrap,rhwrap,hslash,comments,lsep,lquote,'[',']',false);
    }
    public ConfigBase(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote,char lbracket,char rbracket,boolean fancyLine)
    {
    	this(file,initComments,header,commentStart,lhwrap,rhwrap,hslash,comments,lsep,lquote,lbracket,rbracket,fancyLine,"||");
    }
    
    public ConfigBase(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote,char lbracket,char rbracket,boolean fancyLine,String orLogic)
    {
        this.cfgfile = file;
        this.lines = new ArrayList<>();
        for(Comment c : initComments)
            c.start = commentStart;//fix comments
        this.init = initComments;
        this.origin = JavaUtil.copyArrayAndObjects((ArrayList)initComments);
        this.header = header;
        this.commentStart  = commentStart;
        this.headerLWrap = lhwrap;
        this.headerRWrap = rhwrap;
        this.headerSlash = hslash;
        this.enableComments = comments;
        this.lineSeperator = lsep;
        this.lineQuote = lquote;
        this.lbracket = lbracket;
        this.rbracket = rbracket;
        this.fancyLines = fancyLine;
        this.orLogic = orLogic;
        boolean exsists = file.exists();
        
        if(!exsists)
        {
            try {
            	//hotfix dir has to exist
            	File parent = file.getParentFile();
            	if(!parent.exists())
            		parent.mkdirs();
            	
                file.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
            this.first_launch = true;
            this.writeFile(new ArrayList<>());
        }
        this.readFile();//cache arrays
    }

    public void writeFile(ArrayList<String> list) 
    {
        try {
            //header and init
            ArrayList<String> toWrite = new ArrayList();
            for(Comment c : this.init)
            	toWrite.add("" + c.start + c.comment);
            
            if(this.init.size() > 0)
            	toWrite.add("");//create new line if has header
            if(!this.header.equals("")) 
            	toWrite.add(this.getWrapper(true) + "\r\n");
            
            for(String s : list)
            	toWrite.add(s);//lines
            
            //end of header
            if(!this.header.equals(""))
            	toWrite.add("\r\n" + this.getWrapper(false));
            
            //enforce unicode regardless of jvm-args
           JavaUtil.saveFileLines(toWrite, this.cfgfile, true);
            
        } catch (Exception e) {e.printStackTrace();}
        
        //sync changes to before this call
        if(this.enableComments && this.comments != null)
        	for(Comment c : this.comments)
         		c.lineIndex = -1;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void readFile()
    {
        this.lines = new ArrayList();
        this.comments = new ArrayList();
        
        try {
            List<String> filelist = JavaUtil.getFileLines(this.cfgfile,true);
            removeBOM(filelist);
            String wrapperHead = getWrapper(true);
            String wrapperTail = getWrapper(false);
            int index = 0;
            int actualIndex = 0;
            boolean initPassed = false;
           
            int headerIndex = -1;
            
            //scan for header
            int count = 0;
            for(String s : filelist)
            {
                String whitespaced = LineBase.toWhiteSpaced(s);
                if(whitespaced.equals(LineBase.toWhiteSpaced(wrapperHead)) && !this.header.equals("") || whitespaced.equals(""))
                {
                    headerIndex = count;
                    break;
                }
                if(isStringPossibleLine(whitespaced, "" + this.commentStart,this.lineSeperator,this.lineQuote))
                	break;//if is line stop trying to parse the header index
                count++;
            }
            
            for(String strline : filelist)
            {
                String whitespaced = LineBase.toWhiteSpaced(strline);
                initPassed = actualIndex > headerIndex;
                actualIndex++;//since only used for boolean at beginging no need to copy ten other places
                if(whitespaced.equals(""))
                	continue;//optimization
                if(!isStringPossibleLine(strline,"" + this.commentStart,wrapperHead,wrapperTail,this.lineSeperator,this.lineQuote) )
                {
                    //comment handling
                    if(whitespaced.indexOf(this.commentStart) == 0 && this.enableComments)
                    {
                        if(initPassed)
                        	this.comments.add(new Comment(index,strline,false,this.commentStart) );//if not passed header leave comments to init
                        if(!initPassed)
                        {
                        	Comment initcomment = new Comment(strline,this.commentStart);
                        	if(!this.hasHeaderComment(initcomment))
                        		this.init.add(initcomment);
                        }
                    }
                    
                    continue;
                }
                ILine line = this.getLine(strline);
                lines.add(line);
                //scan for attached comments on lines
                if(this.enableComments)
                if(strline.contains("" + this.commentStart))
                {
                    int hIndex = JavaUtil.findLastChar(strline, this.commentStart);
                    if(hIndex >= line.getString().length() )
                        this.comments.add(new Comment(index,strline.substring(hIndex, strline.length()),true,this.commentStart) );
                }
                
                index++;
            }
        } catch (Exception e) {e.printStackTrace();}
        
        
        this.lineChecker = getStringLines();
        if(this.enableComments)
        {
        	this.initChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.init);
        	this.commentChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.comments);
        	updateCommentLines();
        }
        else{
        	this.initChecker = new ArrayList();
        	this.commentChecker = new ArrayList();
        }
    }
    /**
     * Removes UTF-8 Byte Order Marks
     */
    public void removeBOM(List<String> list) 
    {
		for(int i=0;i<list.size();i++)
		{
			String s = list.get(i);
			if(s.length() > 0)
			{
				if((int)s.charAt(0) == 65279)
					s = s.substring(1, s.length());
				list.set(i, s);
			}
		}
	}
	protected boolean isStringPossibleLine(String strline, String comment, String wrapperH, String wrapperT,char sep, char q) {
		return LineDynamicLogic.isStringPossibleLine(strline, comment, wrapperH, wrapperT, sep, q);
	}
	protected boolean isStringPossibleLine(String strline, String comment, char sep, char q) {
		return LineDynamicLogic.isStringPossibleLine(strline, comment, sep, q);
	}
	/**
     * Object oriented so people using this library can override with adding new lines easily
     * returns in ConfigBase LineDynamicLogic.getLineFromString(...)
     */
    protected ILine getLine(String strline) {
    	strline = removeComments(strline);
    	return LineDynamicLogic.getLineFromString(strline,this.lineSeperator,this.lineQuote,this.lbracket,this.rbracket,this.orLogic,this.fancyLines,this.commentStart);
	}
    protected String removeComments(String strline) {
		if(!strline.contains("" + this.commentStart))
			return strline;//optimization
		String str = "";
		boolean insideQuote = false;
		for(int i=0;i<strline.length();i++)
		{
			String s = strline.substring(i, i+1);
			if(s.equals("" + this.lineQuote))
				insideQuote = !insideQuote;//invert the boolean
			if(!insideQuote && s.equals("" + this.commentStart))
				break;//stop parsing if it's a comment that's not inside a quote
			str += s;
		}
		return str;
	}
	protected String getWrapper(boolean head)
    {
        if(head)
            return this.getLWrap() + this.header + this.headerRWrap;
        else
            return this.getLWrap() + this.headerSlash + this.header + this.headerRWrap;
    }
    protected String getLWrap() {
        return LineBase.toWhiteSpaced("" + this.headerLWrap);
    }
    /**
     * sets all from array to indexes if not out of bounds
     * basically just replace all from this line on forward based till it runs out of indexes and then adds
     */
    public void setCfgList(ArrayList<ILine> list, int index)
    {
        for(int i=0;i<list.size();i++)
        {
            ILine str = list.get(i);
            boolean flag = false;
            if(index + i < this.lines.size() && !flag)
                this.lines.set(index+i,str);
            else{
                this.lines.add(str);
                flag = true;
            }
        }
    }
    public void setList(ArrayList<String> list, int index)
    {
        for(int i=0;i<list.size();i++)
        {
            ILine line = this.getLine(list.get(i));
            boolean flag = false;
            if(index + i < this.lines.size() && !flag)
                this.lines.set(index+i,line);
            else{
                this.lines.add(line);
                flag = true;
            }
        }
    }
    /**
     * Add a line if and only if It Doesn't exist treats the ConfigBase like a Set[ILine] rather then ArrayList[ILine]
     */
    public void addLine(ILine line){
        if(this.containsLine(line))
            return;
        this.appendLine(line);
    }
    public void addLine(ILine line, int index){
        if(this.containsLine(line))
            return;
        this.appendLine(line,index);
    }
    /**
     * Add a line if and only if It Doesn't exist treats the ConfigBase like a Set[ILine] rather then ArrayList[ILine]
     */
    public void addLine(ILine line,boolean compareBase){
        if(this.containsLine(line,false,compareBase))
            return;
        this.appendLine(line);
    }
    public void addLine(ILine line, int index,boolean compareBase){
        if(this.containsLine(line,false,compareBase))
            return;
        this.appendLine(line,index);
    }
    
    /**
     * Append List of lines if line itself isn't added
     */
    public void addLineList(ArrayList<ILine> list,boolean compareBase)
    {
        for(ILine line : list)
            addLine(line,compareBase);
    }
    
    /**
     * Append List of lines at starting index
     */
    public void addLineList(ArrayList<ILine> list, int index,boolean compareBase)
    {
        for(ILine line : list)
        	addLine(line,index++,compareBase);
    }
    
    public void addLineList(ArrayList<ILine> list)
    {
    	addLineList(list,true);
    }
    public void addLineList(ArrayList<ILine> list, int index)
    {
    	addLineList(list,index,true);
    }
    
    /**
     * Appends Line To end of file
     * @param str
     */
    public void appendLine(ILine line)
    {
        this.lines.add(line);
    }
    /**
     * Appends Line To index of file
     * @param str
     */
    public void appendLine(ILine line,int index)
    {
        this.lines.add(index,line);
    }
    /**
     * Append List of lines
     * @param list
     */
    public void appendLineList(ArrayList<ILine> list)
    {
        this.lines.addAll(list);
    }
    /**
     * Append List of lines at starting index
     * @param list
     * @param index
     */
    public void appendLineList(ArrayList<ILine> list, int index)
    {
        for(ILine line : list)
            this.appendLine(line,index++);
    }
    public void setLine(ILine line)
    {
    	setLine(line,true);
    }
    /**
     * replace line if already exists else add it
     */
    public void setLine(ILine line,boolean compareBase)
    {
    	int index = getLineIndex(compareBase ? line.getLineBase() : line);
    	if(index != -1)
    		setLine(line,index);
    	else
    		this.lines.add(line);
    }
    public int getLineIndex(ILine line) {
    	int index = 0;
    	for(ILine compare : this.lines)
    	{
    		if(this.isLineEqual(line, compare, false))
    			return index;
    		index++;
    	}
		return -1;
	}
	/**
     * Sets line to index
     * @param line
     * @param index
     */
    public void setLine(ILine line, int index)
    {
    	this.lines.set(index, line);
    }
    /**
     * Take line objects and convert set them to the file
     * @param list
     * @param index
     */
    public void setLineList(ArrayList<ILine> list, int index)
    {
        this.setCfgList(list, index);
    }
    /**
     * Deletes first instanceof ILine
     */
    public void deleteLine(ILine line)
    {
    	deleteILine(line,false,false,true);
    }
    public void deleteILine(ILine line, boolean deleteAll,boolean compareHead,boolean compareBase)
    {
    	if(compareBase)
    		line = line.getLineBase();
        Iterator<ILine> it = this.lines.iterator();
        while(it.hasNext())
        {
            ILine compare = it.next();
            if(compareBase)
            	compare = compare.getLineBase();
            if(isLineEqual(line,compare,compareHead) )
            {
                it.remove();
                removeComments(line,compareHead,compareBase);
                if(!deleteAll)
                    break;
            }
        }
    }
    /**
     * doesn't make initial line compare base since it should already be filtered
     */
    protected void removeComments(ILine line,boolean compareHead,boolean compareBase) 
    {
    	Iterator<Comment> it = this.comments.iterator();
    	while(it.hasNext())
    	{
    		Comment c = it.next();
    		ILine compare = c.nearestLine;
    		if(compareBase)
    			compare = compare.getLineBase();
    		if(compare.equals(line,compareHead))
    			it.remove();
    	}
	}
	public boolean isLineEqual(ILine line, ILine compare, boolean compareHead) {
		return line.equals(compare,compareHead) && compare.equals(line, compareHead);
	}
	/**
     * Delete all instances of list of lines
     * @param list
     */
    public void deleteAllLines(ArrayList<ILine> list)
    {
        for(ILine line : list)
        	deleteILine(line,true,false,true);
    }
    public boolean containsLine(ILine line,boolean compareHead,boolean compareBase)
    {
    	if(compareBase)
    		line = line.getLineBase();
    	for(ILine compare : this.lines)
    	{
    		if(compareBase)
    			compare = compare.getLineBase();
    		//let both lines have a rejection because line base isn't going to check for additions of lineitemstackbase etc...
    		if(isLineEqual(line,compare,compareHead))
                return true;//makes it compare the equals at the base level
    	}
        return false;
    }
    
    public ILine getUpdatedLine(ILine print){
    	return getUpdatedLine(print,true);
    }
    
    public ILine getUpdatedLine(ILine print,boolean compareBase){
    	if(compareBase)
    		print = print.getLineBase();
    	for(ILine compare : this.lines)
    	{
    		if(compareBase)
    			compare = compare.getLineBase();
    		//let both lines have a rejection because line base isn't going to check for additions of lineitemstackbase etc...
    		if(isLineEqual(print,compare,false))
                return compare;//makes it compare the equals at the base level
    	}
        return print;
    }
    /**
     * Does this config contain this line?
     * @param line
     * @return
     */
    public boolean containsLine(ILine line)
    {
        return containsLine(line,false,true);
    }
    
    /**
     * Don't Use Unless you want the config defaults people will get pissed
     */
    public void resetConfig()
    {
        try {
        	setInit(this.origin);
        	writeFile(new ArrayList());
            this.readFile();//makes arrays reset
        } catch(Exception e) {e.printStackTrace();}
    }
    /**
     * shuffle entries on the config
     */
    public void shuffle()
    {
    	boolean changeLines = this.getStringLines().equals(this.lineChecker);
    	boolean changeComments = this.comments.equals(this.commentChecker);
    	
    	Collections.shuffle(this.lines);
    	if(changeLines)
    		this.lineChecker = this.getStringLines();
    	updateCommentIndexes(false,changeComments);
    }
    public void shuffle(int times)
    {
    	boolean changeLines = this.getStringLines().equals(this.lineChecker);
    	boolean changeComments = this.comments.equals(this.commentChecker);
    	
    	for(int i=0;i<times;i++)
    		Collections.shuffle(this.lines);
    	
    	if(changeLines)
    		this.lineChecker = this.getStringLines();
    	
    	updateCommentIndexes(false,changeComments);
    }
    public void shuffle(Random rnd)
    {
    	boolean changeLines = this.getStringLines().equals(this.lineChecker);
    	boolean changeComments = this.comments.equals(this.commentChecker);
    	
    	Collections.shuffle(this.lines, rnd);
    	
    	if(changeLines)
    		this.lineChecker = this.getStringLines();
    	
    	updateCommentIndexes(false,changeComments);
    }
    public void shuffle(Random rnd, int times)
    {
    	boolean changeLines = this.getStringLines().equals(this.lineChecker);
    	boolean changeComments = this.comments.equals(this.commentChecker);
    	
    	for(int i=0;i<times;i++)
    		Collections.shuffle(this.lines, rnd);
    	
    	if(changeLines)
    		this.lineChecker = this.getStringLines();
    	
    	updateCommentIndexes(false,changeComments);
    }
    /**
     * Re-Orders File to be alphabetized
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void alphabetize()
    {
        boolean alphabitizeChecker = this.getStringLines().equals(this.lineChecker);
        boolean changeComments = this.comments.equals(this.commentChecker);
        
        Collections.sort(this.lines);//ILine extends IObject which extends Comparable,ICopy so this is possible
        if(alphabitizeChecker)
        	this.lineChecker = this.getStringLines();//sync changes is called get lines again since lines have been alphabitized
        
        updateCommentIndexes(false,changeComments);
    }
    
    protected void updateCommentIndexes(boolean hasIndex,boolean checker) 
    {
    	 //re-organize comments to new locations and sync checkers for updating the config
        if(this.enableComments)
        {
        	if(hasIndex)
        	{
        		for(Comment c : this.comments)
        		{
        			int index = 0;
        			for(ILine line : this.lines)
        			{
        				//enforces higherarchy to deny it's equal to also in case lines change values it compares the bases
        				if(line != null && c.nearestLine != null)
        				if(isLineEqual(line,c.nearestLine,false) )
        				{
        					c.lineIndex = index;
        					break;
        				}
        				index++;
        			}
        		}
        	}
        	if(checker)
        		this.commentChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.comments);//sync index change since it's compared for equals()
        }
	}
	protected void updateCommentLines() {
        for(Comment c : this.comments)
        {
           if(this.lines.size() != 0)
           {
        	   c.nearestLine = this.lines.get(c.lineIndex);
           	   c.lineIndex = -1;
           }
        }
    }

    /**
     * Since it's un-optimized to re-write the file every single entry is deleted/added 
     * it will only save data when this is called
     */
    public void updateConfig()
    {
        ArrayList<String> list = getStringLines();
        //comment handling
        if(this.enableComments)
        {
            try
            {
              updateCommentIndexes(true,true);
              reorganizeComments();
              int offset = 0;
              for(Comment c : this.comments)
              {
                  if(!c.isAttactched)
                  {
                      list.add(c.lineIndex + offset,c.start + c.comment);
                      offset++;//only offset it if comment is full line
                  }
                  else
                      list.set(c.lineIndex + offset, list.get(c.lineIndex + offset) + c.start + c.comment);
              }
           }
           catch(Exception e){e.printStackTrace();}
          
           this.initChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.init);//regular comments got update via call above
           this.commentChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.comments);//sync
        }
        else{
        	this.initChecker.clear();
        	this.commentChecker.clear();//not supported if not enabled
        }
        
        this.lineChecker = JavaUtil.copyArrays(list);//since is immutable don't worry about them getting modified
        
        this.writeFile(list);
    }
    public void updateConfig(boolean alphabitize)
    {
    	updateConfig(alphabitize,false);
    }
    public void updateConfig(boolean alphabitize,boolean forceUpdate)
    {
    	updateConfig(alphabitize,forceUpdate,false);
    }
    /**
     * update if and only if data has been modified in memory
     */
    public void updateConfig(boolean alphabitize,boolean forceUpdate,boolean msg)
    {   
        //don't update for alphabetizate as it will screw up people using the cfg only do it if the config itself needs updating
        if(forceUpdate || !this.getStringLines().equals(this.lineChecker) || this.enableComments && !this.comments.equals(this.commentChecker) || this.enableComments && !this.init.equals(this.initChecker))
        {
        	if(alphabitize)
                this.alphabetize();
        	if(msg)
        		System.out.print("CFG Updating:" + this.cfgfile + "\n");
        	this.updateConfig();
        }
    }
    /**
     * organize comments by numeric order
     */
    public void reorganizeComments() {
    	boolean alphaComment = this.comments.equals(this.commentChecker) && this.enableComments;
        ArrayList<Integer> initialArray = new ArrayList<>();
        //add then sort
        for(Comment c : this.comments)
        	initialArray.add(new Integer(c.lineIndex));
        Set<Integer> set = new HashSet<Integer>(initialArray);
        initialArray.clear();
        for(Integer i : set)
        	initialArray.add(i);
        Collections.sort(initialArray);
        
//        System.out.println(initialArray);
        ArrayList<Comment> coms = new ArrayList<>();
        for(Integer i : initialArray)
        {
        	for(Comment c : this.comments)
        		if(c.lineIndex == i)
        			coms.add(c);
        }
        this.comments = coms;
        if(alphaComment)
    		this.commentChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.comments);//sync re-organization
    }
    protected ArrayList<String> getStringLines() {
        ArrayList<String> list = new ArrayList();
        for(ILine line : this.lines)
            list.add(line.getString() );
        return list;
    }
    /**
     * Ignores the index parameter
     */
    public boolean hasHeaderComment(Comment comment){
    	for(Comment c : this.init)
    		if(c.equals(comment,false))
    			return true;
    	return false;
    }
    /**
     * Ignores the index parameter
     */
    public boolean hasComment(Comment comment){
    	for(Comment c : this.comments)
    		if(c.equals(comment,false))
    			return true;
    	return false;
    }
    /**
     * Add a comment after the config has been instantiated
     */
    public void appendComment(Comment c,ILine line){
    	c.nearestLine = line;
    	this.comments.add(c);
    }
    
    //basic getters and setters for comments
    public ArrayList<Comment> getComments(){return this.comments;}
    public ArrayList<Comment> getInit(){return this.init;}
    public ArrayList<Comment> getOrigin(){return this.origin;}
    public void setOrigin(ArrayList<Comment> list){this.origin = list;}
    public void setInit(ArrayList<Comment> list){this.init = list;}
    
    //getters no setters for base as in a default config by my library shouldn't be changing ever however if someone makes their own they are free to change them whenever
    public char getCommentStart(){return this.commentStart;}
    public char getHeaderLWrap(){return this.headerLWrap;}
    public char getHeaderRWrap(){return this.headerRWrap;}
    public char getHeaderSlash(){return this.headerSlash;}
    public char getLineSeperator(){return this.lineSeperator;}
    public char getLineQuote(){return this.lineQuote;}
    public char getLBracket(){return this.lbracket;}
    public char getRBracket(){return this.rbracket;}
    public String getOrLogic(){return this.orLogic;}

    @Override
    public String toString()
    {
        String s = "<ConfigBase>\n";
        for(Comment c : this.comments)
            s += c + "\n";
        for(ILine line : this.lines)
            s += line.toString() + "\n";
        return s + "</ConfigBase>\n";
    }
    
}
