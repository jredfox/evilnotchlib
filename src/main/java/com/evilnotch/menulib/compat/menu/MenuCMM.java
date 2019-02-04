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
	public static Method loadSplashTexts;
	
	public static Object configInstance;
	public static Method getGui;
	
	public GuiScreen gui;
	
	public static final ResourceLocation bTexture = new ResourceLocation("minecraft:textures/gui/widgets.png");
	
	static
	{
		try 
		{
			cmm = Class.forName("lumien.custommainmenu.CustomMainMenu");
			configClass = Class.forName("lumien.custommainmenu.configuration.Config");
			guiCustom =  Class.forName("lumien.custommainmenu.gui.GuiCustom");
			
			loadSplashTexts = guiCustom.getDeclaredMethod("loadSplashTexts");
			loadSplashTexts.setAccessible(true);
			
			Object instance = ReflectionUtil.getObject(null, cmm, "INSTANCE");
			configInstance = ReflectionUtil.getObject(instance, cmm, "config");
			getGui = ReflectionUtil.getMethod(configClass, "getGUI", String.class);
			getGui.setAccessible(true);
		} 
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onClose() 
	{
		
	}

	@Override
	public void onOpen() 
	{
		try 
		{
			loadSplashTexts.invoke(MenuRegistry.getCurrentGui());
		} 
		catch (Throwable t) 
		{
			t.printStackTrace();
		}
	}

	@Override
	public GuiScreen getGui() 
	{
		return this.gui;
	}
	
	@Override
	public GuiScreen createGui()
	{
		try 
		{
			GuiScreen gui = (GuiScreen) getGui.invoke(configInstance, "mainmenu");
			this.gui = gui;
			return this.getGui();
		} 
		catch (Throwable t) 
		{
			t.printStackTrace();
		}
		return null;
	}

	@Override
	public Class<? extends GuiScreen> getGuiClass() 
	{
		return guiCustom;
	}

	@Override
	public ResourceLocation getId() 
	{
		return new ResourceLocation("custommainmenu:mainmenu");
	}

	@Override
	public ResourceLocation getButtonTexture() 
	{
		return bTexture;
	}

	@Override
	public void setButtonTexture(ResourceLocation loc)
	{
		
	}

	@Override
	public GuiButton getLeftButton() 
	{
		return Menu.lbutton;
	}

	@Override
	public GuiButton getRightButton() 
	{
		return Menu.rbutton;
	}

}
