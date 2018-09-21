package com.evilnotch.lib.Api;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;

public class FieldAcessClient {
	
	public static String CURRENT_LOCALE = null;
	public static String properties = null;

	public static void cacheFields()
	{
		CURRENT_LOCALE = MCPMappings.getField(LanguageManager.class,"CURRENT_LOCALE");
		properties = MCPMappings.getField(Locale.class,"properties");
	}

}
