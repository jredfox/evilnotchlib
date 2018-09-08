package com.elix_x.itemrender;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IItemRendererHandler {

	private static Map<Item, IItemRenderer> renderers = new HashMap<>();

	private static TransformType currentTransformType;

	public static IItemRenderer getIItemRenderer(Item item){
		return renderers.get(item);
	}

	public static IItemRenderer getIItemRenderer(ItemStack itemstack){
		return itemstack != null ? getIItemRenderer(itemstack.getItem()) : null;
	}
	public static boolean hasKey(ItemStack stack){
		return stack != null ? renderers.containsKey(stack.getItem()) : false;
	}

	public static void registerIItemRenderer(Item item, IItemRenderer renderer){
		renderers.put(item, renderer);
	}

	public static void handleCameraTransforms(TransformType type){
		currentTransformType = type;
	}

	public static boolean renderPre(RenderItem renderItem, ItemStack itemstack, IBakedModel model){
		IItemRenderer renderer = getIItemRenderer(itemstack);
		if(renderer != null){
			return renderer.renderPre(renderItem, itemstack, model, currentTransformType);
		} else {
			return true;
		}
	}

	public static void renderPost(RenderItem renderItem, ItemStack itemstack, IBakedModel model){
		IItemRenderer renderer = getIItemRenderer(itemstack);
		if(renderer != null){
			renderer.renderPost(renderItem, itemstack, model, currentTransformType);
		}
	}

	public static boolean renderItemOverlayIntoGUIPre(RenderItem renderItem, FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, @Nullable String text){
		IItemRenderer renderer = getIItemRenderer(itemstack);
		if(renderer != null){
			return renderer.renderItemOverlayIntoGUIPre(renderItem, fr, itemstack, xPosition, yPosition, text);
		} else {
			return true;
		}
	}

	public static void renderItemOverlayIntoGUIPost(RenderItem renderItem, FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, @Nullable String text){
		IItemRenderer renderer = getIItemRenderer(itemstack);
		if(renderer != null){
			renderer.renderItemOverlayIntoGUIPost(renderItem, fr, itemstack, xPosition, yPosition, text);
		}
	}

}
