package com.elix_x.itemrender;

import java.lang.reflect.Method;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Main class of IItem Renderer API.
 * <br><br>
 * Here you register your {@link IItemRenderer}.
 * <br><br>
 * This API in whole allows use of GL calls during rendering, super-ultra-hyper dynamic rendering, and manipulations of GUI overlay.
 * <br><br>
 * This API is built entirely without ASM.
 * <br><br>
 * So now, you ask: "If this is so simple and possible without ASM, why can't i copy all your code to my project and don't use IItem Renderer API?".
 * <br>
 * Well, that's a good question, so here's your answer: if every body that wants to do this, does this with his own reflection block (even if copied from IItem Renderer), it will cause incompatibilities between every user of such code block, because of all the side effects it has.
 * @author elix_x
 *
 */
public class IItemRendererAPI {

	private static Method registerIItemRenderer;

	static {
		try {
			registerIItemRenderer = Class.forName("zdoctor.lazymodder.client.render.itemrender.IItemRendererHandler").getMethod("registerIItemRenderer", Item.class, IItemRenderer.class);
		} catch(Exception e){
			e.printStackTrace();
			FMLCommonHandler.instance().exitJava(-1, true);
		}
	}

	/**
	 * Method to register your IItemRenderer
	 * @param item {@link Item} that this {@link IItemRenderer} handles
	 * @param renderer {@link IItemRenderer} that handles rendering of that item
	 */
	public static void registerIItemRenderer(Item item, IItemRenderer renderer){
		try {
			registerIItemRenderer.invoke(null, item, renderer);
		} catch(Exception e){
			e.printStackTrace();
			FMLCommonHandler.instance().exitJava(-1, true);
		}
	}

}
