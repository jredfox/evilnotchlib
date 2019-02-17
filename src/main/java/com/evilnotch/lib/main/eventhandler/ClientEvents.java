package com.evilnotch.lib.main.eventhandler;

import java.util.List;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.basicmc.auto.json.JsonGen;
import com.evilnotch.lib.minecraft.client.Seeds;
import com.evilnotch.lib.minecraft.event.client.ClientDisconnectEvent;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.tick.TickRegistry;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.NBTUtil;
import com.evilnotch.lib.minecraft.util.PlayerUtil;
import com.evilnotch.menulib.menu.MenuRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientEvents {
	
	/**
	 * future:Generate models with textures coming from the registry name
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void modeltest(ModelRegistryEvent event)
	{
		JsonGen.modelReg();
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
				f3.add(index+1, "Seed:" + Seeds.getSeed(mc.world));
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
		e.setGui(new GuiDisconnected(new GuiMainMenu(),"disconnect.lost", PlayerUtil.msgShutdown) );
		PlayerUtil.msgShutdown = null;
	}
	
	@SubscribeEvent
	public void disconnect(ClientDisconnectEvent e)
	{
		ClientProxy.clearClientData();
	}
	
	@SubscribeEvent
	public void enchantmentFix(ItemTooltipEvent e)
	{
		ItemStack stack = e.getItemStack();
		NBTTagList ench = stack.getEnchantmentTagList();
		if(ench.tagCount() == 0)
		{
			ench = NBTUtil.getNBTTagListSafley(stack.getTagCompound(), "StoredEnchantments", 10);
		}
		if(stack.isEmpty() || ench.tagCount() == 0)
		{
			return;
		}
		
		List<String> list = e.getToolTip();
		for(int i=0;i<list.size();i++)
		{
			String s = list.get(i);
			int index = s.indexOf("enchantment.level.");
			if(index == -1)
			{
				continue;
			}
			String fixed = s.substring(0, index) + s.substring(index + "enchantment.level.".length(), s.length());
			list.set(i, fixed);
		}
	}
	
}
