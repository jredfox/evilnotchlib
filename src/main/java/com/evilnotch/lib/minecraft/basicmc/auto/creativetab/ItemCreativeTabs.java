package com.evilnotch.lib.minecraft.basicmc.auto.creativetab;

import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.main.MainJava;
import com.evilnotch.lib.minecraft.basicmc.auto.lang.LangEntry;
import com.evilnotch.lib.minecraft.basicmc.item.BasicItem;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ItemCreativeTabs extends BasicItem{
	
	public static List<ICreativeTabHook> tabs = new ArrayList(0);
	
	public ItemCreativeTabs()
	{
		super(new ResourceLocation(MainJava.MODID, "creativeTabDummy"), (CreativeTabs)null, new LangEntry("en_us", "Item Not Found"));
		this.setHasSubtypes(true);
	}
	
	@Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
    	for(ICreativeTabHook hook : tabs)
    		hook.getSubItems(tab, items);
    }
	
	public static void register(ICreativeTabHook hook)
	{
		tabs.add(hook);
	}
}
