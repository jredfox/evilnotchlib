package com.evilnotch.lib.main.eventhandler;

import com.evilnotch.lib.main.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StopSteve {
	
    public static long msJoined2 = 0;
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void stopSteve(RenderPlayerEvent.Pre event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(event.getEntityPlayer() != mc.player || mc.world == null || mc.player == null)
			return;
		
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		NetworkPlayerInfo info = player.connection.getPlayerInfo(player.getUniqueID());
		if(info == null)
		{
			event.setCanceled(true);
			return;
		}
		else if(info.stopedSteve)//if we already stopped steve return
		{
			msJoined2 = 0;
			return;
		}
		
		if(msJoined2 == 0)
			msJoined2 = System.currentTimeMillis();
		
		if(info.skinType == null && (System.currentTimeMillis() - msJoined2) < Config.stopSteveMs)
		{
			info.getLocationSkin();//make it download the skin
			event.setCanceled(true);
		}
		else
		{
			info.stopedSteve = true;
			msJoined2 = 0;
		}
	}
    
	public static long msJoined = 0;
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void stopSteve(RenderHandEvent event)
	{
		//return from canceling if we are not inside of the world
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.getRenderViewEntity() != mc.player || mc.world == null || mc.player == null)
		{
			return;
		}
		
		NetworkPlayerInfo info = mc.player.connection.getPlayerInfo(mc.player.getUniqueID());
		if(info == null)
		{
			event.setCanceled(true);
			return;
		}
		else if(info.stopedSteve)//if we already stopped steve return
		{
			msJoined = 0;
			return;
		}
		
		if(msJoined == 0)
			msJoined = System.currentTimeMillis();
		
		if(info.skinType == null && (System.currentTimeMillis() - msJoined) < Config.stopSteveMs)
		{
			info.getLocationSkin();//make it download the skin
			event.setCanceled(true);
		}
		else
		{
			info.stopedSteve = true;
			msJoined = 0;
		}
	}

}
