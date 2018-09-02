package com.EvilNotch.lib.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.io.IOException;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.EvilNotch.lib.Api.MCPSidedString;


public class CapTransformer {

	public static void transFormEntityCaps(String name, ClassNode classNode, boolean obfuscated) throws IOException 
	{
    	implementICapProvider(classNode,name,"Lnet/minecraft/entity/Entity;");
    	
    	//Serialization and ticks
    	String owner = new MCPSidedString("net/minecraft/entity/Entity","vg").toString();
    	String desc_nbt = new MCPSidedString("Lnet/minecraft/nbt/NBTTagCompound;","Lfy;").toString();
    	String readDesc = "(Ljava/lang/Object;" + desc_nbt + ")V";
    	
     	String method_readFromNBT = new MCPSidedString("readFromNBT","f").toString();
    	MethodNode readFromNBT = TestTransformer.getMethodNode(classNode, method_readFromNBT, "(" + desc_nbt + ")V");
    	
    	//readFromNBT
    	InsnList toInsert1 = new InsnList();
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new VarInsnNode(ALOAD,1));
    	toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", readDesc, false));
    	AbstractInsnNode spotNode = TestTransformer.getFirstInstruction(readFromNBT, false, Opcodes.ALOAD);
    	readFromNBT.instructions.insertBefore(spotNode,toInsert1);
    	
    	String method_writeToNBT = new MCPSidedString("writeToNBT","f").toString();
        MethodNode writeToNBT = TestTransformer.getMethodNode(classNode,method_writeToNBT, "(" + desc_nbt + ")" + desc_nbt);
		
