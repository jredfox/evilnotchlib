package com.evilnotch.lib.main.eventhandler;

import java.util.List;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.content.client.Seeds;
import com.evilnotch.lib.minecraft.content.tick.TickReg;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.NBTUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.RomanNumerals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientEvents {
	
	/**
	 * this is so data get's cleared on client side only that needs to not be stored all the time or is attatched per world
	 */
	@SubscribeEvent
	public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
	{
		TickReg.garbageCollectClient();
		Seeds.clearSeeds();
	}
	
	@SubscribeEvent
	public void tickClient(ClientTickEvent e)
	{
		 if(e.phase != Phase.END)
			 return;
		 TickReg.tickClient();
	}
	/**
	 * put the seed into f3
	 */
	@SubscribeEvent
	public void seedTxt(RenderGameOverlayEvent.Text e)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(e.getType() != ElementType.TEXT || !mc.gameSettings.showDebugInfo)
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
		if(e.getGui() == null || !(e.getGui() instanceof GuiDisconnected) || EntityUtil.msgShutdown == null)
			return;
		GuiDisconnected old = (GuiDisconnected)e.getGui();
		e.setGui(new GuiDisconnected(new GuiMainMenu(),"disconnect.lost", EntityUtil.msgShutdown) );
		EntityUtil.msgShutdown = null;
	}
	
}
