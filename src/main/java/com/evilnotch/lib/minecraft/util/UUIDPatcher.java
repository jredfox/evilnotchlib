package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ralleytn.simple.json.JSONObject;

import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.skin.SkinCache.EvilProperty;
import com.evilnotch.lib.main.skin.SkinEntry;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.evilnotch.lib.util.JavaUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.World;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.event.ForgeEventFactory;

public class UUIDPatcher {
	
	public static final String VERSION = "2.0.0";

	public static GameProfile patch(GameProfile old) 
	{
		String user = old.getName();
		UUID id = getUUID(old);
		UUID cached = getCachedUUID(id, user);
		if(cached != null && !id.equals(cached))
		{
			System.out.println("Patched " + user + " UUID From:" + id + " To:" + cached);
			id = cached;
		}
		
		EvilGameProfile profile = new EvilGameProfile(id, old);
		patchSkin(profile);
		return profile;
	}

	public static GameProfile patchCheck(GameProfile profile)
	{
		return profile instanceof EvilGameProfile ? profile : patch(profile);
	}
	
	/**
	 * Get the Player's Cached UUID from previous logins by providing current uuid and username
	 */
    public static UUID getCachedUUID(UUID id, String user)
    {
		File u = new File(VanillaBugFixes.playerDataNames, user + ".dat");
		if(!u.exists())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setUniqueId("uuid", id);
			NBTUtil.updateNBTFileSafley(u, nbt);
			return id;
		}
		
		try
		{
			NBTTagCompound nbt = NBTUtil.getFileNBT(u);
			
			//Detect UUIDPatcher 1.0 or missing UUID
			if(!nbt.hasUniqueId("uuid"))
			{
				//Get UUID as String
				if(nbt.hasKey("uuid", 8))
				{
					System.out.println("Converting Cached UUID to UUIDPatcher " + VERSION + " " + user);
					UUID oldid = UUIDTypeAdapter.fromString(nbt.getString("uuid").replace("-", ""));
					nbt.setUniqueId("uuid", oldid);
					NBTUtil.updateNBTFileSafley(u, nbt);
					return oldid;
				}
				else
				{
					System.err.println("Cached UUID Missing:" + u.getAbsolutePath());
					nbt.setUniqueId("uuid", id);
					NBTUtil.updateNBTFileSafley(u, nbt);
					return id;
				}
			}
			
			return nbt.getUniqueId("uuid");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			//delete the corrupted file
			u.delete();
			
			//update the corrupted file
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setUniqueId("uuid", id);
			NBTUtil.updateNBTFileSafley(u, nbt);
			return id;
		}
	}

	/**
     * Gets a players UUID given their GameProfie
     */
    public static UUID getUUID(GameProfile profile)
    {
        UUID uuid = profile.getId();
        return uuid != null ? uuid : UUID.nameUUIDFromBytes(("OfflinePlayer:" + profile.getName()).getBytes(StandardCharsets.UTF_8));
    }

	public static NBTTagCompound getLevelDat(World w, NBTTagCompound level) 
	{
		return w.getGameRules().getBoolean("PlayerLvlDAT") ? level : null;
	}

	/**
	 * if the EvilGameProfile has login NBT return true
	 */
	public static boolean hasLogin(EntityPlayerMP player) 
	{
		GameProfile p = player.getGameProfile();
		return p instanceof EvilGameProfile ? ((EvilGameProfile)p).login != null : false;
	}
	
	/**
	 * get the login NBT without safety checks
	 */
	public static NBTTagCompound getLogin(EntityPlayerMP player)
	{
		return ((EvilGameProfile)player.getGameProfile()).login;
	}
	
	public static void setLogin(EntityPlayerMP player, NBTTagCompound playerNBT)
	{
		GameProfile p = player.getGameProfile();
		if(p instanceof EvilGameProfile)
			((EvilGameProfile)player.getGameProfile()).login = playerNBT;
	}

	public static NBTTagCompound fireLogin(PlayerList list, EntityPlayerMP playerIn)
	{
		EvilGameProfile profile = (EvilGameProfile) playerIn.getGameProfile();
		NBTTagCompound nbt = ((SaveHandler)list.playerDataManager).dataFixer.process(FixTypes.PLAYER, profile.login);
		playerIn.readFromNBT(nbt);
		ForgeEventFactory.firePlayerLoadingEvent(playerIn, list.playerDataManager, playerIn.getCachedUniqueIdString());
		return nbt;
	}
	
	public static void patchSkin(GameProfile profile)
	{
		PropertyMap props = profile.getProperties();
		String payload = getEncode(props);
		if(isSkinEmpty(payload))
			return;
		setSkin(props, patchSkin(profile, payload));
	}

	public static String patchSkin(GameProfile profile, String payload) 
	{
		//do not modify empty skins encoding
		if(isSkinEmpty(payload))
			return SkinEntry.EMPTY_SKIN_ENCODE;
		
		try
		{
			System.out.println("payload:" + payload);
			JSONObject json = JavaUtil.toJsonFrom64(payload);
			json.put("profileId", profile.getId().toString().replace("-", ""));
			json.put("profileName", profile.getName());
			//internal SKIN caps redirect URL meaning
			if(json.containsKey("textures"))
			{
				try
				{
					JSONObject skin = json.getJSONObject("textures").getJSONObject("SKIN");
					if(skin.getString("url").equals("http://textures.minecraft.net/texture/$null"))
					{
						skin.put("url", "http://textures.minecraft.net/texture/" + (PlayerUtil.isAlex(profile.getId()) ? "$alex" : "$steve"));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.err.println("Invalid Textures Payload! Missing SKIN URL:" + payload);
				}
			}
			return JavaUtil.toBase64(json.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void setSkin(PropertyMap props, String skindata) 
	{
		if(skindata == null || skindata.trim().isEmpty())
			return;
		props.removeAll("textures");
		props.put("textures", new EvilProperty("textures", skindata));
	}
	
	private static boolean isSkinEmpty(String payload) 
	{
		return payload == null || payload.isEmpty() || SkinEntry.EMPTY_SKIN_ENCODE.equals(payload);
	}

	public static String getEncode(PropertyMap map) 
	{
		if(map.isEmpty())
			return null;
		Property p = ((Property)JavaUtil.getFirst(map.get("textures")));
		return p == null ? null : p.getValue();
	}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final ResourceLocation fileSteve = new ResourceLocation("minecraft:skins/$steve");
	private static final ResourceLocation fileAlex = new ResourceLocation("minecraft:skins/$alex");
	public static ResourceLocation patchSkinResource(ResourceLocation resource) 
	{
		return resource.equals(fileSteve) ? PlayerUtil.STEVE : resource.equals(fileAlex) ? PlayerUtil.ALEX : resource;
	}
	
}
