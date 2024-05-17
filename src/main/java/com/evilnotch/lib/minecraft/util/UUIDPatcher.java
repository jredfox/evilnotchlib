package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
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
		PlayerUtil.patchLANSkin(profile);
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

}
