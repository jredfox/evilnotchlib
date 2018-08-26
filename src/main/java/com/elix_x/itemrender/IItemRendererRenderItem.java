package com.elix_x.itemrender;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class IItemRendererRenderItem extends RenderItem {

	private boolean tth = false;

	public IItemRendererRenderItem(RenderItem renderItem, TextureManager textureManager, ItemColors itemColors){
		super(textureManager, renderItem.getItemModelMesher().getModelManager(), itemColors);
		try {
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			Field itemModelMesher = ReflectionHelper.findField(RenderItem.class, "itemModelMesher", "field_175059_m");
			itemModelMesher.setAccessible(true);;
			modifiers.set(itemModelMesher, itemModelMesher.getModifiers() & (~Modifier.FINAL));
			itemModelMesher.set(this, renderItem.getItemModelMesher());
		} catch(Exception e){
			e.printStackTrace();
			FMLCommonHandler.instance().exitJava(-1, true);
		}
	}

	@Override
	public void renderItem(ItemStack itemstack, IBakedModel model){
		if(!tth) IItemRendererHandler.handleCameraTransforms(TransformType.GROUND);
		tth = false;
		if(IItemRendererHandler.renderPre(this, itemstack, model)){
			super.renderItem(itemstack, model);
			IItemRendererHandler.renderPost(this, itemstack, model);
		}
	}

	@Override
	public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack itemstack, int xPosition, int yPosition, String text){
		if(IItemRendererHandler.renderItemOverlayIntoGUIPre(this, fr, itemstack, xPosition, yPosition, text)){
			super.renderItemOverlayIntoGUI(fr, itemstack, xPosition, yPosition, text);
			IItemRendererHandler.renderItemOverlayIntoGUIPost(this, fr, itemstack, xPosition, yPosition, text);
		}
	}

	@Override
	protected void renderItemModel(ItemStack stack, IBakedModel bakedmodel, TransformType transform, boolean leftHanded){
		IItemRendererHandler.handleCameraTransforms(transform);
		tth = true;
		super.renderItemModel(stack, bakedmodel, transform, leftHanded);
	}

	@Override
	protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel){
		IItemRendererHandler.handleCameraTransforms(TransformType.GUI);
		tth = true;
		super.renderItemModelIntoGUI(stack, x, y, bakedmodel);
	}

}
