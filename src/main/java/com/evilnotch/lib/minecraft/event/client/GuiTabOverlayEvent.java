package com.evilnotch.lib.minecraft.event.client;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GuiTabOverlayEvent extends Event {
	
	public boolean canDisplay;
	public boolean keyDown;
	public boolean isIntegratedRunning;
	public int playerSize;
	public ScoreObjective scoreboard;
	public Minecraft mc;
	
	public GuiTabOverlayEvent(Minecraft mc)
	{
		this.keyDown = mc.gameSettings.keyBindPlayerList.isKeyDown();
		this.isIntegratedRunning = mc.isIntegratedServerRunning();
		this.playerSize = mc.player.connection.getPlayerInfoMap().size();
		this.scoreboard = mc.world.getScoreboard().getObjectiveInDisplaySlot(0);
		this.mc = mc;
		
//		this.canDisplay = this.keyDown && (!this.isIntegratedRunning || this.playerSize > 1 || this.scoreboard != null);//Vanilla's Logic
		this.canDisplay = this.keyDown && (!this.isIntegratedRunning || mc.getIntegratedServer().lanServerPing != null || this.scoreboard != null);
	}

	public static boolean fire()
	{
		GuiTabOverlayEvent e = new GuiTabOverlayEvent(Minecraft.getMinecraft());
		MinecraftForge.EVENT_BUS.post(e);
		return e.canDisplay;
	}

}
