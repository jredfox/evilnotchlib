package com.EvilNotch.lib.minecraft.content.items;

import java.util.ArrayList;

import com.EvilNotch.lib.minecraft.content.LangEntry;

import net.minecraft.item.Item;

public interface IBasicItem {

	public boolean register();
	public boolean registerModel();
	public boolean useLangRegistry();
	public boolean useConfigPropterties();
}
