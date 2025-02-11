package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.skin.SkinEvent;
import com.evilnotch.lib.main.skin.SkinEvent.GameProfileEvent;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.evilnotch.lib.minecraft.auth.FakeGameProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.World;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

public class UUIDPatcher {
	
	public static final String VERSION = "2.0.0";

	public static GameProfile patch(GameProfile old) 
	{
		//Handle FakePlayer
		if(old instanceof FakeGameProfile || old.getName() != null && old.getName().startsWith("["))
			return old;
		
    	if(VanillaBugFixes.playerDataNames == null)
    	{
    		String err = "WorldDir is NULL! Cannot Patch UUID:" + old.getId() + " for User:" + old.getName();
    		if(Config.UUIDCrashOnFailure)
    			throw new RuntimeException(err);
    		else
    			System.err.println(err);
    		return old;
    	}
    	
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
		File u = new File(VanillaBugFixes.playerDataNames, user.toLowerCase() + ".dat");
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
	
	/**
	 * Updates a Skin BaseCode64 Texture Payload into the GameProfile
	 */
	public static void patchSkin(GameProfile profile, String payload)
	{
		SkinEvent.GameProfileEvent e = new GameProfileEvent(profile, payload);
		MinecraftForge.EVENT_BUS.post(e);
		e.update();
	}
	
	/**
	 * Simply Posting the Event Patches the UUID and Username for the GameProfile's Properties in Textures
	 * Then it Calls {@link GameProfileEvent#update()} to Sync any Texture Changes
	 * Empty, Null Skins or http://textures.minecraft.net/texture/$null will result in the default skin based on your UUID
	 */
	public static void patchSkin(GameProfile profile)
	{
		SkinEvent.GameProfileEvent e = new GameProfileEvent(profile);
		MinecraftForge.EVENT_BUS.post(e);
		e.update();
	}
	
	/**
	 * Sets the TMP LoginHook's NBT to the GameProfile
	 */
	public static void setLoginHooks(GameProfile profile, NBTTagCompound nbt)
	{
		if(profile instanceof EvilGameProfile)
			((EvilGameProfile)profile).iloginhooks = nbt;
	}

	public static GameProfile patchFake(GameProfile name) 
	{
		return new FakeGameProfile(name.getId(), name.getName());
	}
	
}
