package com.EvilNotch.lib.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.EvilNotch.lib.Api.MCPSidedString;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.nbt.NBTTagCompound;
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
    	"net.minecraft.item.ItemBlock",
    	"net.minecraft.network.play.server.SPacketUpdateTileEntity",
    	"net.minecraft.network.NetHandlerPlayServer",
    	"net.minecraft.entity.Entity"//capabilities start here
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
            String inputBase = "assets/evilnotchlib/asm/" + (obfuscated ? "srg/" : "deob/");
            String origin = "assets/evilnotchlib/asm/";
            switch(index)
            {
                case 0:
                	if(!ConfigCore.asm_playerlist)
                	{
                		System.out.println("returning default class:" + name + " ob:" + obfuscated + " cfg:" + ConfigCore.asm_playerlist);
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
                	TestTransformer.transformMethod(classNode, name, inputBase + "TileEntityFurnace", "readFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)V", "a", "(Lfy;)V", "func_145839_a");
                	TestTransformer.transformMethod(classNode, name, inputBase + "TileEntityFurnace", "writeToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", "b", "(Lfy;)Lfy;", "func_189515_b");
                break;
                
                case 2:
                	if(!ConfigCore.asm_furnace)
                		return classToTransform;
                	TestTransformer.transformMethod(classNode, name, inputBase + "GuiFurnace", "getBurnLeftScaled", "(I)I", "i", "(I)I", "func_175382_i");
                break;
                
                case 3:
                	if(!ConfigCore.asm_clientPlaceEvent)
                	{
                		System.out.println("returning bytes ITEMSTACK");
                		return classToTransform;
                	}
                	if(true)
                	{
                		TestTransformer.transformMethod(classNode, name, inputBase + "ItemStack", "onItemUse", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumHand;Lnet/minecraft/util/EnumFacing;FFF)Lnet/minecraft/util/EnumActionResult;", "a", "(Laed;Lamu;Let;Lub;Lfa;FFF)Lud;", "func_179546_a");
                		TestTransformer.transformMethod(classNode, name, inputBase + "ItemStack", "getDisplayName", "()Ljava/lang/String;", "r", "()Ljava/lang/String;", "func_82833_r");
                		ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                		classNode.accept(classWriter);
//                		FileUtils.writeByteArrayToFile(new File("C:/Users/jredfox/Desktop/test.class"), classWriter.toByteArray());
                		return classWriter.toByteArray();
                	}
                break;
                
                case 4:
                	if(!ConfigCore.asm_setTileNBTFix)
                		return classToTransform;
                	TestTransformer.transformMethod(classNode, name, inputBase + "ItemBlock", "setTileEntityNBT", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)Z", "a", "(Lamu;Laed;Let;Laip;)Z", "func_179224_a");
//                	TestTransformer.removeMethod(classNode,name,inputBase + "ItemBlock","setTileNBT","(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;Lnet/minecraft/nbt/NBTTagCompound;Z)Z");
                	if(obfuscated)
                		TestTransformer.addMethod(classNode,name,inputBase + "ItemBlock","setTileNBT","(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;Lnet/minecraft/nbt/NBTTagCompound;Z)Z");
                break;
                
                case 5:
                		return TestTransformer.replaceClass(inputBase + "SPacketUpdateTileEntity");
                case 6:
                	TestTransformer.transformMethod(classNode, name, inputBase + "NetHandlerPlayServer", "processTryUseItemOnBlock", "(Lnet/minecraft/network/play/client/CPacketPlayerTryUseItemOnBlock;)V", "a", "(Lma;)V", "func_184337_a");
                break;
                //custom capability system to the Entity.class
                case 7:
                	TestTransformer.addFeild(classNode, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;","Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer<Lnet/minecraft/entity/Entity;>;");
                	
                	//add interface and implement methods
                	TestTransformer.addInterface(classNode, "com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider");
                	MethodNode getCap = TestTransformer.addMethod(classNode, name,"com/EvilNotch/lib/asm/Caps.class", "getCapContainer", "()Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;");
                	TestTransformer.patchLocals(getCap, name);
                	TestTransformer.patchInstructions(getCap, name, "com/EvilNotch/lib/asm/Caps");
                	
                	MethodNode setCap = TestTransformer.addMethod(classNode, name,"com/EvilNotch/lib/asm/Caps.class", "setCapContainer", "(Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;)V");
                	TestTransformer.patchLocals(setCap, name);
                	TestTransformer.patchInstructions(setCap, name, "com/EvilNotch/lib/asm/Caps");
                	
                	//serealization and ticks
                	MethodNode readFromNBT = TestTransformer.getMethodNode(classNode, new MCPSidedString("readFromNBT","f").toString(), new MCPSidedString("(Lnet/minecraft/nbt/NBTTagCompound;)V","(Lfy;)V").toString());
				
                	String readDesc = new MCPSidedString("(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V","(Ljava/lang/Object;Lfy;)V").toString();

                	//readFromNBT
                	InsnList toInsert1 = new InsnList();
                	toInsert1.add(new VarInsnNode(ALOAD,0));
                	toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/Entity", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
                	toInsert1.add(new VarInsnNode(ALOAD,0));
                	toInsert1.add(new VarInsnNode(ALOAD,1));
                	toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
                	AbstractInsnNode spotNode = TestTransformer.getFirstInstruction(readFromNBT, false, Opcodes.ALOAD);
                	readFromNBT.instructions.insertBefore(spotNode,toInsert1);
                	
                    MethodNode writeToNBT = TestTransformer.getMethodNode(classNode, new MCPSidedString("writeToNBT","f").toString(), new MCPSidedString("(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;","(Lfy;)Lnet/minecraft/nbt/NBTTagCompound;").toString());
    				
                    //writeToNBT
                    InsnList toInsert2 = new InsnList();
                    toInsert2.add(new VarInsnNode(ALOAD,0));
                    toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,  "net/minecraft/entity/Entity", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
                    toInsert2.add(new VarInsnNode(ALOAD,0));
                    toInsert2.add(new VarInsnNode(ALOAD,1));
               	    toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V",false));
               	    AbstractInsnNode spotWriteNode = TestTransformer.getFirstInstruction(writeToNBT, false, Opcodes.ALOAD);
               	    writeToNBT.instructions.insertBefore(spotWriteNode,toInsert2);

               	    //tick injection
               	    MethodNode tick = TestTransformer.getMethodNode(classNode, new MCPSidedString("onEntityUpdate","f").toString(), "()V");
               	    InsnList isntick = new InsnList();
               	    isntick.add(new VarInsnNode(ALOAD,0));
               	    isntick.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/entity/Entity", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
               	    isntick.add(new VarInsnNode(ALOAD,0));
               	    isntick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
               	    AbstractInsnNode spotTickNode = TestTransformer.getFirstInstruction(tick, true, -1);
             	    tick.instructions.insert(spotTickNode,isntick);
                break;
            }
            
        	TestTransformer.clearCacheNodes();

            ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
          if(index == 7)
          {
        	  System.out.println(classNode.interfaces);
        	  FileUtils.writeByteArrayToFile(new File("C:/Users/jredfox/Desktop/test.class"), classWriter.toByteArray());
          }
            
            return classWriter.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return classToTransform;
    }

	public static String getFieldNodeString(FieldNode node) {
		return node.name + " desc:" + node.desc + " signature:" + node.signature + " access:" + node.access;
 	}

}
