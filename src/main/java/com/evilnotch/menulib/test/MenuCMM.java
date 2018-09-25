package com.evilnotch.menulib.test;

import com.evilnotch.menulib.menu.IMenu;
import com.evilnotch.menulib.menu.Menu;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class MenuCMM implements IMenu{

	@Override
	public void onClose() {
	}

	@Override
	public void onOpen() {
	}

	@Override
	public GuiScreen getGui() {
		return CustomMainMenu.INSTANCE.config.getGUI("mainmenu");
	}

	@Override
	public Class<? extends GuiScreen> getGuiClass() {
		return GuiCustom.class;
	}

	@Override
	public ResourceLocation getId() {
		return new ResourceLocation("custommainmenu:mainmenu");
	}

	@Override
	public boolean allowButtonOverlay() {
		return true;
	}

	@Override
	public void setAllowButtonOverlay(boolean toset) {
	}

	@Override
	public GuiButton getButton(boolean previous) {
		return previous ? Menu.lbutton : Menu.rbutton;
	}

	@Override
	public ResourceLocation getPageButtonTexture() {
		return new ResourceLocation("textures/gui/widgets.png");
	}

	@Override
	public void setPageButtonTexture(ResourceLocation loc) {
	}

}
