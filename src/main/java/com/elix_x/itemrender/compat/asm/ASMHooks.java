package com.elix_x.itemrender.compat.asm;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.elix_x.itemrender.IItemRendererHandler;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.client.ForgeHooksClient;

public class ASMHooks {
	
	private static final Matrix4f flipX = null;
	
    public static IBakedModel handleCameraTransforms(IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType, boolean leftHandHackery)
    {
    	IItemRendererHandler.handleCameraTransforms(cameraTransformType);
        Pair<? extends IBakedModel, Matrix4f> pair = model.handlePerspective(cameraTransformType);

        if (pair.getRight() != null)
        {
            Matrix4f matrix = new Matrix4f(pair.getRight());
            if (leftHandHackery)
            {
                matrix.mul(flipX, matrix);
                matrix.mul(matrix, flipX);
            }
            ForgeHooksClient.multiplyCurrentGlMatrix(matrix);
        }
        return pair.getLeft();
    }

}
