package com.EvilNotch.lib.minecraft.content.client.creativetab;

import java.util.ArrayList;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.content.LangEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BasicCreativeTab extends CreativeTabs{
	
	public ItemStack display = null;
	public static ArrayList<LangEntry> creativeTabLang = new ArrayList();

	public BasicCreativeTab(String label,ItemStack display,LangEntry... langlist) {
		this(label,display,"items.png",true,langlist);
	}
	public BasicCreativeTab(String label,ItemStack display,String backgroundIcon,boolean drawTitle,LangEntry... langlist){
		super(label);
		this.display = display;
		this.setBackgroundImageName(backgroundIcon);
		if(!drawTitle)
			this.setNoTitle();
		if(MainJava.isClient)
		{
			populateLang(langlist,label);
		}
	}
	@SideOnly(Side.CLIENT)
    protected void populateLang(LangEntry[] langlist,String label) 
	{
		for(LangEntry lang : langlist)
		{
			lang.langId = "itemGroup." + label;
			creativeTabLang.add(lang);
		}
	}
	@SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem(){
        return display;
    }

}
