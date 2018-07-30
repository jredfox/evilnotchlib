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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class Transformer implements IClassTransformer
{
    public static final List<String> classesBeingTransformed = (List<String>)JavaUtil.<String>asArray2(new Object[]
    {
    	"net.minecraft.server.management.PlayerList",
    	"net.minecraft.tileentity.TileEntityFurnace",
    	"net.minecraft.client.gui.inventory.GuiFurnace",
    	"net.minecraft.item.ItemStack",
    	"net.minecraft.item.ItemBlock"
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
                	TestTransformer.transformMethod(classNode, name, inputBase + "PlayerList", "getPlayerNBT",  "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "getPlayerNBT","(Loq;)Lfy;","getPlayerNBT");
                	TestTransformer.transformMethod(classNode, name, inputBase + "PlayerList", "readPlayerDataFromFile", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", "a", "(Loq;)Lfy;","func_72380_a");
                	OtherTransformer.injectUUIDPatcher(classNode, obfuscated);
                break;
                
                case 1:
                	if(!ConfigCore.asm_furnace)
                	{
                		System.out.println("returning default class:" + name);
                		return classToTransform;
                	}
                	if(FMLCorePlugin.isObf)
                	{
                		TestTransformer.transformMethod(classNode, name, inputBase + "TileEntityFurnace", "readFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)V", "a", "(Lfy;)V", "func_145839_a");
                		TestTransformer.transformMethod(classNode, name, inputBase + "TileEntityFurnace", "writeToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", "b", "(Lfy;)Lfy;", "func_189515_b");
                	}
                	else
                	{
                		FurnaceTransformer.transformMethod(classNode,obfuscated);
                	}
                break;
                
                case 2:
                	if(!ConfigCore.asm_furnace || !FMLCorePlugin.isObf)
                		return classToTransform;
                	TestTransformer.transformMethod(classNode, name, inputBase + "GuiFurnace", "getBurnLeftScaled", "(I)I", "i", "(I)I", "func_175382_i");
                break;
                
                case 3:
                	if(!ConfigCore.asm_clientPlaceEvent || !FMLCorePlugin.isObf)
                	{
                		System.out.println("returning bytes ITEMSTACK");
                		return classToTransform;
                	}
                	TestTransformer.transformMethod(classNode, name, inputBase + "ItemStack", "onItemUse", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/EnumFacing;FFF)Lnet/minecraft/util/EnumActionResult;", "a", "(Laed;Lamu;Let;Lub;Lfa;FFF)Lud;", "func_179546_a");
                break;
                
                case 4:
                	if(!ConfigCore.asm_setTileNBTFix || !FMLCorePlugin.isObf)
                		return classToTransform;
                	TestTransformer.transformMethod(classNode, name, inputBase + "ItemBlock", "setTileEntityNBT", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)Z", "a", "(Lamu;Laed;Let;Laip;)Z", "func_179224_a");
                break;
            }
        	TestTransformer.clearCacheNodes();

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
//          if(index == 4)
//        	FileUtils.writeByteArrayToFile(new File("C:/Users/jredfox/Desktop/test.class"), classWriter.toByteArray());
            
            return classWriter.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return classToTransform;
    }

}
