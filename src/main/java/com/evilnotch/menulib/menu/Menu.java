package com.evilnotch.menulib.menu;

import java.lang.reflect.Constructor;

import com.evilnotch.lib.minecraft.content.client.gui.GuiBasicButton;
import com.evilnotch.menulib.ConfigMenu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class Menu implements IMenu {
	
	public ResourceLocation id = null;
	public Constructor ctr = null;
	public Class clazz = null;
	public ResourceLocation bTexture = new ResourceLocation("textures/gui/widgets.png");
	protected boolean allowButton = true;
	
	public static final GuiBasicButton lbutton = new GuiBasicButton(498,5,5,20,20,"<");
	public static final GuiBasicButton rbutton = new GuiBasicButton(499,30,5,20,20,">");
	
	public static final GuiBasicButton fancyLButton = new GuiBasicButton(498,5,5,64,20,"previous");
	public static final GuiBasicButton fancyRButton = new GuiBasicButton(499,74,5,64,20,"next");
	

	public Menu(Class<? extends GuiScreen> clazz,ResourceLocation id)
	{
		this.clazz = clazz;
		try 
		{
			this.ctr = clazz.getConstructor();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		this.id = id;
	}

	/**
	 * for clearing open gl special effects on close for stubborn guis
	 */
	@Override
	public void onClose() {}

	/**
	 * do special and or rnd effects to your gui and open gl via open
	 */
	@Override
	public void onOpen() {}

	@Override
	public GuiScreen getGui() 
	{
		try
		{
			return (GuiScreen) ctr.newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ResourceLocation getId() 
	{
		return this.id;
	}

	/**
	 * set this to false to use your own buttons for switching menus
	 */
	@Override
	public boolean allowButtonOverlay() 
	{
		return this.allowButton;
	}
	
	@Override
	public void setAllowButtonOverlay(boolean b)
	{
		this.allowButton = b;
	}

	@Override
	public Class<? extends GuiScreen> getGuiClass() 
	{
		return this.clazz;
	}
	@Override
	public void setButtonTexture(ResourceLocation loc)
	{
		this.bTexture  = loc;
	}

	@Override
	public ResourceLocation getButtonTexture() 
	{
		return this.bTexture;
	}
	
	@Override
	public GuiButton getLeftButton() 
	{
		return ConfigMenu.fancyPage ? fancyLButton : lbutton;
	}
	@Override
	public GuiButton getRightButton()
	{
		return ConfigMenu.fancyPage ? fancyRButton : rbutton;
	}
}
