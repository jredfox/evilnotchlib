package com.EvilNotch.lib.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class FMLCorePlugin implements IFMLLoadingPlugin
{
	public static boolean isObf;
	
    @Override
    public String[] getASMTransformerClass()
    {
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
