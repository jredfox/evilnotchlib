package com.evilnotch.menulib.menu;

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
	public GuiButton getLeftButton();
	public GuiButton getRightButton();
	public ResourceLocation getButtonTexture();
	public void setButtonTexture(ResourceLocation loc);

}
