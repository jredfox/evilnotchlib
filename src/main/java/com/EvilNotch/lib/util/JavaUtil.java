package com.EvilNotch.lib.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.io.IOUtils;


import com.EvilNotch.lib.util.Line.LineBase;
import com.EvilNotch.lib.util.Line.LineItemStackBase;

public class JavaUtil {
	public static final String SPECIALCHARS = "~!@#$%^&*()_+`'-=/,.<>?\"{}[]:;|" + "\\";

	public static boolean isSpecialChar(char c){
		return SPECIALCHARS.contains("" + c);
	}
	
	public static boolean isStringAlphaNumeric(String str){
		str = LineBase.toWhiteSpaced(str);
		for(char c : str.toCharArray())
			if(!isCharAlphaNumeric(c))
				return false;
		return true;
	}
	public static boolean containsAlphaNumeric(String str){
		str = LineBase.toWhiteSpaced(str);
		for(char c : str.toCharArray())
			if(isCharAlphaNumeric(c))
				return true;
		return false;
	}
	/**
	 * Supports all languages
	 * Difference between is alphabetic and isLetter is that some languages combiners and vowels 
	 */
	public static boolean isCharAlphaNumeric(char c){
		return Character.isAlphabetic(c) || Character.isDigit(c);
	}
	public static boolean isCharLetterNumeric(char c){
		return Character.isLetterOrDigit((int)c);
	}
	
	@SuppressWarnings("rawtypes")
	public static void moveFileFromJar(Class clazz,String input,File output,boolean replace) {
		if(output.exists() && !replace)
			return;
		try {
			InputStream inputstream =  clazz.getResourceAsStream(input);
			FileOutputStream outputstream = new FileOutputStream(output);
			output.createNewFile();
			IOUtils.copy(inputstream,outputstream);
			inputstream.close();
			outputstream.close();
		} catch (Exception io) {io.printStackTrace();}
	}
	
	public static void printTime(long time,String msg) {
		System.out.println(msg + (System.currentTimeMillis()-time) + "ms" );
	}
	
	public void writeToClipboard(String s, ClipboardOwner owner) 
	{
		if(s == null)
			s = "null";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    Transferable transferable = new StringSelection(s);
	    clipboard.setContents(transferable, owner);
	}
	
