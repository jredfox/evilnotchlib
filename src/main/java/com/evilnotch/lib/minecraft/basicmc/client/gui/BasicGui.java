package com.evilnotch.lib.minecraft.basicmc.client.gui;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;

public class BasicGui extends GuiScreen implements IBasicGui{

	@Override
	public RenderItem getRenderItem() {
		return this.itemRender;
	}

	@Override
	public List<? extends GuiButton> getButtonList() {
		return this.buttonList;
	}

	@Override
	public List<? extends GuiLabel> getLabelList() {
		return this.labelList;
	}

	@Override
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	@Override
	public GuiButton getSelectedButton() {
		return this.selectedButton;
	}

	@Override
	public boolean getKeyHandled() {
		return this.keyHandled;
	}

	@Override
	public boolean getmouseHandled() {
		return this.mouseHandled;
	}

	@Override
	public float getZLevel() {
		return this.zLevel;
	}

}
