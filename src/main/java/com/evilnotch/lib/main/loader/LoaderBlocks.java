package com.evilnotch.lib.main.loader;

import java.io.File;
import java.util.ArrayList;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.basicmc.auto.IBasicBlock;
import com.evilnotch.lib.minecraft.basicmc.block.BasicMetaBlock;
import com.evilnotch.lib.minecraft.basicmc.block.item.ItemBlockMeta;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigNonMeta;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
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
		for(IBasicBlock basic : LoaderBlocks.blocks)
		{
			Block b = (Block) basic.getObject();
			ItemBlock itemblock = basic.getItemBlock();
			if(itemblock != null)
				ForgeRegistries.ITEMS.register(itemblock);
	    }
	}

	public static void loadpostinit() 
	{
		if(!LoaderMain.isDeObfuscated)
		{
			LoaderBlocks.cfgBlockProps.saveConfig(true, false, true);
		}
		clearBlocks();
	}

	private static void clearBlocks() 
	{
		LoaderBlocks.blocks.clear();
		cfgBlockProps = null;
	}

}
