package com.elix_x.itemrender.compat.asm;

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

import com.evilnotch.lib.api.MCPSidedString;
import com.evilnotch.lib.asm.ASMHelper;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.MCWriter;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class RenderTransformer  implements IClassTransformer{
	
    public static final List<String> classesBeingTransformed = JavaUtil.<String>asArray(new Object[]
    {
    		"mezz.jei.render.IngredientListBatchRenderer",
    		"net.minecraftforge.client.ForgeHooksClient"
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
			switch (index)
			{
			  case 0:
				MethodNode method = ASMHelper.getMethodNode(classNode, "set", "(Lmezz/jei/render/IngredientListSlot;Lmezz/jei/gui/ingredients/IIngredientListElement;)V");

				AbstractInsnNode[] arr = method.instructions.toArray();
				AbstractInsnNode spot = null;
				for(int i=arr.length-1;i>=0;i--)
				{
					AbstractInsnNode node = arr[i];
					if(node.getOpcode() == Opcodes.INSTANCEOF && node instanceof TypeInsnNode)
					{
						TypeInsnNode type = (TypeInsnNode)node;
						if(type.desc.equals("mezz/jei/api/ingredients/ISlowRenderItem"))
						{
							spot = type;
							System.out.println(spot.getOpcode() + " next:" + spot.getNext().getOpcode() );
							while(spot.getOpcode() != Opcodes.IFNE)
							{
								spot = spot.getNext();
								if(spot.getOpcode() == Opcodes.RETURN)
									break;
							}
							break;
						}
					}
				}
			
				JumpInsnNode spotting = (JumpInsnNode)spot;
				int varLoad = ASMHelper.getLocalVarIndexFromOwner(method,"Lnet/minecraft/item/ItemStack;");
				InsnList toInsert = new InsnList();
				toInsert.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/elix_x/itemrender/compat/asm/JEIRenderer", "slowItems", "Ljava/util/Set;"));
				toInsert.add(new VarInsnNode(ALOAD,varLoad));
		   		toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/item/ItemStack", new MCPSidedString("getItem","func_77973_b").toString(), "()Lnet/minecraft/item/Item;", false));
		   		toInsert.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"java/util/Set", "contains", "(Ljava/lang/Object;)Z", true));
		   		toInsert.add(new JumpInsnNode(Opcodes.IFNE,spotting.label));
		   		method.instructions.insert(spotting,toInsert);
		   	  break;

            	
			  case 1:
				  String stream = "com/elix_x/itemrender/compat/asm/ASMHooks";
				  MethodNode camera = ASMHelper.replaceMethod(classNode, stream + ".class", "handleCameraTransforms", "(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)Lnet/minecraft/client/renderer/block/model/IBakedModel;", "handleCameraTransforms");
				  ASMHelper.patchMethod(camera, "net.minecraftforge.client.ForgeHooksClient", stream,true);
			  break;
			  
			}
			ASMHelper.clearCacheNodes();
	   		ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        	classNode.accept(classWriter);
        	
        	int origin = index;
        	index = index == 0 ? 14 : 15;
        	
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
