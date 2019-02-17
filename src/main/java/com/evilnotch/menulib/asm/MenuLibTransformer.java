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

import com.evilnotch.iitemrender.asm.RenderTransformer;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.transformer.Transformer;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.asm.util.MCWriter;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class MenuLibTransformer implements IClassTransformer{
	
    public static final List<String> clazzes = (List<String>)JavaUtil.<String>asArray(new Object[]
    {
   		"net.minecraft.client.audio.MusicTicker",
    	"lumien.custommainmenu.handler.CMMEventHandler",
    	"lumien.custommainmenu.gui.GuiCustom",
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) 
	{
		if(bytes == null)
		{
			return null;
		}
		
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

	public byte[] transform(int index, byte[] clazz, boolean isObf) throws IOException 
	{
		String name =  clazzes.get(index);
		ClassNode classNode = ASMHelper.getClassNode(clazz);
		String inputBase = "assets/menulib/asm/" + (isObf ? "srg/" : "deob/");
		
		System.out.println("MenuLib Transforming:" + name);
		
		switch (index)
		{
			case 0:
				ASMHelper.replaceMethod(classNode, inputBase + "MusicTicker", "update", "()V", "func_73660_a");
				ASMHelper.addMethod(classNode, inputBase + "MusicTicker", "isMenu", "(Lnet/minecraft/client/gui/GuiScreen;)Z");
			break;
			
			case 1:
				return ASMHelper.replaceClass(inputBase + "CMMEventHandler");//99% re-coded edited or removed as his ideas where wrong
			
			case 2:
				MethodNode node = ASMHelper.replaceMethod(classNode, inputBase + "GuiCustom", "initGui", "()V", "func_73866_w_");
				ASMHelper.replaceMethod(classNode, inputBase + "GuiCustom","actionPerformed", "(Lnet/minecraft/client/gui/GuiButton;)V", "func_146284_a");
			break;
		}
		
		ASMHelper.clearCacheNodes();
        
		ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        
        byte[] bytes = classWriter.toByteArray();
        if(ConfigCore.dumpASM)
        {
        	ASMHelper.dumpFile(name,bytes);
        }
		return bytes;
	}

}
