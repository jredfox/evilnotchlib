package com.evilnotch.lib.minecraft.proxy;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.eventhandler.ClientEvents;
import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.client.Seeds;
import com.evilnotch.lib.minecraft.command.client.ClientUUID;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
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

}
