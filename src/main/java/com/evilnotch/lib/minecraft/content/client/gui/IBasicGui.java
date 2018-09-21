package com.evilnotch.lib.minecraft.content.client.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.RenderItem;

public interface IBasicGui {
	
	public RenderItem getRenderItem();
	public List<? extends GuiButton> getButtonList();
	public List<? extends GuiLabel> getLabelList();
	public FontRenderer getFontRenderer();
	public GuiButton getSelectedButton();
	public boolean getKeyHandled();
	public boolean getmouseHandled();
	public float getZLevel();
}
