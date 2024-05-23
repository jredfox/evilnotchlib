package com.evilnotch.lib.main.eventhandler;

import com.evilnotch.lib.main.skin.SkinEntry;
import com.evilnotch.lib.util.JavaUtil;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

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
		else if(info.stopSteve)//if we already stopped steve return
			return;
		
		if(msJoined2 == 0)
			msJoined2 = System.currentTimeMillis();
		
		String encode = getEncode(info.getGameProfile().getProperties());
		if(info.skinType == null && (System.currentTimeMillis() - msJoined2) < 2500 && !SkinEntry.EMPTY_SKIN_ENCODE.equals(encode))
		{
			info.getLocationSkin();//make it download the skin
			event.setCanceled(true);
		}
		else
		{
			info.stopSteve = true;
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
		else if(info.stopSteve)//if we already stopped steve return
			return;
		
		if(msJoined == 0)
			msJoined = System.currentTimeMillis();
		
		String encode = getEncode(info.getGameProfile().getProperties());
		if(info.skinType == null && (System.currentTimeMillis() - msJoined) < 2500 && !SkinEntry.EMPTY_SKIN_ENCODE.equals(encode) )
		{
			info.getLocationSkin();//make it download the skin
			event.setCanceled(true);
		}
		else
		{
			info.stopSteve = true;
			msJoined = 0;
		}
	}
	
	public static String getEncode(PropertyMap map) 
	{
		if(map.isEmpty())
			return null;
		Property p = ((Property)JavaUtil.getFirst(map.get("textures")));
		return p == null ? null : p.getValue();
	}

}
