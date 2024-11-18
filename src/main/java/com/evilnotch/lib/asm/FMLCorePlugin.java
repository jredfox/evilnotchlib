package com.evilnotch.lib.asm;

import java.util.Map;

import net.minecraftforge.fml.crashy.Crashy;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@IFMLLoadingPlugin.Name("evilnotchlib-transformer_fixes")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@TransformerExclusions("com.evilnotch.lib.asm.")
public class FMLCorePlugin implements IFMLLoadingPlugin
{
	public static boolean isObf;
	
	static
	{
		try
		{
			ConfigCore.load();
		}
		catch(Throwable t)
		{
			Crashy.crash("ASM CoreMod Config Failed to Load this is Bad!", t, true);
		}
	}
	
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {
        		"com.evilnotch.lib.asm.transformer.Transformer",
        		"com.evilnotch.lib.asm.transformer.EntityTransformer"
        };
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
