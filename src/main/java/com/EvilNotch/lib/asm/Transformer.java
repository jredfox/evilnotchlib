package com.EvilNotch.lib.asm;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer
{
    public static final List<String> classesBeingTransformed = JavaUtil.asArray(new String[]
    {
    	"net.minecraft.server.management.PlayerList",
    	"net.minecraft.tileentity.TileEntityFurnace"
    });	
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] classToTransform)
    {
        int index = classesBeingTransformed.indexOf(transformedName);
        return index != -1 ? transform(index, classToTransform, FMLCorePlugin.isObf) : classToTransform;
    }
    
    public static byte[] transform(int index, byte[] classToTransform,boolean obfuscated)
    {
    	String name = classesBeingTransformed.get(index);
   		System.out.println("Transforming: " + name);
    	
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classToTransform);
            classReader.accept(classNode, 0);
            String inputBase = "assets/evilnotchlib/asm/";

            switch(index)
            {
                case 0:
                	if(!FMLCorePlugin.isObf || !ConfigCore.asm_playerlist)
                	{
                		System.out.println("returning default class:" + name + " ob:" + FMLCorePlugin.isObf + " cfg:" + ConfigCore.asm_playerlist);
                		return classToTransform;
                	}
                	TestTransformer.transformMethod(classNode, classesBeingTransformed.get(index), inputBase + "PlayerList", "getPlayerNBT",  "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "getPlayerNBT","(Loq;)Lfy;","getPlayerNBT");
                	TestTransformer.transformMethod(classNode, classesBeingTransformed.get(index), inputBase + "PlayerList", "readPlayerDataFromFile", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "a", "(Loq;)Lfy;","func_72380_a");
                	OtherTransformer.injectUUIDPatcher(classNode, obfuscated);
                	TestTransformer.clearCacheNodes();
                break;
                
                case 1:
                	if(!ConfigCore.asm_furnace)
                	{
                		System.out.println("returning default class:" + name);
                		return classToTransform;
                	}
                	FurnaceTransformer.transformMethod(classNode,obfuscated);
                break;
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
//            if(index == 1)
//            	FileUtils.writeByteArrayToFile(new File("C:/Users/jredfox/Desktop/test.class"), classWriter.toByteArray());
            
            return classWriter.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return classToTransform;
    }

}
