package com.evilnotch.lib.minecraft.content.client.gui;

import java.lang.reflect.Constructor;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.main.ConfigMenu;
import com.evilnotch.lib.util.simple.ICopy;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class Menu implements IMenu {
	
	public ResourceLocation id = null;
	public Constructor ctr = null;
	public Class clazz = null;
	public ResourceLocation bTexture = new ResourceLocation("textures/gui/widgets.png");
	private boolean allowButton = true;
	public static final GuiBasicButton lbutton = new GuiBasicButton(498,5,5,20,20,"<");
	public static final GuiBasicButton rbutton = new GuiBasicButton(499,30,5,20,20,">");
	

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
	public boolean allowButtonOverlay() {
		return this.allowButton;
	}
	
	@Override
	public void setAllowButtonOverlay(boolean b){
		this.allowButton = b;
	}

	@Override
	public Class<? extends GuiScreen> getGuiClass() {
		return this.clazz;
	}
	@Override
	public void setPageButtonTexture(ResourceLocation loc){
		this.bTexture  = loc;
	}

	@Override
	public ResourceLocation getPageButtonTexture() {
		return this.bTexture;
	}
	
	@Override
	public GuiButton getButton(boolean previous) 
	{
		lbutton.setButtonTexture(this.bTexture);
		rbutton.setButtonTexture(this.bTexture);
		return previous ? (ConfigMenu.fancyPage ? new GuiBasicButton(498,5,5,64,20,"previous",this.bTexture) : lbutton ) : (ConfigMenu.fancyPage ? new GuiBasicButton(499,74,5,64,20,"next",this.bTexture) : rbutton);
	}
}
