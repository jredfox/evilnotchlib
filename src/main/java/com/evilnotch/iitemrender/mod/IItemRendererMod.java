package com.evilnotch.iitemrender.mod;

import com.evilnotch.iitemrender.asm.compat.JEI;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.api.ReflectionUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = IItemRendererMod.MODID, name = IItemRendererMod.NAME, version = IItemRendererMod.VERSION, clientSideOnly = true)
public class IItemRendererMod {

	public static final String MODID = "iitemrenderer";
	public static final String NAME = "IItem Renderer";
	public static final String VERSION = "1.3";
	
	/**
	 * add jei support to the tabs
	 */
	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{
		if(Loader.isModLoaded("jei"))
		{
			for(Item i : IItemRendererHandler.getItems())
				JEI.slowItems.add(i);
			Class c = ReflectionUtil.classForName("mezz.jei.render.IngredientListBatchRenderer");
			String s = c.getName();
		}
	}

}