	public static String getStaticArrayStringWithLiteral(int[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "i" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayString(boolean[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(byte[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "b" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(short[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "s" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(long[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "l" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(float[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "f" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(double[] list)
	{
		String str = "";
		for(int i=0;i<list.length;i++)
		{
			if(i != 0)
				str += " ";
			str += list[i] + "d" + ",";
		}
		if(str.equals(""))
			return null;
		return str.substring(0,str.length()-1);
	}
	public static String getStaticArrayStringWithLiteral(String[] obj)
	{
		String str = "";
		for(int i=0;i<obj.length;i++)
		{
			if(i != 0)
				str += " ";
			str += "\"" + obj[i].toString() + "\",";
		}
		if(str.equals(""))
			return null;
		
		return str.substring(0,str.length()-1);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Integer> staticToArray(int[] values) 
	{
		ArrayList<Integer> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Boolean> staticToArray(boolean[] values) 
	{
		ArrayList<Boolean> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Byte> staticToArray(byte[] values) 
	{
		ArrayList<Byte> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Short> staticToArray(short[] values) 
	{
		ArrayList<Short> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Long> staticToArray(long[] values) 
	{
		ArrayList<Long> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Float> staticToArray(float[] values) 
	{
		ArrayList<Float> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Double> staticToArray(double[] values) 
	{
		ArrayList<Double> list = new ArrayList();
		for(int i=0;i<values.length;i++)
	    	list.add(values[i]);
		return list;
	}
	/**
	 * Supports most used line only
	 * @param init_list
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<LineBase> staticToLineArray(String[] init_list)
	{
		ArrayList<String> strlist = (ArrayList<String>) JavaUtil.staticToArray(init_list);
		ArrayList<LineBase> list = new ArrayList();
		for(String s : strlist)
			list.add(new LineItemStackBase(s));
		return list;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap sortByValues(HashMap map) 
	{ 
	     List list = new LinkedList(map.entrySet());
	     // Defined Custom Comparator here
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	             return ((Comparable) ((Map.Entry) (o1)).getValue())
	                .compareTo(((Map.Entry) (o2)).getValue());
	          }
	     });

	     // Here I am copying the sorted list in HashMap
	     // using LinkedHashMap to preserve the insertion order
	     HashMap sortedHashMap = new LinkedHashMap();
	     for (Iterator it = list.iterator(); it.hasNext();) {
	            Map.Entry entry = (Map.Entry) it.next();
	            sortedHashMap.put(entry.getKey(), entry.getValue());
	     } 
	     return sortedHashMap;
	}
	public static boolean stringContainsChars(String a,String b){
		for(char c : a.toCharArray())
			if(!b.contains("" +c))
				return false;
		return true;
	}
	/**
	 * Returns Cross platform file name removing any illegal chars/names
	 * Supports mac and linux not having the "." at begining index if you want to preserver that check if it has a . to begin with and if your on windows
	 */
	public static String toFileCharacters(String s)
	{ 
		String invalid = "*/<>?\":|" + "\\";
		String resault = "";
		String sub = "";
		for (int i=0;i<s.length();i++)
		{
			sub = s.substring(i, i+1);
			if(i == 0 && sub.equals("."))
				continue;//hotfix stop . from being at 0
			if (!invalid.contains(sub) && resault.length() < 240)
				resault = resault + sub;
		}
		resault = toConWindowsCharacters(resault);
		if(resault.equals(""))
			resault = "failedModName.txt";
		
		return resault;
	}
	/**
	 * internal use toFileCharacters() instead
	 */
	public static String toConWindowsCharacters(String resault) 
	{
		String extension = "";
		if(resault.contains("."))
		{
			extension = resault.substring(resault.lastIndexOf('.'), resault.length());
			resault = resault.substring(0, resault.lastIndexOf('.'));
		}
		//windows junk
		if (resault.toUpperCase().equals("CON") || resault.toUpperCase().equals("PRN") || resault.toUpperCase().equals("AUX") || resault.toUpperCase().equals("NUL"))
			return resault + "_failed" + extension;
		for (int j=0;j<10;j++)
		{
			String com = "COM" + String.valueOf(j);
			String lpt = "LPT" + String.valueOf(j);
			if (resault.toUpperCase().equals(com) || resault.toUpperCase().equals(lpt))
			{
				return resault + "_failed" + extension;
			}
		}
		return resault + extension;
	}

	@SuppressWarnings("rawtypes")
	public static void printMap(Map map)
	{
		Iterator it = map.entrySet().iterator();
		int index = 0;
		System.out.print("[");
		while(it.hasNext())
		{
			Map.Entry pair = (Entry) it.next();
			System.out.print(" Key" + index + ":" + pair.getKey() + " Value"  + index + ":" + pair.getValue());
			index++;
		}
		System.out.print("]\n");
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List staticToArray(Object[] str)
	{
	    List list = new ArrayList();
	    for(int i=0;i<str.length;i++)
	    	list.add(str[i]);
	    return list;
	}
	
	public static byte[] arrayToStaticBytes(List<Byte> list)
	{
		byte[] strstatic =  new byte[list.size()];
		for(int i=0;i<list.size();i++)
			strstatic[i] = list.get(i);
		return strstatic;
	}
	public static int[] arrayToStaticInts(List<Integer> list)
	{
		int[] strstatic =  new int[list.size()];
		for(int i=0;i<list.size();i++)
			strstatic[i] = list.get(i);
		return strstatic;
	}
	@SuppressWarnings("rawtypes")
	public static Object[] arrayToStatic(List list)
	{
		Object[] strstatic = new Object[list.size()];
		for(int i=0;i<list.size();i++)
			strstatic[i] = list.get(i);
		return strstatic;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setList(List filelist,List list,int index)
	{
		for(int i=0;i<list.size();i++)
		{
			Object entry = list.get(i);
			boolean flag = false;
			if(index + i < filelist.size() && !flag)
				filelist.set(index+i,entry);
			else{
				filelist.add(entry);
				flag = true;
			}
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void deleteListRange(List filelist,int index1,int index2)
	{
		List<String> sub = filelist.subList(index1, index2);
		sub.clear();
	}
	public static boolean ArrayhasEqualString(String[] list, String strhead) 
	{
		for(int i=0;i<list.length;i++)
		{
			String s = list[i];
			if(s == null)
				continue;//Static arrays are known for nulls
			if(strhead.equals(s))
				return true;
		}
		return false;
	}
	public static int findLastChar(String str, char character) 
	{
		for(int i=str.length()-1;i>0;i--)
		{
			if(str.substring(i, i+1).equals("" + character))
				return i;
		}
		return -1;
	}
	public static int findLastChar(String str, String charSequence) 
	{
		for(int i=str.length()-1;i>0;i--)
		{
			if(charSequence.contains(str.substring(i, i+1)))
				return i;
		}
		return -1;
	}
	public static String[] splitStringAtIndex(int index,String tosplit) 
	{
		String[] list = new String[2];
		String first = "";
		String second = "";
		for(int i=0;i<tosplit.length();i++)
		{
			if(i < index)
				first += tosplit.substring(i, i+1);
			if(i > index)
				second += tosplit.substring(i, i+1);
		}
		list[0] = first;
		list[1] = second;
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList copyArrays(ArrayList li) 
	{
		ArrayList list = new ArrayList();
		for(Object object : li)
			list.add(object);
		return list;
	}
	public static ArrayList<ICopy> copyArrayAndObjects(ArrayList<ICopy> listings) 
	{
		ArrayList<ICopy> li = (ArrayList<ICopy>) listings;
		ArrayList<ICopy> list = new ArrayList<ICopy>();
		for(ICopy object : li)
			list.add((ICopy)object.copy());
		return list;
	}
	public static String reverseString(String s) 
	{
		String str = "";
		for(int i=s.length()-1;i>=0;i--)
			str += s.substring(i, i+1);
		return str;
	}
	@SuppressWarnings("rawtypes")
	public static boolean hasKeys(Map list, Map list2)
	{
		for(int i=0;i<list.size();i++)
		{
			Object obj = list.get(i);
			if(!list2.containsKey(obj))
				return false;
		}
		return true;
	}
	@SuppressWarnings("rawtypes")
	public static boolean hasKeys(List list, List list2)
	{
		for(int i=0;i<list.size();i++)
		{
			Object obj = list.get(i);
			if(!list2.contains(obj))
				return false;
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void reverseArray(ArrayList origin) {
		ArrayList list = new ArrayList();
		for(int i=origin.size()-1;i>=0;i--)
			list.add(origin.get(i));
		origin.clear();
		for(Object obj : list)
			origin.add(obj);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void reverseHashMap(HashMap<Integer, ArrayList> list) {
		HashMap<Integer,ArrayList> map = new HashMap();
		//sort ints
		ArrayList<Integer> ints = new ArrayList();
		for(Integer i : list.keySet() )
			ints.add(i);
		Collections.sort(ints,Collections.reverseOrder());
		for(Integer i : ints)
			map.put(i,list.get(i));
		list.clear();
		list.putAll(map);
	}
	/**
	 * Use for hashmaps for keys that override the .equals method this compares memory location
	 */
	@SuppressWarnings("rawtypes")
	public static boolean containsMemoryLocKey(HashMap map,Object obj) {
		Iterator it = map.keySet().iterator();
		while(it.hasNext())
		{
			Object obj2 = it.next();
			if(obj == obj2)
				return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static void removeKeyMemoryLoc(HashMap map, Object obj) {
		Iterator it = map.keySet().iterator();
		while(it.hasNext())
		{
			Object obj2 = it.next();
			if(obj == obj2)
				it.remove();
		}
	}

	public static char toUpperCaseChar(char c) {
		return ("" + c).toUpperCase().charAt(0);
	}

}
