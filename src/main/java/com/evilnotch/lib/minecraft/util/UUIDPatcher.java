package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.mojang.authlib.GameProfile;

import net.minecraft.nbt.NBTTagCompound;

public class UUIDPatcher {

	public static GameProfile patch(GameProfile old) 
	{
		String user = old.getName();
		UUID id = getUUID(old);
		UUID cached = getCachedUUID(id, user);
		if(cached != null && !id.equals(cached))
		{
			System.out.println("Patched " + user + " UUID Set From:" + id + " To:" + cached);
			id = cached;
		}
		EvilGameProfile profile = new EvilGameProfile(id, old);
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
			return NBTUtil.getFileNBT(u).getUniqueId("uuid");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			u.delete();
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

}
