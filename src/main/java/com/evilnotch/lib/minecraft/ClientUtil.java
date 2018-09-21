package com.evilnotch.lib.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.text.TextComponentString;

public class ClientUtil {

	public static void disconnectPlayer(EntityPlayerMP player, TextComponentString msg) 
	{
		Minecraft mc = Minecraft.getMinecraft();
        boolean flag = mc.isIntegratedServerRunning();
        boolean flag1 = mc.isConnectedToRealms();
        mc.world.sendQuittingDisconnectingPacket();
        mc.loadWorld((WorldClient)null);

        if (flag)
        {
            mc.displayGuiScreen(new GuiMainMenu());
        }
        else if (flag1)
        {
            RealmsBridge realmsbridge = new RealmsBridge();
            realmsbridge.switchToRealms(new GuiMainMenu());
        }
        else
        {
            mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
	}

}
