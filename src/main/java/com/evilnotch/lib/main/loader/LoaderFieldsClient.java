package com.evilnotch.lib.main.loader;

import com.evilnotch.lib.api.mcp.MCPMappings;

import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;

public class LoaderFieldsClient {
	
	public static String CURRENT_LOCALE = null;
	public static String properties = null;

	public static void cacheFields()
	{
		CURRENT_LOCALE = MCPMappings.getField(LanguageManager.class,"CURRENT_LOCALE");
		properties = MCPMappings.getField(Locale.class,"properties");
	}

}
