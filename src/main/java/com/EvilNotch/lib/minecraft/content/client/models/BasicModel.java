package com.EvilNotch.lib.minecraft.content.client.models;

import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class BasicModel implements IBakedModel{
	public IBakedModel model = null;
	public String texture = null;
	public TextureAtlasSprite sprite = null;
	
	public BasicModel(IBakedModel basemodel,String icon) 
	{
		this.model = basemodel;
		this.texture = icon;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long seed) {
		return this.model.getQuads(state,side,seed);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return this.model.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return this.model.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return this.model.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() 
	{
		if(this.sprite == null)
			this.sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(this.texture);
		return this.sprite;
	}
    @Override
    public ItemOverrideList getOverrides()
    {
        return this.model.getOverrides();
    }
    
    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return this.model.getItemCameraTransforms();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        return this.model.handlePerspective(cameraTransformType);
    }

}
