package com.evilnotch.lib.minecraft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.eventhandler.TickServerEvent;
import com.evilnotch.lib.minecraft.network.NetWorkHandler;
import com.evilnotch.lib.minecraft.network.packet.PacketClipBoard;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PointId;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class PlayerUtil {
    
    /**
     * send to clipboard with both url and message being the same color
     */
    public static void sendClipBoard(EntityPlayer p, String pc, String c, String message, String url, boolean copyURL)
    {
    	sendClipBoard(p, pc, c, c, message, url, copyURL);
    }
    
    public static void sendClipBoard(EntityPlayer p, String pc, String c, String urlc, String message, String url, boolean copyURL)
    {
    	sendClipBoard(p, pc + p.getName() + " " + c + message, urlc + url);
    	if(copyURL)
    		copyClipBoard(p, url);
    }
	
    /**
     * default url vanilla format
     */
    public static void sendClipBoard(EntityPlayer p, String message, String url)
    {
    	sendURL(p, message, url, ClickEvent.Action.SUGGEST_COMMAND);
    }
    
    public static void sendURL(EntityPlayer p, String message, String url, ClickEvent.Action action)
    {
    	TextComponentString str = new TextComponentString(message + " " + EnumChatFormatting.UNDERLINE + url);
    	if(action != null)
    	{
    		if(action == ClickEvent.Action.OPEN_URL && !url.contains("http"))
    			url = "http://" + url;
    		str.getStyle().setClickEvent(new ClickEvent(action, url));
    	}
    	p.sendMessage(str);
    }
	
    public static void printChat(EntityPlayer player,String c_player, String c_msg, String message)
	{
		player.sendMessage(new TextComponentString(c_player + player.getName() + " " + c_msg + message) );
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
    
	/**
	 * this alters both the uuid of the player object and the gameprofile uuid
	 */
	public static void setPlayerUUID(EntityPlayer player,UUID uuid) 
	{
		ReflectionUtil.setFinalObject(player.getGameProfile(), uuid, GameProfile.class, "id");
		EntityUtil.setEntityUUID(player, uuid);
	}

	public static void kickPlayer(EntityPlayerMP p, int ticks,String msg) 
	{
		TickServerEvent.kicker.put(p, new PointId(0,ticks,msg) );
	}

	public static boolean isPlayerOwner(EntityPlayerMP player)
	{
		return player.getName().equals(player.getServer().getServerOwner());
	}
	
	public static boolean isPlayerOwner(String name, MinecraftServer server)
	{
		return name.equals(server.getServerOwner());
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

	public static EntityPlayer getClientPlayer() {
		return ClientProxy.getPlayer();
	}

//	public static List<EntityPlayerMP> getWatchingPlayers(EntityPlayerMP p, boolean addSelf) 
//	{
//		List<EntityPlayerMP> players = new ArrayList<>();
//		if(addSelf)
//			players.add(p);
//    	PlayerChunkMap playerChunkMap = p.getServerWorld().getPlayerChunkMap();
//    	ChunkPos chunkPos = new ChunkPos(p.chunkCoordX, p.chunkCoordZ);
//    	PlayerChunkMapEntry entry = playerChunkMap.getEntry(chunkPos.x, chunkPos.z);
//    	if(entry != null)
//    	{
//    		players.addAll(entry.players);
//    	}
//    	return players;
//	}

    /** The default skin for the Steve model. */
	public static final ResourceLocation STEVE = new ResourceLocation("textures/entity/steve.png");
    /** The default skin for the Alex model. */
	public static final ResourceLocation ALEX = new ResourceLocation("textures/entity/alex.png");
    public static ResourceLocation getDefaultSkin(UUID uuid)
    {
    	return isAlex(uuid) ? ALEX : STEVE;
    }
    
    /**
     * Checks if a players skin model is slim or the default. The Alex model is slime while the Steve model is default.
     */
    public static boolean isAlex(UUID uuid)
    {
        return (uuid.hashCode() & 1) == 1;
    }

}
