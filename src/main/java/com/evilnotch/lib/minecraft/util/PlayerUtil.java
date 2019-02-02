package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketClipBoard;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PointId;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlayerUtil {
	
    /**
     * default url vanilla format
     */
    public static void sendURL(EntityPlayer p ,String messege,String url)
    {
    	TextComponentString str = new TextComponentString(EnumChatFormatting.AQUA + messege + " " + EnumChatFormatting.BLUE + EnumChatFormatting.UNDERLINE + url);
    	str.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    	p.sendMessage(str);
    }
	
    public static void printChat(String c_player, String c_msg,EntityPlayer player, String messege)
	{
		player.sendMessage(new TextComponentString(c_player + player.getName() + " " + c_msg + messege) );
	}
    
    public static void sendClipBoard(String pc,String c,EntityPlayer p ,String messege,String url)
    {
    	sendClipBoard(pc,c,p,messege,url,true);
    }
    public static void sendClipBoard(String pc,String c,EntityPlayer p ,String messege,String url,boolean copyURL)
    {
    	TextComponentString str = new TextComponentString(pc + messege + " " + c + EnumChatFormatting.UNDERLINE + url);
    	str.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, url));
    	p.sendMessage(str);
    	if(copyURL)
    		sendClipBoard(p,url);
    }
    
    public static void sendClipBoard(EntityPlayer p, String url)
    {
    	if(p instanceof EntityPlayerMP)
    	{
            PacketClipBoard packet = new PacketClipBoard(url);
            NetWorkHandler.INSTANCE.sendTo(packet, (EntityPlayerMP)p);
    	}
    	else
    	{
    		JavaUtil.writeToClipboard(url, null);
    	}
    }
    
    public static File getPlayerFile(EntityPlayer player,boolean uuid)
	{
		if(uuid)
			return new File(VanillaBugFixes.playerDataDir,player.getUniqueID().toString() + ".dat");
		else
			return new File(VanillaBugFixes.playerDataNames,player.getName() + ".dat");
	}
    
	public static File getPlayerFile(String username,boolean uuid)
	{
		if(uuid)
			return new File(VanillaBugFixes.playerDataDir,username + ".dat");
		else
			return new File(VanillaBugFixes.playerDataNames,username + ".dat");
	}
	
	/**
	 * Returns the uuidFile or cached file based on uuid boolean
	 * @param player
	 * @param uuid
	 * @return player file
	 */
	public static File getPlayerFileSafley(EntityPlayer player,boolean uuid)
	{
		File file = getPlayerFile(player,uuid);
		if(uuid)
		{
			if(!file.exists())
			{
				NBTTagCompound nbt = EntityUtil.getEntityNBT(player);
				updatePlayerFile(file,nbt);
			}
			return file;
		}
		else
		{
			if(!file.exists())
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("uuid", player.getUniqueID().toString() );
				updatePlayerFile(file,nbt);
			}
			return file;
		}
	}
	
	public static File getPlayerFileNameSafley(GameProfile profile)
	{
		File file = getPlayerFile(profile.getName(),false);
		if(!file.exists())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("uuid", profile.getId().toString() );
			updatePlayerFile(file,nbt);
		}
		return file;
	}
	
	/**
	 * Update Player file
	 */
	public static void updatePlayerFile(File file, NBTTagCompound nbt) 
	{
		NBTUtil.updateNBTFileSafley(file,nbt);
	}
	
	public static NBTTagCompound getPlayerFileNBT(String display,EntityPlayerMP player,boolean uuidDir) 
	{
		return getPlayerFileNBT(display,player.mcServer.getPlayerList(),uuidDir);
	}
	
	/**
	 * Gets cached playerdata from filename don't call unless you have the exact string
	 */
	public static NBTTagCompound getPlayerFileNBT(String display,PlayerList playerlist,boolean uuidDir) 
	{
		FileInputStream stream = null;
		NBTTagCompound nbt = null;
		try
		{
			stream = !uuidDir ? new FileInputStream(new File(VanillaBugFixes.playerDataNames,display + ".dat")) : new FileInputStream(new File(VanillaBugFixes.playerDataDir,display + ".dat"));
			nbt = CompressedStreamTools.readCompressed(stream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			nbt = null;
		}
		finally
		{
			if(stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					System.out.println("unable to close input stream for player:" + display + ".dat");
				}
			}
		}
		//fix player's nbt
		if(nbt != null)
		{
			nbt = getFixedPlayerNBT(playerlist,nbt);
		}
		return nbt;
	}
	
	private static NBTTagCompound getFixedPlayerNBT(PlayerList playerlist,NBTTagCompound nbt) 
	{
		SaveHandler handler = getPlayerDataManager(playerlist);
		nbt = getDataFixer(handler).process(FixTypes.PLAYER, nbt);
		return nbt;
	}

	public static SaveHandler getPlayerDataManager(PlayerList playerList) 
	{
		return (SaveHandler) playerList.playerDataManager;
	}
	
	public static DataFixer getDataFixer(SaveHandler handler) 
	{
		return (DataFixer) handler.dataFixer;
	}
	//Prints Colored Chat from player
	public static void printChat(EntityPlayer player, String color, EnumChatFormatting colormsg, String messege)
	{
		player.sendMessage(new TextComponentString(color + player.getDisplayName() + " " + colormsg + messege));
	}
	
	/**
	 * this alters both the uuid of the player object and the gameprofile uuid
	 */
	public static void setPlayerUUID(EntityPlayer player,UUID uuid) 
	{
		ReflectionUtil.setFinalObject(player.getGameProfile(), uuid, GameProfile.class, "id");
		EntityUtil.setEntityUUID(player,uuid);
	}

	public static UUID getServerPlayerUUID(GameProfile profile) 
	{
		File file = getPlayerFileNameSafley(profile);//updates player file synced to uuid on login
		NBTTagCompound nbt = NBTUtil.getFileNBT(file);
		return UUID.fromString(nbt.getString("uuid"));
	}

    public static void patchUUID(GameProfile gameprofile) 
    {
    	//set the inital value of the player's uuid to what it's suppose to be
        UUID init = EntityPlayer.getUUID(gameprofile);
        UUID actual = getServerPlayerUUID(gameprofile);
        
        if(!actual.toString().equals(init.toString()))
        {
        	System.out.println("Patching Player UUID uuidPlayer:" + gameprofile.getId() + " with uuidServer:" + actual);
    		ReflectionUtil.setFinalObject(gameprofile, actual, GameProfile.class, "id");
    		VanillaBugFixes.playerFlags.add(gameprofile.getName());
        }
	}
    
	public static EntityPlayer getClientPlayer()
	{
		return FMLClientHandler.instance().getClientPlayerEntity();
	}

	public static void kickPlayer(EntityPlayerMP p, int ticks,String msg) 
	{
		LibEvents.kicker.put(p.connection, new PointId(0,ticks,msg) );
	}

	public static boolean isPlayerOwner(EntityPlayerMP player)
	{
		if(!LoaderMain.isClient)
			return false;
		return player.getName().equals(player.getServer().getServerOwner());
	}

	public static TextComponentTranslation msgShutdown = null;
	public static void disconnectPlayer(EntityPlayerMP player,TextComponentString msg) 
	{	
		if(isPlayerOwner(player))
		{
			player.mcServer.initiateShutdown();
			msgShutdown = new TextComponentTranslation(msg.getText(),new Object[0]);
		}
		else
		{
			player.connection.disconnect(new TextComponentTranslation(msg.getText(),new Object[0]) );
		}
	}

	public static ItemStack getActiveItemStack(EntityPlayer p,EnumHand hand) 
	{
		return p.getHeldItem(hand);
	}
	
	public static EntityPlayer getPlayer(String key) 
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.getPlayerList().getPlayerByUsername(key);
	}

	/**
	 * set the game type with keeping previous capabilities
	 */
	public static void setGameTypeSafley(EntityPlayerMP p, GameType gameType) 
	{
		NBTTagCompound nbt = new NBTTagCompound();
		p.capabilities.writeCapabilitiesToNBT(nbt);
	    p.setGameType(gameType);
	    p.capabilities.readCapabilitiesFromNBT(nbt);
	    p.sendPlayerAbilities();
	}

	/**
	 * hides the player for all users
	 */
	public static void hidePlayer(EntityPlayerMP p) 
	{
	    p.getServerWorld().getEntityTracker().removePlayerFromTrackers(p);
	    p.getServerWorld().getEntityTracker().untrack(p);
	}
	
	/**
	 * shows player for all users
	 */
	public static void showPlayer(EntityPlayerMP p) 
	{
	       p.getServerWorld().getEntityTracker().track(p);
	}
	
	public static void broadCastMessege(String msg) 
	{
		LibEvents.msgs.add(msg);
	}

}
