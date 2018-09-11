package com.elix_x.itemrender.compat.asm;

import java.io.IOException;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.EvilNotch.lib.asm.ASMHelper;
import com.EvilNotch.lib.asm.FMLCorePlugin;
import com.EvilNotch.lib.util.JavaUtil;
import com.elix_x.itemrender.mod.IItemRendererMod;

import net.minecraft.launchwrapper.IClassTransformer;

public class JEITransformer  implements IClassTransformer{
	
    public static final List<String> classesBeingTransformed = (List<String>)JavaUtil.<String>asArray2(new Object[]
    {
    		"mezz.jei.render.IngredientListBatchRenderer"
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		int index = classesBeingTransformed.indexOf(transformedName);
	    return index != -1 ? transform(index, basicClass, FMLCorePlugin.isObf) : basicClass;
	}
	/**
	 * f**k jei
	 */
	public byte[] transform(int index, byte[] basicClass, boolean obfuscated) 
	{
		System.out.println("Transforming JEI to Accept IItemRenderer");
		try 
		{
			ClassNode classNode = ASMHelper.getClassNode(basicClass);
			MethodNode test = ASMHelper.getMethodNode(classNode, "<clinit>", "()V");
			System.out.println("Class Initializer is methodNode:" + (test != null));
			String inputBase = "assets/" + IItemRendererMod.MODID + "/asm/" + (obfuscated ? "srg/" : "deob/");
			return ASMHelper.replaceClass(inputBase + "IngredientListBatchRenderer");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return basicClass;
		}
	}

}
