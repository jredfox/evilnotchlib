package com.evilnotch.lib.main.eventhandler;

import com.evilnotch.lib.main.Config;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StopSteve {
	
	public static boolean smooth;
	public StopSteve()
	{
		smooth = Config.skinPacketSmooth && !Config.skinVanillaPackets;
	}
	
    public static long m2 = 0;
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void stopSteve(RenderPlayerEvent.Pre event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(smooth ? (event.getEntityPlayer() != mc.player) : (mc.getRenderViewEntity() != mc.player) || mc.world == null || mc.player == null)
			return;
		
		NetworkPlayerInfo info = ((AbstractClientPlayer)event.getEntityPlayer()).playerInfo;//mc.player.connection.getPlayerInfo(mc.player.getUniqueID());
		if(info == null || !info.canRender)
		{
			if(info != null)
			{
				info.getLocationSkin();
				if(m2 == 0) {
					m2 = System.currentTimeMillis();
				}
				else if((System.currentTimeMillis() - m2) >= Config.stopSteveMs) {
					info.canRender = true;
					m2 = 0;
					System.err.println("StopSteve Lasted Max MS:" + Config.stopSteveMs + " This is probably a mod incompatibility!");
				}
			}
			event.setCanceled(true);
			return;
		}
		m2 = 0;
	}
    
	public static long m = 0;
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void stopSteve(RenderHandEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.getRenderViewEntity() != mc.player || mc.world == null || mc.player == null)
			return;
		
		NetworkPlayerInfo info = ((AbstractClientPlayer)mc.player).playerInfo;//mc.player.connection.getPlayerInfo(mc.player.getUniqueID());
		if(info == null || !info.canRender)
		{
			if(info != null)
			{
				info.getLocationSkin();
				if(m == 0) {
					m = System.currentTimeMillis();
				}
				else if((System.currentTimeMillis() - m) >= Config.stopSteveMs) {
					info.canRender = true;
					m = 0;
					System.err.println("StopSteve Lasted Max MS:" + Config.stopSteveMs + " This is probably a mod incompatibility!");
				}
			}
			event.setCanceled(true);
			return;
		}
		m = 0;
	}
	
	public static void stopSteve(NetworkPlayerInfo info, Type type)
	{
		if(type == Type.SKIN)
			info.canRender = true;
	}

}
