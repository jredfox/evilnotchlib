package com.purejava.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;
import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.ralleytn.simple.json.JSONArray;
import org.ralleytn.simple.json.JSONObject;
import org.ralleytn.simple.json.JSONParseException;
import org.ralleytn.simple.json.JSONParser;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.RomanNumerals;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class MainJava {
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args) throws IOException, JSONParseException
	{
		int a = PlayerUtil.nbts.size();
		long ms = System.currentTimeMillis();
		fill("Miclee");
	}
	
	public static void fill(String username)
	{
		PropertyMap properties = new PropertyMap();
		JSONObject json = getPlayerDBJSON(username);
		if(json == null)
		{
			fillMojang(username);//use mojang as a fallback even though they can lock you out for 30s
			return;
		}
		
		String uuid = json.getString("id").replace("-", "");
		
		//Create the decompiled JSON
		JSONObject props = new JSONObject();
		props.put("timestamp", System.currentTimeMillis() - 30000);
		props.put("profileId", uuid);
		props.put("profileName", username);
		props.put("signatureRequired", false);
		JSONObject textures = new JSONObject();
		props.put("textures", textures);
		
		//Use craftar.com when possible as their cache is updated every 20-60 minuets
		if(JavaUtil.isOnline("crafatar.com"))
		{
			//Add the skin
			JSONObject skin = new JSONObject();
			skin.put("url", "https://crafatar.com/skins/" + uuid);
			textures.put("SKIN", skin);
			
			//Add a cape (crafatar will always be within 60 minuets while playerdb could be days behind)
			if(HasCraftarCape(uuid))
			{
				JSONObject cape = new JSONObject();
				cape.put("url", "https://i.imgur.com/3Lm3rfx.png");
				textures.put("CAPE", cape);
			}
		}
		else
		{
			String db64 = json.getJSONArray("properties").getJSONObject(0).getString("value");
			JSONObject db = JavaUtil.toJsonFrom64(db64).getJSONObject("textures");
			
			//Add a skin
			JSONObject skin = new JSONObject();
			skin.put("url", db.getJSONObject("SKIN").getString("url"));
			textures.put("SKIN", skin);
			
			//Add a cape
			JSONObject dbcape = db.getJSONObject("CAPE");
			if(dbcape != null)
			{
				JSONObject cape = new JSONObject();
				cape.put("url", dbcape.getString("url"));
				textures.put("CAPE", cape);
			}
		}
		
		//update properties
		String base64json = Base64.encodeBase64String(props.toString().getBytes());
		properties.removeAll("textures");
		properties.put("textures", new EvilProps("textures", base64json));
		
		ReflectionUtil.setObject(Minecraft.getMinecraft().getSession(), null, Session.class, "properties");
		ReflectionUtil.setFinalObject(Minecraft.getMinecraft(), properties, Minecraft.class, "profileProperties");
		Minecraft.getMinecraft().getSession().setProperties(properties);
	}
	
	public static class EvilProps extends Property{

		public EvilProps(String name, String value) {
			this(name, value, null);
		}
		
		public EvilProps(String name, String value, String signature) {
			super(name, value, signature);
		}
		
		@Override
		 public boolean isSignatureValid(final PublicKey publicKey) {
			 return true;
		 }

	}

	public static boolean HasCraftarCape(String uuid)
	{
		URLConnection obj = null;
		BufferedReader stream = null;
		try
		{
			URL cape = new URL("https://crafatar.com/capes/" + uuid);
			obj = cape.openConnection();
			obj.setRequestProperty("User-Agent", "Mozilla");
			obj.setConnectTimeout(3500);
			stream = new BufferedReader(new InputStreamReader(obj.getInputStream()));
			return true;
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			IOUtils.closeQuietly(stream);
			if(obj instanceof HttpURLConnection)
				((HttpURLConnection)obj).disconnect();
		}
		return false;
	}

	public static JSONObject getPlayerDBJSON(String username)
	{
		BufferedReader stream = null;
		URLConnection con = null;
		try
		{
			URL url = new URL("https://playerdb.co/api/player/minecraft/" + username);
			con = url.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla");
			con.setConnectTimeout(3500);
			stream = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(stream);
			JSONObject data = json.getJSONObject("data");
			JSONObject player = data.getJSONObject("player");
			return player;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(stream);
			if(con instanceof HttpURLConnection)
				((HttpURLConnection)con).disconnect();
		}
	}
	
	private static void fillMojang(String username)
	{
		String uuid = getMojangUUID(username);
	}
	
	public static String getMojangUUID(String username)
	{
		BufferedReader stream = null;
		try
		{
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			stream = new BufferedReader(new InputStreamReader(url.openStream()));
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(stream);
			String id = (String) json.get("id");
			return id;
		}
		catch (Exception e)
		{
			return null;
		}
		finally
		{
			IOUtils.closeQuietly(stream);
		}
	}

}
