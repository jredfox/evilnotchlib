package com.evilnotch.lib.minecraft.basicmc.client.creativetab;

import com.evilnotch.lib.minecraft.basicmc.auto.IAutoItem;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BasicCreativeTab extends CreativeTabs implements IAutoItem{
	
	public ItemStack display = null;
	
	public BasicCreativeTab(ResourceLocation label, ItemStack display, LangEntry... langlist) 
	{
		this(label, display, "items.png", true, langlist);
	}
	
	public BasicCreativeTab(ResourceLocation label, ItemStack display, String backgroundIcon, boolean drawTitle, LangEntry... langlist)
	{
		super(label.toString().replaceAll(":", "."));
		this.display = display;
		this.setBackgroundImageName(backgroundIcon);
		if(!drawTitle)
			this.setNoTitle();
		populateLang(langlist);
	}
	
    protected void populateLang(LangEntry... langlist) 
	{
		if(this.canRegisterLang())
			LangRegistry.registerLang(this, langlist);
	}
	
    @Override
	public ItemStack getTabIconItem()
	{
        return display;
    }
	
	/**
	 * the creative tabs are auto registered without this objects control
	 */
	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean canRegisterLang() {
		return true;
	}
	
	/**
	 * object doesn't support this feature
	 */
	@Override
	public boolean canRegisterJSON() {
		return false;
	}

}
