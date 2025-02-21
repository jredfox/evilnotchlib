package com.evilnotch.lib.main.eventhandler;

import com.evilnotch.lib.main.Config;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StopSteve {
	
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void stopSteve(RenderPlayerEvent.Pre event)
	{
		EntityPlayer a = event.getEntityPlayer();
		Minecraft mc = Minecraft.getMinecraft();
		if(!(a instanceof AbstractClientPlayer) || mc.world == null || mc.player == null)
			return;
		
		AbstractClientPlayer p = (AbstractClientPlayer)a;
		NetworkPlayerInfo info = p.playerInfo;//mc.player.connection.getPlayerInfo(mc.player.getUniqueID());
		if(info == null || !info.canRender)
		{
			if(info != null)
			{
				info.getLocationSkin();
				if(info.ssms == 0) {
					info.ssms = System.currentTimeMillis();
				}
				else if((System.currentTimeMillis() - info.ssms) >= (mc.player == p ? Config.stopSteveMs : Config.stopSteveOtherMs)) {
					info.canRender = true;
					info.ssms = 0;
					System.err.println("StopSteve Lasted Max MS:" + (mc.player == p ? Config.stopSteveMs : Config.stopSteveOtherMs) + " This is probably a mod incompatibility!");
					return;
				}
			}
			event.setCanceled(true);
			return;
		}
		info.ssms = 0L;
	}
    
	public static long m = 0;
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void stopSteve(RenderHandEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.world == null || mc.player == null)
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
					return;
				}
			}
			event.setCanceled(true);
			return;
		}
		m = 0L;
	}
	
	public static void stopSteve(NetworkPlayerInfo info, Type type)
	{
		if(type == Type.SKIN)
			info.canRender = true;
	}

}
