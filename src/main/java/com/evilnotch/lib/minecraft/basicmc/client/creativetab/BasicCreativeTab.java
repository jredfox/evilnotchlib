package com.evilnotch.lib.minecraft.basicmc.client.creativetab;

import com.evilnotch.lib.main.loader.LoaderMain;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BasicCreativeTab extends CreativeTabs{
	
	public ItemStack display = null;
	
	public BasicCreativeTab(ResourceLocation label,ItemStack display,LangEntry... langlist) {
		this(label,display,"items.png",true,langlist);
	}
	public BasicCreativeTab(ResourceLocation id,ItemStack display,String backgroundIcon,boolean drawTitle,LangEntry... langlist){
		super(id.toString().replaceAll(":", "."));
		this.display = display;
		this.setBackgroundImageName(backgroundIcon);
		if(!drawTitle)
			this.setNoTitle();
		if(LoaderMain.isClient)
		{
			populateLang(id, langlist);
		}
	}
	@SideOnly(Side.CLIENT)
    protected void populateLang(ResourceLocation id, LangEntry... langlist) 
	{
		LangRegistry.registerLang(this, langlist);
	}
	
	@SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem()
	{
        return display;
    }

}
