package com.purejava.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args) throws IOException
	{
		JSONObject object = new JSONObject();
		object.put("a","b");
		object.put("b", "c");
		object.put("ints", new JSONArray(new int[]{0,1,2,3,4,5}));
		object.put("d", null);
		FileWriter writer = new FileWriter(new File("C:/Users/jredfox/Desktop/tst.txt"));
		object.write(writer);
		writer.close();
		System.out.println("Done:" + object.toString());
	}

}
