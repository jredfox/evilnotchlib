package com.evilnotch.lib.main.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.main.Config;
import com.evilnotch.lib.minecraft.basicmc.auto.BlockWrapper;
import com.evilnotch.lib.minecraft.basicmc.auto.block.BlockProperty;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigNonMeta;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class LoaderBlocks {
	
	public static List<BlockWrapper> blocks = new ArrayList();
	 
	public static void loadpreinit()
	{
		BlockProperty.parseProperties();
	}

	public static void registerBlocks() 
	{
		for(BlockWrapper basic : LoaderBlocks.blocks)
		{
			Block b = (Block) basic.b;
			ItemBlock itemblock = basic.itemblock;
			ForgeRegistries.BLOCKS.register(b);
			if(itemblock != null)
			{
				ForgeRegistries.ITEMS.register(itemblock);
			}
	    }
		blocks.clear();
	}

	public static void loadpostinit() 
	{
		BlockProperty.saveBlockProps();
	}

}
