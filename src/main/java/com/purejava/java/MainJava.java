package com.purejava.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import net.minecraft.util.ResourceLocation;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args) throws IOException
	{
		JsonObject json = new JsonObject();
		json.addProperty("a", "b");
		json.addProperty("c", "d");
		json.addProperty("int", 1000);
		json.addProperty("boolean", true);
		json.addProperty("null", (String)null);
		Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
		System.out.println(gson.toJson(json));
	}

}
