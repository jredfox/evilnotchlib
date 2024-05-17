package com.evilnotch.lib.minecraft.proxy;

import java.util.UUID;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.main.eventhandler.ClientEvents;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.minecraft.auth.EvilGameProfile;
import com.evilnotch.lib.minecraft.client.Seeds;
import com.evilnotch.lib.minecraft.command.client.ClientUUID;
import com.evilnotch.lib.minecraft.event.client.UUIDChangeEvent;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.minecraft.util.UUIDPatcher;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends ServerProxy{
	
	@Override
	public void proxyStart()
	{
		LoaderMain.isClient = true;
	}
	
	@Override
	public void preinit(FMLPreInitializationEvent e) 
	{
		super.preinit(e);
		registerEvents();
		this.cacheUUID();
	}
	
	@Override
	public void initMod()
	{
		super.initMod();
	}
	
	@Override
	public void postinit()
	{
		super.postinit();
	}
	
	private void registerEvents() 
	{
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		if(Config.debug)
		{
			GeneralRegistry.registerClientCommand(new ClientUUID());
		}
	}
	
	public static void disconnect()
	{
		clearClientData();
		MainJava.proxy.handleUUIDChange(new PacketUUID(ClientProxy.org.org));//Undo UUID Changes done from connecting to a server
	}
	
	public static void clearClientData()
	{
		TickRegistry.garbageCollectClient();
		Seeds.clearSeeds();
		System.out.println("disconnecting..........................");
	}

	public static EntityPlayer getPlayer() 
	{
		return Minecraft.getMinecraft().player;
	}

	public static boolean isCtrlDown() 
	{
		return GuiScreen.isCtrlKeyDown();
	}

	public static boolean isCurrentThread() 
	{
		return Minecraft.getMinecraft().isCallingFromMinecraftThread();
	}
	
	/**
	 * stores the original UUID and name of the client
	 */
	public static EvilGameProfile org = null;
	
	public static void cacheUUID()
	{
		if(org != null)
			return;
		
		GameProfile p = Minecraft.getMinecraft().getSession().getProfile();
		UUID id = p.getId();
		org = new EvilGameProfile(UUIDPatcher.getUUID(p), p);
		org.getProperties().removeAll("textures");
	}
	
	@Override
	public void handleUUIDChange(PacketUUID message) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Minecraft mc = Minecraft.getMinecraft();
			Session session = mc.getSession();
			UUID org = UUIDPatcher.getUUID(session.getProfile());
			UUID uuid = message.uuid;
			
			//Set UUID of the Session
			ReflectionUtil.setFinalObject(session, uuid.toString().replace("-", ""), Session.class, new MCPSidedString("playerID", "field_148257_b").toString());//sets uuid of the session
			
			//After Changing the Sessions UUID cache it for further use
			GameProfile profile = session.getProfile();
			
			//Sync GameProfile with Player's
			if(mc.myNetworkManager != null && mc.myNetworkManager.getNetHandler() instanceof NetHandlerLoginClient)
			{
				NetHandlerLoginClient nc = (NetHandlerLoginClient) mc.myNetworkManager.getNetHandler();
				nc.gameProfile = profile;
			}
			
			EntityPlayerSP player = mc.player;
			if(player != null)
			{
				//Set UUID of the Player's profile
				ReflectionUtil.setFinalObject(player.getGameProfile(), uuid, GameProfile.class, "id");
				//Set UUID of the Player
				player.setUniqueId(uuid);
				//Set UUID of the EntityPlayerSP#connection#gameProfile
				if(player.connection != null)
					ReflectionUtil.setFinalObject(player.connection.getGameProfile(), uuid, GameProfile.class, "id");
			}
			
			if(!org.equals(uuid) || player != null && !org.equals(player.getGameProfile().getId()))
			{
				MinecraftForge.EVENT_BUS.post(new UUIDChangeEvent(org, uuid, player));
			}
		});
	}
	
	

}
