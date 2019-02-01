package com.evilnotch.menulib.asm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.asm.util.MCWriter;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class MenuLibTransformer implements IClassTransformer{
	
    public static final List<String> clazzes = (List<String>)JavaUtil.<String>asArray(new Object[]
    {
    		"lumien.custommainmenu.handler.CMMEventHandler",
    		"lumien.custommainmenu.gui.GuiCustom",
    		"net.minecraft.client.audio.MusicTicker"
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) 
	{
		if(bytes == null)
			return null;
		int index = clazzes.indexOf(transformedName);
        try 
        {
			return index != -1 ? transform(index, bytes, FMLCorePlugin.isObf) : bytes;
		} 
        catch (Throwable e) 
        {
			e.printStackTrace();
		}
        return bytes;
	}

	public byte[] transform(int index, byte[] bytes, boolean isObf) throws IOException 
	{
		String name =  clazzes.get(index);
		System.out.println("MenuLib Transforming:" + name);
		ClassNode classNode = ASMHelper.getClassNode(bytes);
		String inputBase = "assets/menulib/asm/" + (isObf ? "srg/" : "deob/");
		switch (index)
		{
			case 0:
				return ASMHelper.replaceClass(inputBase + "CMMEventHandler");//99% re-coded edited or removed as his ideas where wrong
			case 1:
				MethodNode node = ASMHelper.replaceMethod(classNode, inputBase + "GuiCustom", "initGui", "()V", "func_73866_w_");
				
				if(isObf)
				for(AbstractInsnNode n : node.instructions.toArray())
				{
					if(n instanceof FieldInsnNode)
					{
						FieldInsnNode feild = (FieldInsnNode)n;
						if(feild.name.equals("backgroundTexture"))
						{
							feild.name = "field_110351_G";//patch call
						}
					}
				}
				ASMHelper.replaceMethod(classNode, inputBase + "GuiCustom","actionPerformed", "(Lnet/minecraft/client/gui/GuiButton;)V", "func_146284_a");
			break;
			case 2:
				ASMHelper.replaceMethod(classNode, inputBase + "MusicTicker", "update", "()V", "func_73660_a");
				ASMHelper.addMethod(classNode, inputBase + "MusicTicker", "isMenu", "(Lnet/minecraft/client/gui/GuiScreen;)Z");
			break;
		}
		ASMHelper.clearCacheNodes();
        ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        
        if(index == ConfigCore.cfgIndex || ConfigCore.cfgIndex == -2 || true)
        {
      	  String[] a = name.split("\\.");
      	  File f = new File(System.getProperty("user.home") + "/Desktop/" + a[a.length-1] + ".class");
      	  FileUtils.writeByteArrayToFile(f, classWriter.toByteArray());
        }
		return classWriter.toByteArray();
	}

}
