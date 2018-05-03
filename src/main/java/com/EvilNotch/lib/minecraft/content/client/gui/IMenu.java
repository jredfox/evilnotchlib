package com.EvilNotch.lib.minecraft.content.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public interface IMenu {
	
	public void onClose();
	public void onOpen();
	public GuiScreen getGui();
	public Class<? extends GuiScreen> getGuiClass();
	public ResourceLocation getId();
	public boolean allowButtonOverlay();
	public void setAllowButtonOverlay(boolean toset);
	public GuiButton getButton(boolean previous);
	public ResourceLocation getPageButtonTexture();
	public void setPageButtonTexture(ResourceLocation loc);

}
