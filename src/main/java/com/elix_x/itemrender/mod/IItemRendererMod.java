package com.elix_x.itemrender.mod;

import java.util.List;

import com.elix_x.itemrender.asm.compat.JEI;
import com.elix_x.itemrender.handlers.IItemRendererHandler;
import com.elix_x.itemrender.handlers.RenderItemObj;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = IItemRendererMod.MODID, name = IItemRendererMod.NAME, version = IItemRendererMod.VERSION, clientSideOnly = true)
public class IItemRendererMod {

	public static final String MODID = "iitemrenderer";
	public static final String NAME = "IItem Renderer";
	public static final String VERSION = "1.3";

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();

		if(resourceManager instanceof SimpleReloadableResourceManager)
		{
			List<IResourceManagerReloadListener> reloadListeners = ((SimpleReloadableResourceManager)resourceManager).reloadListeners;
			reloadListeners.remove(Minecraft.getMinecraft().getItemRenderer());
			reloadListeners.remove(Minecraft.getMinecraft().entityRenderer);
			reloadListeners.remove(Minecraft.getMinecraft().renderGlobal);
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem renderItem = new RenderItemObj(mc.renderItem, mc.renderEngine, mc.getItemColors());
		mc.renderItem = renderItem;

		RenderManager renderManager = new RenderManager(mc.renderEngine, renderItem);
		mc.renderManager = renderManager;
		
		ItemRenderer itemRenderer = new ItemRenderer(mc);
		resourceManager.registerReloadListener(renderItem);
		mc.itemRenderer = itemRenderer;
		
		EntityRenderer entityRenderer = new EntityRenderer(mc, resourceManager);
		resourceManager.registerReloadListener(entityRenderer);
		mc.entityRenderer = entityRenderer;
		
		RenderGlobal renderGlobal = new RenderGlobal(mc);
		resourceManager.registerReloadListener(renderGlobal);
		mc.renderGlobal = renderGlobal;
	}
	
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
