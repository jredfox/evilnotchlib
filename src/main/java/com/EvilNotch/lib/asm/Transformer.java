package com.EvilNotch.lib.asm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mojang.authlib.minecraft.InsecureTextureException;

import static org.objectweb.asm.Opcodes.*;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerList2;

public class Transformer implements IClassTransformer
{
    private static final  String[] classesBeingTransformed = 
    	{
    			"net.minecraft.server.management.PlayerList",
    			"net.minecraft.block.BlockCactus"
    	};
    	
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] classToTransform)
    {
            int index = Arrays.asList(classesBeingTransformed).indexOf(transformedName);
            return index != -1 ? transform(index, classToTransform, FMLCorePlugin.isObf) : classToTransform;
    }
    
    private static byte[] transform(int index, byte[] classToTransform,boolean obfuscated)
    {
    	System.out.println("Transforming: " + classesBeingTransformed[index]);
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classToTransform);
            classReader.accept(classNode, 0);

            switch(index)
            {
                case 0:
//                	TestTransformer.transformClass(classNode, PlayerList2.class, "getPlayerNBT",  "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "getPlayerNBT","(Loq;)Lfy;");
//                	TestTransformer.transformClass(classNode, PlayerList2.class, "readPlayerDataFromFile", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "a", "(Loq;)Lfy;");
                	OtherTransformer.transformOther(classNode, obfuscated);
                break;
                case 1:
                	//debugger test for void methods
//                	TestTransformer.transformClass(classNode, Methods.class, "onEntityCollidedWithBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;)V", "a", "(Lamu;Let;Lawt;Lvg;)V");
                break;
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
//            FileUtils.writeByteArrayToFile(new File("C:/Users/jredfox/Desktop/test.class"), classWriter.toByteArray());
            
            return classWriter.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return classToTransform;
    }

}
