package com.elix_x.itemrender.mod;

import java.util.List;

import com.elix_x.itemrender.IItemRendererRenderItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = IItemRendererMod.MODID, name = IItemRendererMod.NAME, version = IItemRendererMod.VERSION, clientSideOnly = true)
public class IItemRendererMod {

	public static final String MODID = "iitemrenderer";
	public static final String NAME = "IItem Renderer";
	public static final String VERSION = "1.0";

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		IReloadableResourceManager mcResourceManager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();

		if(mcResourceManager instanceof SimpleReloadableResourceManager){
			List<IResourceManagerReloadListener> reloadListeners = ReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager) mcResourceManager, "reloadListeners", "field_110546_b");
			reloadListeners.remove(Minecraft.getMinecraft().getItemRenderer());
			reloadListeners.remove(Minecraft.getMinecraft().entityRenderer);
			reloadListeners.remove(Minecraft.getMinecraft().renderGlobal);
		}

		RenderItem renderItem = new IItemRendererRenderItem(Minecraft.getMinecraft().getRenderItem(), Minecraft.getMinecraft().renderEngine, Minecraft.getMinecraft().getItemColors());
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), renderItem, "renderItem", "field_175621_X");

		RenderManager renderManager = new RenderManager(Minecraft.getMinecraft().renderEngine, renderItem);
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), renderManager, "renderManager", "field_175616_W");

		ItemRenderer itemRenderer = new ItemRenderer(Minecraft.getMinecraft());
		mcResourceManager.registerReloadListener(renderItem);
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), itemRenderer, "itemRenderer", "field_175620_Y");

		EntityRenderer entityRenderer = new EntityRenderer(Minecraft.getMinecraft(), mcResourceManager);
		mcResourceManager.registerReloadListener(entityRenderer);
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), entityRenderer, "entityRenderer", "field_71460_t");

		RenderGlobal renderGlobal = new RenderGlobal(Minecraft.getMinecraft());
		mcResourceManager.registerReloadListener(renderGlobal);
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), renderGlobal, "renderGlobal", "field_71438_f");
	}

}
