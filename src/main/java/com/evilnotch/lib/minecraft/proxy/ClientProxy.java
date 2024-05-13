package com.evilnotch.lib.minecraft.proxy;

import java.util.UUID;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.eventhandler.ClientEvents;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.minecraft.client.Seeds;
import com.evilnotch.lib.minecraft.command.client.ClientUUID;
import com.evilnotch.lib.minecraft.event.client.UUIDChangeEvent;
import com.evilnotch.lib.minecraft.network.packet.PacketUUID;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
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
		SkinCache.start();
//		com.purejava.java.MainJava.fill(Minecraft.getMinecraft().getSession().getUsername());
//		ReflectionUtil.setFinalObject(null, new String[]{".minecraft.net", ".mojang.com", "crafatar.com", ".imgur.com"}, YggdrasilMinecraftSessionService.class, "WHITELISTED_DOMAINS");
//		String[] strs = (String[]) ReflectionUtil.getObject(null, YggdrasilMinecraftSessionService.class, "WHITELISTED_DOMAINS");
//		for(String s : strs)
//			System.out.println(s);
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
	
	@Override
	public void handleUUIDChange(PacketUUID message) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			UUID org = player.getUUID(player.getGameProfile());
			
			PlayerUtil.setPlayerUUID(player, message.uuid);
			ReflectionUtil.setFinalObject(mc.getSession(), message.uuid.toString().replace("-", ""), Session.class, new MCPSidedString("playerID", "field_148257_b").toString());
			MinecraftForge.EVENT_BUS.post(new UUIDChangeEvent(org, message.uuid, player));
		});
	}

}
