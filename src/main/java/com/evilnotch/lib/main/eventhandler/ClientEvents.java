package com.evilnotch.lib.main.eventhandler;

import java.util.List;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.basicmc.client.gui.GuiBasicButton;
import com.evilnotch.lib.minecraft.client.Seeds;
import com.evilnotch.lib.minecraft.event.client.ClientDisconnectEvent;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientEvents {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void r(RenderPlayerEvent.Pre event)
	{
		GlStateManager.enableNormalize();//Fixes Player's Layers(Mouse Ears) from becoming darker when holding an item if a slime is rendering
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void pr(RenderPlayerEvent.Post event)
	{
		GlStateManager.disableNormalize();
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
	
	/**
	 * Vanilla Bug Fix Prevents Crashing When Rendering Entities Sooner then expected with nametags
	 */
	@SubscribeEvent
	public void nametag(RenderLivingEvent.Specials.Pre event)
	{
		if(event.getRenderer().getRenderManager().renderViewEntity == null)
			event.setCanceled(true);
	}
	
//	@SubscribeEvent(priority=EventPriority.HIGHEST)
//	public void coolskins(SkinEvent.Capability event)
//	{
//		event.skin.meta.put("a", String.valueOf(true));
//	}
	
}
