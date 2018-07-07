package com.EvilNotch.lib.minecraft.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.content.ConfigLang;
import com.EvilNotch.lib.minecraft.content.LangEntry;
import com.EvilNotch.lib.minecraft.content.LangLine;
import com.EvilNotch.lib.minecraft.content.blocks.BasicBlock;
import com.EvilNotch.lib.minecraft.content.client.creativetab.BasicCreativeTab;
import com.EvilNotch.lib.minecraft.content.items.BasicItem;
import com.EvilNotch.lib.util.Line.ILine;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;

public class ServerProxy {
	
	public void preinit(){}
	
	/**
	 * generate lan files and inject here
	 */
	public void postinit(){}
	
	/**
	 * called before anything else
	 */
	public void proxypreinit() {}
	public void initMod() {}

	public void lang() {
		
	}

}
