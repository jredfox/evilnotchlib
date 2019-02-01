package com.evilnotch.lib.main.loader;

import java.io.File;
import java.util.ArrayList;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.content.block.IBasicBlock;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigNonMeta;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class LoaderBlocks {
	
	public static ArrayList<IBasicBlock> blocks = new ArrayList();
	public static ConfigBase cfgBlockProps = null;
	 
	public static void loadpreinit()
	{
		cfgBlockProps = new ConfigNonMeta(new File(Config.cfg.getParent(),"config/blockprops.cfg"));
		cfgBlockProps.loadConfig();
	}

	public static void registerBlocks() 
	{
		for(IBasicBlock i : LoaderBlocks.blocks)
		{
		   if(i.register())
		   {
			   Block b = (Block)i;
			   ForgeRegistries.BLOCKS.register(b);
			   if(i.hasItemBlock())
				   ForgeRegistries.ITEMS.register(i.getItemBlock());
		   }
	    }
	}

}
