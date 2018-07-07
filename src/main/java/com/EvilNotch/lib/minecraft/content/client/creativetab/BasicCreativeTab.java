package com.EvilNotch.lib.minecraft.content.client.creativetab;

import java.util.ArrayList;

import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.content.LangEntry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BasicCreativeTab extends CreativeTabs{
	
	public ItemStack display = null;
	public static ArrayList<LangEntry> creativeTabLang = new ArrayList();

	public BasicCreativeTab(ResourceLocation label,ItemStack display,LangEntry... langlist) {
		this(label,display,"items.png",true,langlist);
	}
	public BasicCreativeTab(ResourceLocation id,ItemStack display,String backgroundIcon,boolean drawTitle,LangEntry... langlist){
		super(id.toString().replaceAll(":", "."));
		this.display = display;
		this.setBackgroundImageName(backgroundIcon);
		if(!drawTitle)
			this.setNoTitle();
		if(MainJava.isClient)
		{
			populateLang(langlist,id);
		}
	}
	@SideOnly(Side.CLIENT)
    protected void populateLang(LangEntry[] langlist,ResourceLocation id) 
	{
		for(LangEntry lang : langlist)
		{
			lang.langId = "itemGroup." + id.toString().replaceAll(":", ".");
			lang.loc = id;
			creativeTabLang.add(lang);
		}
	}
	@SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem(){
        return display;
    }

}
