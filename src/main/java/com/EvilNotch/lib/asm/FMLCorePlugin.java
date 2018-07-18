package com.EvilNotch.lib.asm;

import java.io.File;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class FMLCorePlugin implements IFMLLoadingPlugin
{
	public static boolean isObf;
    public static volatile boolean configGenerated = false;
	
    @Override
    public String[] getASMTransformerClass()
    {
    	if(!configGenerated)
    	{
    		ConfigCore.load();
    		configGenerated = true;
    	}
        return new String[] {"com.EvilNotch.lib.asm.Transformer"};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
       return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
    	isObf = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
