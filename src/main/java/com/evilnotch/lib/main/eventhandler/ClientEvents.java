package com.evilnotch.lib.main.eventhandler;

import java.util.List;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.content.tick.TickReg;
import com.evilnotch.lib.minecraft.proxy.ClientProxy;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.NBTUtil;
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
		MainJava.proxy.onClientDisconect();
		TickReg.garbageCollectClient();
	}
	@SubscribeEvent
	public void tickClient(ClientTickEvent e)
	{
		 if(e.phase != Phase.END)
			 return;
		 TickReg.tickClient();
	}
	@SubscribeEvent
	public void seedText(RenderGameOverlayEvent.Text e)
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
				f3.add(index+1, "Seed:" + ClientProxy.getSeed(mc.world));
				break;
			}
			index++;
		}
	}

	@SubscribeEvent
	public void onGuiDisconnect(GuiOpenEvent e)
	{
		if(e.getGui() == null || !(e.getGui() instanceof GuiDisconnected) || EntityUtil.msgShutdown == null)
			return;
		GuiDisconnected old = (GuiDisconnected)e.getGui();
		e.setGui(new GuiDisconnected(new GuiMainMenu(),"disconnect.lost", EntityUtil.msgShutdown) );
		EntityUtil.msgShutdown = null;
	}
	
	/**
	 * tooltip roman numerals fixer
	 */
	@SubscribeEvent (priority = EventPriority.HIGH)
	public void onToolTip(ItemTooltipEvent e)
	{
		ItemStack stack = e.getItemStack();
		NBTTagList ench = stack.getEnchantmentTagList();
		if(ench.tagCount() == 0)
			ench = NBTUtil.getNBTTagListSafley(stack.getTagCompound(),"StoredEnchantments",10);
			
		if(stack.isEmpty() || ench.tagCount() == 0)
			return;
		List<String> toolTip = e.getToolTip();
		for(int i=0;i<toolTip.size();i++)
		{
			String s = toolTip.get(i);
			if(!s.contains("enchantment.level"))
				continue;
			for(int j=0;j<ench.tagCount();j++)
			{
				NBTTagCompound nbt = (NBTTagCompound)ench.getCompoundTagAt(j);
				int id = nbt.getInteger("id");
				int lvl = nbt.getInteger("lvl");
				String Roman = RomanNumerals.translateIntToRoman(lvl);
				Enchantment enchantment = Enchantment.getEnchantmentByID(id);
				String enchName = getEnchName(enchantment);
				String enchname = enchantment.getTranslatedName(lvl);
				if(s.equals(enchname))
				{
					toolTip.set(i, enchName + " " + Roman);
				}
			}
		}
	}

    public String getEnchName(Enchantment e)
    {
        String s = I18n.translateToLocal(e.getName());

        if (e.isCurse())
        {
            s = TextFormatting.RED + s;
        }
        return s;
    }
	
}
