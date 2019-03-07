package com.purejava.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;
import org.ralleytn.simple.json.JSONParseException;
import org.ralleytn.simple.json.JSONParser;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args) throws IOException, JSONParseException
	{
		JSONParser parser = new JSONParser();
		System.out.println("Done:" + parser.parseJSONObject("{\"key\":1, \"key\":2}"));
	}

}