        //writeToNBT
        InsnList toInsert2 = new InsnList();
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,  owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new VarInsnNode(ALOAD,1));
   	    toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", readDesc,false));
   	    AbstractInsnNode spotWriteNode = TestTransformer.getFirstInstruction(writeToNBT, false, Opcodes.ALOAD);
   	    writeToNBT.instructions.insertBefore(spotWriteNode,toInsert2);

   	    //tick injection
   	    MethodNode tick = TestTransformer.getMethodNode(classNode, new MCPSidedString("onEntityUpdate","Y").toString(), "()V");
   	    InsnList isntick = new InsnList();
   	    isntick.add(new VarInsnNode(ALOAD,0));
   	    isntick.add(new FieldInsnNode(Opcodes.GETFIELD,owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
   	    isntick.add(new VarInsnNode(ALOAD,0));
   	    isntick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
   	    AbstractInsnNode spotTickNode = TestTransformer.getFirstInstruction(tick, true, -1);
 	    tick.instructions.insert(spotTickNode,isntick);
	}

	public static void transFormTileEntityCaps(String name, ClassNode classNode, boolean obfuscated) throws IOException {
    	//add interface and implement methods
    	implementICapProvider(classNode,name,"Lnet/minecraft/tileentity/TileEntity;");
    	
    	//Serialization
    	String owner = new MCPSidedString("net/minecraft/tileentity/TileEntity","avj").toString();
    	String desc_nbt = new MCPSidedString("Lnet/minecraft/nbt/NBTTagCompound;","Lfy;").toString();
    	String readDesc = "(Ljava/lang/Object;" + desc_nbt + ")V";
    	
     	String method_readFromNBT = new MCPSidedString("readFromNBT","a").toString();
    	MethodNode readFromNBT = TestTransformer.getMethodNode(classNode, method_readFromNBT, "(" + desc_nbt + ")V");
    	
    	//readFromNBT
    	InsnList toInsert1 = new InsnList();
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new VarInsnNode(ALOAD,1));
    	toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", readDesc, false));
    	AbstractInsnNode spotNode = TestTransformer.getFirstInstruction(readFromNBT, false, Opcodes.ALOAD);
    	readFromNBT.instructions.insertBefore(spotNode,toInsert1);
    	
    	String method_writeToNBT = new MCPSidedString("writeToNBT","f").toString();
        MethodNode writeToNBT = TestTransformer.getMethodNode(classNode,method_writeToNBT, "(" + desc_nbt + ")" + desc_nbt);
		
        //writeToNBT
        InsnList toInsert2 = new InsnList();
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,  owner, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new VarInsnNode(ALOAD,1));
   	    toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", readDesc,false));
   	    AbstractInsnNode spotWriteNode = TestTransformer.getFirstInstruction(writeToNBT, false, Opcodes.ALOAD);
   	    writeToNBT.instructions.insertBefore(spotWriteNode,toInsert2);
   	    
   	    //Constructor cap initiation
   	    MethodNode constructor = TestTransformer.getConstructionNode(classNode,"()V");
   	    InsnList toInsert3 = new InsnList();
   		toInsert3.add(new VarInsnNode(ALOAD,0));
   		toInsert3.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
   		//find insertion point in the constructor to make capabilities register themselves
   		for(AbstractInsnNode obj : constructor.instructions.toArray())
   		{
   			if(obj.getOpcode() == Opcodes.INVOKESTATIC)
   			{
   				MethodInsnNode node = (MethodInsnNode)obj;
   				if(node.owner.equals("net/minecraftforge/event/ForgeEventFactory") && node.name.equals("gatherCapabilities"))
   				{
   					constructor.instructions.insert(obj.getNext(), toInsert3);
   					break;
   				}
   			}
   		}
   	    //tick injection doesn't occur since a world hook is needed to make tiles tick
	}
	/**
	 * add capabilities into the itemstack
	 * @param name
	 * @param classNode
	 * @param obfuscated
	 * @throws IOException
	 */
	public static void transformItemStack(String name, ClassNode classNode, boolean obfuscated) throws IOException
	{
		String desc_nbt = new MCPSidedString("Lnet/minecraft/nbt/NBTTagCompound;","Lfy;").toString();
		
		implementICapProvider(classNode,name,"Lnet/minecraft/item/ItemStack;");
		
		InsnList toInsert1 = new InsnList();
		//inject CapRegUtil.registerCapsToObject(this);
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapRegHandler", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		
		//inject this.capContainer.readFromNBT(this,this.getTagCompound());
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/item/ItemStack", "getTagCompound", "()Lnet/minecraft/nbt/NBTTagCompound;", false));
		toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		//inject all instructions
		MethodNode capNode = TestTransformer.getMethodNode(classNode, "forgeInit", "()V");
		AbstractInsnNode spotNode = TestTransformer.getFirstInstruction(capNode, false, Opcodes.ALOAD);
		capNode.instructions.insertBefore(spotNode, toInsert1);
		
		//writeToNBT inject this.capContainer.writeToNBT(this,nbt);
		InsnList toInsert2 = new InsnList();
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/item/ItemStack", "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;"));
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new VarInsnNode(ALOAD,1));
		toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "writeToNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		MethodNode write = TestTransformer.getMethodNode(classNode, "writeToNBT", "(" + desc_nbt + ")" + desc_nbt);
		AbstractInsnNode spoteWriteNode = TestTransformer.getLineNumberNode(write.instructions);
		write.instructions.insert(spoteWriteNode,toInsert2);
	}
	/**
	 * inject the line to make tile entities caps tick safer then screwing around with other people's classes
	 */
	public static void injectTileTickToWorld(String name, ClassNode classNode, boolean obfuscated)
	{
		MethodNode method = TestTransformer.getMethodNode(classNode, "updateEntities", "()V");
		for(AbstractInsnNode obj : method.instructions.toArray())
		{
			if(obj.getOpcode() == Opcodes.CHECKCAST && obj instanceof TypeInsnNode)
			{
				TypeInsnNode isn = (TypeInsnNode)obj;
				if(isn.desc.equals("net/minecraft/util/ITickable"))
				{
					System.out.println("found injection point for ticking tile caps:");
					AbstractInsnNode point = isn.getPrevious();
					System.out.println((point.getOpcode() == Opcodes.ALOAD) + " clazz:" + point.getClass());

					InsnList toInsert = new InsnList();
					toInsert.add(new VarInsnNode(ALOAD,2));
					toInsert.add(new TypeInsnNode(Opcodes.CHECKCAST,"com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider"));
					toInsert.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider", "getCapContainer", "()Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;", true));
					toInsert.add(new VarInsnNode(ALOAD,2));
					toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
					method.instructions.insertBefore(point,toInsert);
				}
			}
		}
	}
	/**
	 * add world caps that tick attatched to directly the WorldInfo.class
	 */
	public static void transformWorld(ClassNode classNode,String name, boolean obfuscated) throws IOException
	{
		implementICapProvider(classNode,name,"Lnet/minecraft/world/WorldInfo;");
	}

	/**
	 * make a class automatically implement ICapProvider without readFromNBT(),writeToNBT(), or tick() implementations
	 * @param classNode
	 * @param name
	 * @throws IOException
	 */
	public static void implementICapProvider(ClassNode classNode, String name,String classType) throws IOException {
		TestTransformer.addFeild(classNode, "capContainer", "Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;","Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer<" + classType + ">;");
		
    	TestTransformer.addInterface(classNode, "com/EvilNotch/lib/minecraft/content/capabilites/registry/ICapProvider");
    	MethodNode getCap = TestTransformer.addMethod(classNode,"com/EvilNotch/lib/asm/Caps.class", "getCapContainer", "()Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;");
    	TestTransformer.patchLocals(getCap, name);
    	TestTransformer.patchInstructions(getCap, name, "com/EvilNotch/lib/asm/Caps");
    	
    	MethodNode setCap = TestTransformer.addMethod(classNode,"com/EvilNotch/lib/asm/Caps.class", "setCapContainer", "(Lcom/EvilNotch/lib/minecraft/content/capabilites/registry/CapContainer;)V");
    	TestTransformer.patchLocals(setCap, name);
    	TestTransformer.patchInstructions(setCap, name, "com/EvilNotch/lib/asm/Caps");
	}

}
