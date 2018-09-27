package com.evilnotch.menulib.compat.menu;

import java.lang.reflect.Method;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.menulib.menu.IMenu;
import com.evilnotch.menulib.menu.Menu;
import com.evilnotch.menulib.menu.MenuRegistry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class MenuCMM implements IMenu{
	
	public static Class cmm;
	public static Class configClass;
	public static Class guiCustom;
	
	static
	{
		try 
		{
			cmm = Class.forName("lumien.custommainmenu.CustomMainMenu");
			configClass = Class.forName("lumien.custommainmenu.configuration.Config");
			guiCustom =  Class.forName("lumien.custommainmenu.gui.GuiCustom");
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onClose() {
	}

	@Override
	public void onOpen() 
	{
		Method m  = null;
		try 
		{
			m = guiCustom.getDeclaredMethod("loadSplashTexts");
			m.setAccessible(true);
			m.invoke(MenuRegistry.getCurrentGui());
		} 
		catch (Throwable t) 
		{
			t.printStackTrace();
		}
	}

	@Override
	public GuiScreen getGui() 
	{
		try 
		{
			Object instance = ReflectionUtil.getObject(null, cmm, "INSTANCE");
			Object config = ReflectionUtil.getObject(instance, cmm, "config");
			Method m = ReflectionUtil.getMethod(configClass, "getGUI", String.class);
			return (GuiScreen) m.invoke(config, "mainmenu");
		} 
		catch (Throwable t) 
		{
			t.printStackTrace();
		}
		return null;
	}

	@Override
	public Class<? extends GuiScreen> getGuiClass() {
		return guiCustom;
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
	public ResourceLocation getButtonTexture() {
		return new ResourceLocation("textures/gui/widgets.png");
	}

	@Override
	public void setButtonTexture(ResourceLocation loc) {
	}

	@Override
	public GuiButton getLeftButton() {
		return Menu.lbutton;
	}

	@Override
	public GuiButton getRightButton() {
		return Menu.rbutton;
	}

}
