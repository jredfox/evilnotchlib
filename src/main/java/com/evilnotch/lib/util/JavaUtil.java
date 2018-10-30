package com.evilnotch.lib.util;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.primitive.ByteObj;
import com.evilnotch.lib.util.primitive.DoubleObj;
import com.evilnotch.lib.util.primitive.FloatObj;
import com.evilnotch.lib.util.primitive.IntObj;
import com.evilnotch.lib.util.primitive.LongObj;
import com.evilnotch.lib.util.primitive.ShortObj;
import com.evilnotch.lib.util.simple.ICopy;

import net.minecraft.util.ResourceLocation;

public class JavaUtil {
	public static final String SPECIALCHARS = "~!@#$%^&*()_+`'-=/,.<>?\"{}[]:;|" + "\\";
	public static final String uniqueSplitter = "\u00A9" + "#" + "\u20AC";
	public static String numberIds = "bslfdi";
	
	/**
	 * cast without loosing data and have a random negative number
	 */
	public static int castInt(long l)
	{
		if(l > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		if(l < Integer.MIN_VALUE)
			return Integer.MIN_VALUE;
		return (int)l;
	}
	public static short castShort(long l)
	{
		if(l > Short.MAX_VALUE)
		{
			return Short.MAX_VALUE;
		}
		else if(l < Short.MIN_VALUE)
		{
			return Short.MIN_VALUE;
		}
		return (short)l;
	}
	public static byte castByte(long l)
	{
		if(l > Byte.MAX_VALUE)
			return Byte.MAX_VALUE;
		if(l < Byte.MIN_VALUE)
			return Byte.MIN_VALUE;
		return (byte)l;
	}
	
	public static short castShort(int i)
	{
		if(i > Short.MAX_VALUE)
		{
			return Short.MAX_VALUE;
		}
		else if(i < Short.MIN_VALUE)
		{
			return Short.MIN_VALUE;
		}
		return (short)i;
	}
	public static byte castByte(int i)
	{
		if(i > Byte.MAX_VALUE)
			return Byte.MAX_VALUE;
		if(i < Byte.MIN_VALUE)
			return Byte.MIN_VALUE;
		return (byte)i;
	}
	public static byte castByte(short s)
	{
		if(s > Byte.MAX_VALUE)
			return Byte.MAX_VALUE;
		if(s < Byte.MIN_VALUE)
			return Byte.MIN_VALUE;
		return (byte)s;
	}
	public static byte castByte(float f) 
	{
		long l = convertToLong(f);
		return JavaUtil.castByte(l);
	}
	public static byte castByte(double d) 
	{
		long l = convertToLong(d);
		return JavaUtil.castByte(l);
	}
	
	public static short castShort(float f) 
	{
		long l = convertToLong(f);
		return JavaUtil.castShort(l);
	}
	public static short castShort(double d) 
	{
		long l = convertToLong(d);
		return JavaUtil.castShort(l);
	}
	public static int castInt(float f) 
	{
		long l = convertToLong(f);
		return JavaUtil.castInt(l);
	}
	public static int castInt(double d) 
	{
		long l = convertToLong(d);
		return JavaUtil.castInt(l);
	}
	public static long castLong(float f)
	{
		return convertToLong(f);
	}
	public static long castLong(double d)
	{
		return convertToLong(d);
	}
	public static float castFloat(double d)
	{
		if(d > Float.MAX_VALUE)
			return Float.MAX_VALUE;
		else if (d < Float.MIN_VALUE)
			return Float.MIN_VALUE;
		return (float)d;
	}
	
	/**
	 * doesn't work every time as java algorithms truncate to 0 sometimes when negative only????
	 */
	public static long convertToLong(double d)
	{
		if(d > Long.MAX_VALUE)
			return Long.MAX_VALUE;
		if(d < Long.MIN_VALUE)
			return Long.MIN_VALUE;
		return Math.round(d);
	}
	/**
	 * doesn't work every time as java algorithms truncate to 0 sometimes when negative only????
	 */
	public static long convertToLong(float f)
	{
		if(f > Long.MAX_VALUE)
			return Long.MAX_VALUE;
		if(f < Long.MIN_VALUE)
			return Long.MIN_VALUE;
		return (long)f;
	}
	
	public static int getInt(Number obj)
	{
		obj = getIntNum(obj);
		if(obj instanceof Long)
			return JavaUtil.castInt((Long)obj);
		return obj.intValue();
	}
	public static short getShort(Number obj)
	{
		obj = getIntNum(obj);
		if(obj instanceof Long)
			return JavaUtil.castShort((Long)obj);
		else if(obj instanceof Integer)
			return JavaUtil.castShort((Integer)obj);
		return obj.shortValue();
	}
	public static byte getByte(Number obj)
	{
		obj = getIntNum(obj);
		if(obj instanceof Long)
			return JavaUtil.castByte((Long)obj);
		else if(obj instanceof Integer)
			return JavaUtil.castByte((Integer)obj);
		else if(obj instanceof Short)
			return JavaUtil.castByte((Short)obj);
		return obj.byteValue();
	}
	
	public static Long getLong(Number obj){
		obj = getIntNum(obj);
		return obj.longValue();
	}
	public static double getDouble(Number obj){
		return obj.doubleValue();
	}
	public static float getFloat(Number obj){
		return obj.floatValue();
	}
	/**
	 * if double/float convert to integer of long else do nothing
	 */
	public static Number getIntNum(Number obj) {
		if(obj instanceof Double)
		{
			obj = new Long(JavaUtil.convertToLong((Double)obj));
		}
		else if(obj instanceof Float)
		{
			obj = new Long(JavaUtil.convertToLong((Float)obj));
		}
		return obj;
	}
	
	/**
	 * dynamically get your current public ip adress I recommend cacheing it somewhere so it doesn't go throw a huge process each time
	 */
	public static String getPublicIp() throws IOException 
	{
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		String ip = in.readLine(); //you get the IP as a String
		return ip.trim();
	}
	/**
	 * your current computer adress's ip
	 */
	public static String getIpv4() throws UnknownHostException 
	{
        InetAddress inetAddress = InetAddress.getLocalHost();
		return inetAddress.getHostAddress();
	}
	
	public static boolean isOnline(String url)
	{
		try
		{
			if(url == null)
				url = "www.google.com";
			if(url.contains("https://")) {
				url = url.replaceFirst("https://", "");
			}
			else if (url.contains("http://")){
				url = url.replaceFirst("http://", "");
			}
			Socket soc = new Socket();
			InetSocketAddress adress = new InetSocketAddress(url,80);
			soc.setSoTimeout(3500);
			soc.connect(adress);
			soc.close();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public static void getAllFilesFromDir(File directory, ArrayList<File> files,String extension) {

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile() && !files.contains(file) && file.getName().endsWith(extension)) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	        	getAllFilesFromDir(file, files,extension);
	        }
	    }
	}
	public static void getAllFilesFromDir(File directory, ArrayList<File> files) {

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile() && !files.contains(file)) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	        	getAllFilesFromDir(file, files);
	        }
	    }
	}
	
	public static Color getColorFromMsAcess(int p_78258_4_)
	{
		
		int red = (int)(p_78258_4_ >> 16 & 255);
        int green = (int)(p_78258_4_ >> 8 & 255);
        int blue = (int)(p_78258_4_ & 255);
        int alpha = (int)(p_78258_4_ >> 24 & 255);
         return new Color(red,green,blue,alpha);
	}
	
	public static int gethex(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }
	
	public static int getMs(int ms, double[] mul) 
	{
		Color c = getColorFromMsAcess(ms);
		double rmul = mul[0];
		double gmul = mul[1];
		double bmul = mul[2];
		int r = (int)(c.getRed() * rmul);
		int g = (int)(c.getGreen() * gmul);
		int b = (int)(c.getBlue() * bmul);
		if(r >= 255)
			r = 255;
		if(g >= 255)
			g = 255;
		if(b >= 255)
			b = 255;//If it's greater then white return white

		return gethex(r, g, b, 0);
	}

	public static boolean isSpecialChar(char c){
		return SPECIALCHARS.contains("" + c);
	}
	
	public static boolean isStringAlphaNumeric(String str){
		str = toWhiteSpaced(str);
		for(char c : str.toCharArray())
			if(!isCharAlphaNumeric(c))
				return false;
		return true;
	}
	public static boolean containsAlphaNumeric(String str){
		str = toWhiteSpaced(str);
		for(char c : str.toCharArray())
			if(isCharAlphaNumeric(c))
				return true;
		return false;
	}
	/**
	 * Ejects a string that is whitespaced
	 * @param s
	 * @return
	 */
	public static String toWhiteSpaced(String s)
	{
		return s.replaceAll("\\s+", "");
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
	
	public static void writeToClipboard(String s, ClipboardOwner owner) 
	{
		if(s == null)
			s = "null";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    Transferable transferable = new StringSelection(s);
	    clipboard.setContents(transferable, owner);
	}
	
	public static Object[] toStaticArray(List list){
		Object[] li = new Object[list.size()];
		for(int i=0;i<list.size();i++)
			li[i] = list.get(i);
		return li;
	}
	public static String[] toStaticStringArray(List list){
		String[] li = new String[list.size()];
		for(int i=0;i<list.size();i++)
			li[i] = list.get(i).toString();
		return li;
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
	
	public static <T extends Object> List<T> staticToArray(Object[] objs)
	{
		List<T> list = new ArrayList();
		for(Object obj : objs)
			list.add((T)obj);
		return list;
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
	public static ArrayList copyArrays(List li) 
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
	public static void removeKeyMemoryLoc(HashMap map, Object key) {
		Iterator it = map.keySet().iterator();
		while(it.hasNext())
		{
			Object obj2 = it.next();
			if(key == obj2)
				it.remove();
		}
	}
	@SuppressWarnings("rawtypes")
	public static Object getMemoryLocKey(HashMap map, Object value) {
		Iterator it = map.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry) it.next();
			if(entry.getValue() == value)
				return entry.getKey();
		}
		return null;
	}

	public static char toUpperCaseChar(char c) {
		return ("" + c).toUpperCase().charAt(0);
	}
	public static String getIntsAsString(int[] ints) {
		String str = "";
		for(int i : ints)
			str += "" + i + ",";
		return str.substring(0, str.length()-1);
	}
	public static List asList(Set set) {
		if(set == null)
			return null;
		List list = new ArrayList();
		for(Object obj : set)
			list.add(obj);
		return list;
	}
	/**
	 * returns name from first index till it disovers a dot
	 * @param file
	 * @return
	 */
	public static String getFileTrueDisplayName(File file) {
		return file.getName().split("\\.")[0];
	}
	public static <T extends Object> ArrayList<T> asArray(Object... staticArr) {
		ArrayList<T> list = new ArrayList();
		for(int i=0;i<staticArr.length;i++)
			list.add((T) staticArr[i]);
		return list;
	}
	public static boolean isStringNullOrEmpty(String string) {
		if(string == null || string.isEmpty())
			return true;
		return false;
	}
	/**
	 * Equivalent to Files.readAllLines() but, works way faster
	 */
	public static List<String> getFileLines(File f,boolean utf8)
	{
		BufferedReader reader = null;
		List<String> list = null;
		try
		{
			if(!utf8)
			{
				reader = new BufferedReader(new FileReader(f));//says it's utf-8 but, the jvm actually specifies it even though the lang settings in a game might be different
			}
			else
			{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),StandardCharsets.UTF_8) );
			}
			
			list = new ArrayList();
			String s = reader.readLine();
			
			if(s != null)
			{
				list.add(s);
			}
			
			while(s != null)
			{
				s = reader.readLine();
				if(s != null)
				{
					list.add(s);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				try 
				{
					reader.close();
				} catch (IOException e) 
				{
					System.out.println("Unable to Close InputStream this is bad");
				}
			}
		}
		
		return list;
	}
	
	/**
	 * Overwrites entire file default behavior no per line modification removal/addition
	 */
	public static void saveFileLines(List<String> list,File f,boolean utf8)
	{
		BufferedWriter writer = null;
		try
		{
			if(!utf8)
			{
				writer = new BufferedWriter(new FileWriter(f));
			}
			else
			{
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),StandardCharsets.UTF_8 ) );
			}
			
			for(String s : list)
			{
				writer.write(s + "\r\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(writer != null)
			{
				try
				{
					writer.close();
				}
				catch(Exception e)
				{
					System.out.println("Unable to Close OutputStream this is bad");
				}
			}
		}
	}
	public static boolean isStringBoolean(String s) {
		s = s.toLowerCase();
		return s.equals("true") || s.equals("false");
	}
	public static boolean getBoolean(String str) {
		if(!isStringBoolean(str))
			return false;
		return Boolean.parseBoolean(str);
	}
	public static ArrayList toArray(Collection li) {
		ArrayList list = new ArrayList();
		for(Object obj : li)
			list.add(obj);
		return list;
	}
	public static boolean isURL(String url) 
	{
		try 
		{
			URL tst = new URL(url);
			return true;
		} 
		catch (Exception e)
		{
			
		}
		return false;
	}
	public static JSONObject toJsonFrom64(String base64) 
	{
		byte[] out = org.apache.commons.codec.binary.Base64.decodeBase64(base64.getBytes());
		String str = new String(out,StandardCharsets.UTF_8);
		JSONParser parser = new JSONParser();
		try 
		{
			return (JSONObject)parser.parse(str);
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	public static long getDays(long ms) 
	{
		return ms / (1000L*60L*60L*24L);
	}

	public static long getTime(long time) {
		return System.currentTimeMillis()-time;
	}

	public static void saveJSONSafley(JSONObject json, File file) throws IOException 
	{
		createFileSafley(file);
		saveJSON(json,file);
	}

	public static void createFileSafley(File file) throws IOException {
		File parent = file.getParentFile();
		if(!parent.exists())
			parent.mkdirs();
		if(!file.exists())
			file.createNewFile();
	}

	public static void saveJSON(JSONObject json, File file) {
		JavaUtil.saveFileLines(JavaUtil.asArray(new String[]{json.toJSONString()} ), file, true);
	}

	public static Object getFirst(Collection li) {
		for(Object obj : li)
			return obj;
		return null;
	}

	public static Set<Integer> asSet(Object... obj) {
		Set set = new HashSet();
		for(Object o : obj)
			set.add(o);
		return set;
	}

	public static void populateStatic(Object[] locs, List names) {
		for(int i=0;i<names.size();i++)
			locs[i] = names.get(i);
	}

	public static JSONObject getJsonFromString(String string) {
		JSONParser p = new JSONParser();
		try {
			return (JSONObject) p.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static boolean isClassExtending(Class<? extends Object> base, Class<? extends Object> toCompare) 
	{
		return base.isAssignableFrom(toCompare);
	}
	
	public static List<String> getFileLines(String inputStream) 
	{
		BufferedReader reader = null;
		List<String> list = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(ConfigBase.class.getClassLoader().getResourceAsStream(inputStream),StandardCharsets.UTF_8));
			list = new ArrayList<String>();
			String s = reader.readLine();
			
			if(s != null)
			{
				list.add(s);
			}
			
			while(s != null)
			{
				s = reader.readLine();
				if(s != null)
				{
					list.add(s);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				try 
				{
					reader.close();
				} catch (IOException e) 
				{
					System.out.println("Unable to Close InputStream this is bad");
				}	
			}
		}
		return list;
	}
	/**
	 * @return the char id based on the generic number object
	 */
	public static char getNumId(Number obj) 
	{
		if(obj instanceof Integer || obj instanceof IntObj)
		{
			return 'i';
		}
		else if(obj instanceof Long || obj instanceof LongObj)
		{
			return 'l';
		}
		else if(obj instanceof Short || obj instanceof ShortObj)
		{
			return 's';
		}
		else if(obj instanceof Byte || obj instanceof ByteObj)
		{
			return 'b';
		}
		else if(obj instanceof Float || obj instanceof FloatObj)
		{
			return 'f';
		}
		else if(obj instanceof Double || obj instanceof DoubleObj)
		{
			return 'd';
		}
		return ' ';
	}
	
	public static int findFirstChar(int index,String str, char c) 
	{
		for(int j=index;j<str.length();j++)
			if(str.charAt(j) == c)
				return j;
		return -1;
	}
	/**
	 * get the id from the string to parse
	 * @return ' ' if none is found
	 */
	public static char getNumId(String str) {
		str = str.trim();
		String last = "" + str.charAt(str.length()-1);
		if(numberIds.contains(last))
			return last.toLowerCase().charAt(0);
		return ' ';
	}
	/**
	 * an optimized way to split a string from it's first instanceof a char
	 */
	public static String[] splitFirst(String s,char reg)
	{
		String[] parts = new String[2];
		for(int i=0;i<s.length();i++)
		{
			char c = s.charAt(i);
			if(c == reg)
			{
				parts[0] = s.substring(0, i);
				parts[1] = s.substring(i+1, s.length());
				break;
			}
		}
		if(parts[0] == null)
			return new String[]{s};
		return parts;
	}
	public static String parseQuotes(String s, int index,String q) 
	{
		if(index == -1)
			return "";
		char lquote = q.charAt(0);
		char rquote = q.length() > 1 ? q.charAt(1) : lquote;
		
		String strid = "";
		int quote = 0;
		for(int i=index;i<s.length();i++)
		{
			if(quote == 2)
				break; //if end of parsing object stop loop and return getParts(strid,":");
			char tocompare = s.charAt(i);
			boolean contains = tocompare == lquote && quote == 0 || tocompare == rquote;
			
			if(contains)
				quote++;
			if(!contains && quote > 0)
				strid += tocompare;
		}
		return strid;
	}
	public static boolean isStringNum(String s)
	{
		String valid = "1234567890.-";
		String valid_endings = numberIds;//byte,short,long,float,double,int
		String check = ".";
		int indexdot = 0;
		if(s.indexOf('.') == 0 || s.indexOf('.') == s.length() - 1 || s.indexOf('-') > 0)
			return false;
		for(int i=0;i<s.length();i++)
		{
			String character = s.substring(i, i+1).toLowerCase();
			boolean lastindex = i == s.length() -1;
			if(check.contains(character))
			{
				if(character.equals("."))
					indexdot++;
				
				if(indexdot > 1)
					return false;
			}
			if(!valid.contains(character))
			{
				if(i + 1 < s.length())
					return false;
				if(lastindex && valid_endings.contains(character) )
				{
					return character.equals("d") || character.equals("f");
				}
			}
		}
		return true;
	}
	/**
	 * create folders for file
	 */
	public static void createFolders(File file) 
	{
		File parent = file.getParentFile();
		if(!parent.exists())
			file.getParentFile().mkdirs();
	}
	public static List<String> asStringList(String[] str) {
		List list = new ArrayList(str.length);
		for(String s : str)
			list.add(s);
		return list;
	}
	public static List<ResourceLocation> stringToLocArray(String[] list) {
		List<ResourceLocation> locs = new ArrayList();
		for(String str : list)
			locs.add(new ResourceLocation(str));
		return locs;
	}
	
}
