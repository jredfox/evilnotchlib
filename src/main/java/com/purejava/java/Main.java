package com.purejava.java;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;
import java.util.Collection;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.ralleytn.simple.json.JSONObject;
import org.ralleytn.simple.json.JSONParseException;
import org.ralleytn.simple.json.JSONParser;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.main.skin.SkinEntry;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.google.common.collect.Iterables;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void main(String[] args) throws IOException, JSONParseException, InterruptedException 
	{
		SkinCache.init();
		for(int i=0;i<1000;i++)
		{
//			JSONObject b = SkinCache.INSTANCE.getMojangProfile("4223a2b1ed374577adb2d18eca6411f4");
//			JSONObject b = SkinCache.INSTANCE.getPlayerDBJSON("jredfox");
//			Thread.sleep(5000L);
			SkinCache.INSTANCE.getMojangUUID("jredfox2");
		}
	}

	public static void RefreshSkin() {
		if (shouldRefreshSkin()) {
			System.out.println("Refreshing Skin");
		} else {
			System.out.println("Not Refreshing SKIN");
		}
	}

	public static boolean shouldRefreshSkin() {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			Session session = mc.getSession();
			PropertyMap prop = mc.getProfileProperties();
			Collection<Property> textureMap = prop.get("textures");
			Property p = (Property) JavaUtil.getFirst(textureMap);
			// missing or corrupted textures
			if (p == null || p.getValue() == null)
				return true;

			String base64str = p.getValue();
			JSONObject jprops = JavaUtil.toJsonFrom64(base64str);

			// correcupted base64 json
			if (jprops == null)
				return true;

			JSONObject jtexture = jprops.getJSONObject("textures");

			return false;
		}
		// If Exception occurs getting property map then the property map was
		// null or corrupted Refresh the skin
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void fill(String username) {
		PropertyMap properties = new PropertyMap();
		JSONObject json = getPlayerDBJSON(username);
		if (json == null) {
			fillMojang(username);// use mojang as a fallback even though they
									// can lock you out for 30s
			return;
		}

		String uuid = json.getString("id").replace("-", "");
		String orguuid = uuid.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");// Taken
																											// from
																											// UUIDTypeAdapter#fromString

		// Create the decompiled JSON
		JSONObject props = new JSONObject();
		props.put("timestamp", System.currentTimeMillis() - 30000);
		props.put("profileId", uuid);
		props.put("profileName", username);
		props.put("signatureRequired", false);
		JSONObject textures = new JSONObject();
		props.put("textures", textures);

		// Use craftar.com when possible as their cache is updated every 20-60
		// minuets
		if (JavaUtil.returnFalse() && JavaUtil.isOnline("crafatar.com")) {
			// Add the skin
			JSONObject skin = new JSONObject();
			skin.put("url", "https://crafatar.com/skins/" + uuid);
			textures.put("SKIN", skin);

			// Add a cape (crafatar will always be within 60 minuets while
			// playerdb could be days behind)
			if (HasCraftarCape(uuid)) {
				JSONObject cape = new JSONObject();
				cape.put("url", "https://crafatar.com/capes/" + orguuid);
				textures.put("CAPE", cape);
			}
		} else {
			String db64 = json.getJSONArray("properties").getJSONObject(0).getString("value");
			JSONObject db = JavaUtil.toJsonFrom64(db64).getJSONObject("textures");

			// Add a skin
			JSONObject skin = new JSONObject();
			skin.put("url", db.getJSONObject("SKIN").getString("url"));
			textures.put("SKIN", skin);

			// Add a cape
			JSONObject dbcape = db.getJSONObject("CAPE");
			if (dbcape != null) {
				JSONObject cape = new JSONObject();
				cape.put("url", dbcape.getString("url"));
				textures.put("CAPE", cape);
			}
		}

		// update properties
		String base64json = Base64.encodeBase64String(JavaUtil.toPrettyFormat(props.toString()).getBytes());
		properties.removeAll("textures");
		properties.put("textures", new Property("textures", base64json));

		if (Minecraft.getMinecraft() != null) {
			ReflectionUtil.setObject(Minecraft.getMinecraft().getSession(), null, Session.class, "properties");
			ReflectionUtil.setFinalObject(Minecraft.getMinecraft(), properties, Minecraft.class,
					new MCPSidedString("profileProperties", "field_181038_N").toString());
			Minecraft.getMinecraft().getSession().setProperties(properties);
		}

		System.out.println(base64json);
	}

	public static boolean HasCraftarCape(String uuid) {
		URLConnection obj = null;
		BufferedReader stream = null;
		try {
			URL cape = new URL("https://crafatar.com/capes/" + uuid);
			obj = cape.openConnection();
			obj.setRequestProperty("User-Agent", "Mozilla");
			obj.setConnectTimeout(3500);
			stream = new BufferedReader(new InputStreamReader(obj.getInputStream()));
			return true;
		} catch (Exception e) {

		} finally {
			IOUtils.closeQuietly(stream);
			if (obj instanceof HttpURLConnection)
				((HttpURLConnection) obj).disconnect();
		}
		return false;
	}

	public static JSONObject getPlayerDBJSON(String username) {
		BufferedReader stream = null;
		URLConnection con = null;
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeQuietly(stream);
			if (con instanceof HttpURLConnection)
				((HttpURLConnection) con).disconnect();
		}
	}

	private static void fillMojang(String username) {
		String uuid = getMojangUUID(username);
	}

	public static String getMojangUUID(String username) {
		BufferedReader stream = null;
		try {
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			stream = new BufferedReader(new InputStreamReader(url.openStream()));
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(stream);
			String id = (String) json.get("id");
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

}
