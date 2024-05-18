package com.evilnotch.lib.main.eventhandler;

import java.util.List;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.skin.SkinEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.client.gui.GuiBasicButton;
import com.evilnotch.lib.minecraft.client.Seeds;
import com.evilnotch.lib.minecraft.event.client.ClientDisconnectEvent;
import com.evilnotch.lib.minecraft.event.client.SkinTransparencyEvent;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientEvents {
	
    public static long msJoined2 = 0;
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void stopSteve(RenderPlayerEvent.Pre event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(event.getEntityPlayer() != Minecraft.getMinecraft().player || mc.world == null || mc.player == null)
			return;
		
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		NetworkPlayerInfo info = player.connection.getPlayerInfo(player.getUniqueID());
		if(info == null)
		{
			event.setCanceled(true);
			return;
		}
		
		if(msJoined2 == 0)
			msJoined2 = System.currentTimeMillis();
		
		if(info.skinType == null && (System.currentTimeMillis() - msJoined2) < 2500 && !SkinEntry.EMPTY_SKIN_ENCODE.equals(getEncode(info.getGameProfile().getProperties())))
		{
			info.getLocationSkin();//make it download the skin
			event.setCanceled(true);
		}
		else
		{
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
		
		if(msJoined == 0)
			msJoined = System.currentTimeMillis();
		
		if(info.skinType == null && (System.currentTimeMillis() - msJoined) < 2500 && !SkinEntry.EMPTY_SKIN_ENCODE.equals(getEncode(info.getGameProfile().getProperties())) )
		{
			info.getLocationSkin();//make it download the skin
			event.setCanceled(true);
		}
		else
		{
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

	/**
	 * future:Generate models with textures coming from the registry name
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void modeltest(ModelRegistryEvent event)
	{
		JsonGen.registerModels();
	}
	
	@SubscribeEvent
	public void tickClient(ClientTickEvent e)
	{
		 TickRegistry.tickClient(e.phase);
	}
	
	/**
	 * put the seed into f3
	 */
	@SubscribeEvent
	public void seedTxt(RenderGameOverlayEvent.Text e)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(e.getType() != ElementType.TEXT || !mc.gameSettings.showDebugInfo || !Config.seedDisplay)
			return;
		List<String> f3 = e.getLeft();
		int index = 0;
		for(String s : f3)
		{
			if(s.toLowerCase().contains("biome"))
			{
				f3.add(index+1, "Seed: " + Seeds.getSeed(mc.world));
				break;
			}
			index++;
		}
	}
	
	/**
	 * if you get booted from your own world
	 */
	@SubscribeEvent
	public void kickSelf(GuiOpenEvent e)
	{
		if(e.getGui() == null || !(e.getGui() instanceof GuiDisconnected) || PlayerUtil.msgShutdown == null)
			return;
		GuiDisconnected old = (GuiDisconnected)e.getGui();
		e.setGui(new GuiDisconnected(new GuiMainMenu(), "disconnect.lost", PlayerUtil.msgShutdown) );
		PlayerUtil.msgShutdown = null;
	}
	
	@SubscribeEvent
	public void disconnect(ClientDisconnectEvent e)
	{ 
		ClientProxy.disconnect();
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post e)
	{
		for(GuiButton b : e.getButtonList())
		{
			if(b instanceof GuiBasicButton)
			{
				GuiBasicButton button = (GuiBasicButton)b;
				if(button.unlocalizedName != null)
				{
					button.displayString = I18n.format(button.unlocalizedName);
				}
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void stopSounds(PlaySoundEvent event)
	{
		if(LibEvents.disableSound.get())
			event.setResultSound(null);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void coolskins(SkinTransparencyEvent event)
	{
		event.allowTrans = Config.allowskintrans;
	}
	
}
