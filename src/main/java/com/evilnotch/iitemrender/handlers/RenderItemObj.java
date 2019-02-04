package com.evilnotch.iitemrender.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderItemObj extends RenderItem {
	
	public RenderItem child;

	public RenderItemObj(RenderItem renderItem)
	{
		super(Minecraft.getMinecraft().getTextureManager(), renderItem.getItemModelMesher().getModelManager(), Minecraft.getMinecraft().getItemColors());
		this.child = renderItem;
	}

	@Override
	public void renderItem(ItemStack itemstack, IBakedModel model)
	{
		if(IItemRendererHandler.renderPre(this, itemstack, model))
		{
			this.child.renderItem(itemstack, model);
			IItemRendererHandler.renderPost(this, itemstack, model);
		}
	}

	@Override
	public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, String text)
	{
		if(IItemRendererHandler.renderItemOverlayIntoGUIPre(this, fr, itemstack, xPosition, yPosition, text))
		{
			this.child.renderItemOverlayIntoGUI(fr, itemstack, xPosition, yPosition, text);
			IItemRendererHandler.renderItemOverlayIntoGUIPost(this, fr, itemstack, xPosition, yPosition, text);
		}
	}
}
