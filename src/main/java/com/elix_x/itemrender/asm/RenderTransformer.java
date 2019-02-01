package com.elix_x.itemrender.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.elix_x.itemrender.asm.compat.JEI;
import com.elix_x.itemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.asm.util.MCWriter;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class RenderTransformer  implements IClassTransformer{
	
    public static final List<String> classesBeingTransformed = JavaUtil.<String>asArray(new Object[]
    {
		"net.minecraftforge.client.ForgeHooksClient",
		"mezz.jei.render.IngredientListBatchRenderer"
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) 
	{
		if(basicClass == null)
			return null;
		int index = classesBeingTransformed.indexOf(transformedName);
	    return index != -1 ? transform(index, basicClass, FMLCorePlugin.isObf) : basicClass;
	}
	/**
	 * f**k jei
	 */
	public byte[] transform(int index, byte[] basicClass, boolean obfuscated) 
	{
		try 
		{
			ClassNode classNode = ASMHelper.getClassNode(basicClass);
			switch (index)
			{
			  case 0:
				  System.out.println("patching ForgeHooksClient#handleCameraTransforms");
				  MethodNode camera = ASMHelper.getMethodNode(classNode, "handleCameraTransforms", "(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)Lnet/minecraft/client/renderer/block/model/IBakedModel;");
				 
				  InsnList toInsert0 = new InsnList();
			      toInsert0.add(new VarInsnNode(ALOAD,1));
			      toInsert0.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/elix_x/itemrender/handlers/IItemRendererHandler", "handleCameraTransforms", "(Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V", false));
			      camera.instructions.insertBefore(ASMHelper.getFirstInstruction(camera, Opcodes.ALOAD), toInsert0);
			  break;
			  
			  case 1:
				  JEI.patchJEI(classNode);
		   	  break;
			  
			}
			ASMHelper.clearCacheNodes();
	   		ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        	classNode.accept(classWriter);
        	
        	int origin = index;
        	index += 14;
        	
            if(index == ConfigCore.cfgIndex || ConfigCore.cfgIndex == -2)
            {
          	  String[] a = classesBeingTransformed.get(origin).split("\\.");
          	  File f = new File(System.getProperty("user.home") + "/Desktop/" + a[a.length-1] + ".class");
          	  FileUtils.writeByteArrayToFile(f, classWriter.toByteArray());
            }
            
        	return classWriter.toByteArray();
		}
		catch (Throwable e) 
		{
			e.printStackTrace();
			return basicClass;
		}
	}

}
