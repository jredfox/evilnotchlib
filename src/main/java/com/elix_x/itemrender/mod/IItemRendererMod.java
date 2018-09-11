package com.elix_x.itemrender.mod;

import java.lang.reflect.Method;
import java.util.List;

import com.EvilNotch.lib.Api.MCPSidedString;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.elix_x.itemrender.IItemRendererHandler;
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
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = IItemRendererMod.MODID, name = IItemRendererMod.NAME, version = IItemRendererMod.VERSION, clientSideOnly = true)
public class IItemRendererMod {

	public static final String MODID = "iitemrenderer";
	public static final String NAME = "IItem Renderer";
	public static final String VERSION = "1.2";

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
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), renderItem, new MCPSidedString("renderItem", "field_175621_X").toString());

		RenderManager renderManager = new RenderManager(Minecraft.getMinecraft().renderEngine, renderItem);
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), renderManager, new MCPSidedString("renderManager", "field_175616_W").toString());

		ItemRenderer itemRenderer = new ItemRenderer(Minecraft.getMinecraft());
		mcResourceManager.registerReloadListener(renderItem);
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), itemRenderer, new MCPSidedString("itemRenderer", "field_175620_Y").toString());

		EntityRenderer entityRenderer = new EntityRenderer(Minecraft.getMinecraft(), mcResourceManager);
		mcResourceManager.registerReloadListener(entityRenderer);
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), entityRenderer, new MCPSidedString("entityRenderer", "field_71460_t").toString());

		RenderGlobal renderGlobal = new RenderGlobal(Minecraft.getMinecraft());
		mcResourceManager.registerReloadListener(renderGlobal);
		ReflectionHelper.setPrivateValue(Minecraft.class, Minecraft.getMinecraft(), renderGlobal, new MCPSidedString("renderGlobal", "field_71438_f").toString());
	}
	/**
	 * add jei support to the tabs
	 */
	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{
		if(Loader.isModLoaded("jei"))
		{
			Method addSlowRenderer = ReflectionUtil.getMethod(ReflectionUtil.classForName("mezz.jei.render.IngredientListBatchRenderer"),"addSlowRenderer",Item.class);
			try
			{
				for(Item i : IItemRendererHandler.getItems())
				{
					addSlowRenderer.invoke(null,i);
				}
			}
			catch(Throwable e)
			{
				System.out.println("JEI Patcher Isn't working with the current Build of JEI Report to EvilNotchLib issue on github");
				e.printStackTrace();
			}
		}
	}

}
