package com.evilnotch.lib.minecraft.util;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.main.eventhandler.TickServerEvent;
import com.evilnotch.lib.main.eventhandler.VanillaBugFixes;
import com.evilnotch.lib.minecraft.event.EventCanceler;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketClipBoard;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PointId;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class PlayerUtil {
	
	/**
	 * used on player login so it doesn't parse twice will self empty login complete so don't expect data to be here long
	 */
    public static HashMap<UUID,NBTTagCompound> nbts = new HashMap();
    
    public static void sendClipBoard(EntityPlayer p, String pc, String c, String urlc, String messege, String url, boolean copyURL)
    {
    	sendClipBoard(p, pc + p.getName() + " " + c + messege, urlc + url);
    	if(copyURL)
    		copyClipBoard(p, url);
    }
	
    /**
     * default url vanilla format
     */
    public static void sendClipBoard(EntityPlayer p, String messege, String url)
    {
    	sendURL(p, messege, url, ClickEvent.Action.SUGGEST_COMMAND);
    }
    
    public static void sendURL(EntityPlayer p, String messege, String url, ClickEvent.Action action)
    {
    	TextComponentString str = new TextComponentString(messege + " " + EnumChatFormatting.UNDERLINE + url);
    	if(action != null)
    	{
    		if(action == ClickEvent.Action.OPEN_URL && !url.contains("http"))
    			url = "http://" + url;
    		str.getStyle().setClickEvent(new ClickEvent(action, url));
    	}
    	p.sendMessage(str);
    }
	
    public static void printChat(EntityPlayer player,String c_player, String c_msg, String messege)
	{
		player.sendMessage(new TextComponentString(c_player + player.getName() + " " + c_msg + messege) );
	}
    
    public static void copyClipBoard(EntityPlayer p, String url)
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
		return getPlayerFileNBT(getPlayerFile(player, uuidDir),player.mcServer.getPlayerList());
	}
	
	/**
	 * Gets cached playerdata from filename don't call unless you have the exact string
	 */
	public static NBTTagCompound getPlayerFileNBT(File f, PlayerList list) 
	{
		NBTTagCompound nbt = NBTUtil.getFileNBTSafley(f);
		if(nbt != null)
		{
			nbt = getFixedPlayerNBT(list,nbt);
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
	
	/**
	 * this alters both the uuid of the player object and the gameprofile uuid
	 */
	public static void setPlayerUUID(EntityPlayer player,UUID uuid) 
	{
		ReflectionUtil.setFinalObject(player.getGameProfile(), uuid, GameProfile.class, "id");
		EntityUtil.setEntityUUID(player, uuid);
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
		return Minecraft.getMinecraft().player;
	}

	public static void kickPlayer(EntityPlayerMP p, int ticks,String msg) 
	{
		TickServerEvent.kicker.put(p, new PointId(0,ticks,msg) );
	}

	public static boolean isPlayerOwner(EntityPlayerMP player)
	{
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
     * Call this after player is removed from the world
     */
    public static void removeDragonBars(World end) 
    {
		DragonFightManager fightManager = ((WorldProviderEnd)end.provider).getDragonFightManager();
		fightManager.updateplayers();
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
		TickServerEvent.msgs.add(msg);
	}
	
	/**
	 * swings the hand of the player like using an item
	 */
	public static void swingHand(EntityPlayer p, EnumHand hand) 
	{
		p.swingArm(hand);
	}
	
	/**
	 * if you override function of x item then call this so the offhand doesn't fire next
	 */
	public static void cancelRightClickBlock(PlayerInteractEvent.RightClickBlock e, boolean b) 
	{
		e.setCanceled(b);
		if(e.getHand() == EnumHand.MAIN_HAND)
		{
			MinecraftUtil.cancelEvent(e, e.getClass(), b);
		}
	}
	/**
	 * call this when your done overriding an items function in RightClickBlockEvent
	 */
	public static void rightClickBlockSucess(RightClickBlock e, EntityPlayer p) 
	{
//		cancelRightClickBlock(e, true);
  		e.setResult(Result.ALLOW);
	}
	
	/**
	 * don't hard code consuming items everywhere use this
	 */
	public static void consumeItem(EntityPlayer p, EnumHand hand, ItemStack stack) 
	{
		if (!p.capabilities.isCreativeMode)
		{
			stack.shrink(1);
			if(stack.isEmpty())
			{
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(p, stack, hand);
			}
		}
	}

}
