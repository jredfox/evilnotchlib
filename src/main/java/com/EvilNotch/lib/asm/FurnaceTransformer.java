package com.EvilNotch.lib.asm;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


public class FurnaceTransformer 
{

	public static void transformMethod(ClassNode playerList, boolean obfuscated) 
	{
		//writeToNBT (Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound; avu/b (Lfy;)Lfy;
		 final String method_name = obfuscated ? "b" : "writeToNBT";
		 final String method_desc = obfuscated ? "(Lfy;)Lfy;" : "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;";
		 
		 for (MethodNode method : playerList.methods)
		 {
			 if (method.name.equals(method_name) && method.desc.equals(method_desc))
			 {
				 
				 ArrayList nodes = new ArrayList();
				 for (AbstractInsnNode instruction : method.instructions.toArray())
				 {
					 if(instruction.getPrevious()!=null && instruction.getNext()!=null)
					 {
						 if (instruction.getPrevious().getOpcode() == GETFIELD && instruction.getNext().getOpcode() == INVOKEVIRTUAL)
					 	 {
					    	 nodes.add(instruction);
					 	 }
					 }
				 }
				 if (!nodes.isEmpty())
				 {
					 for(Object l:nodes)
					 {
						 AbstractInsnNode node = (AbstractInsnNode)l;
						 method.instructions.remove(node);
					 }
				 }
				 else
				 {
					 System.out.println("Error Asming Furnace REPORT THIS TO MOD AUTHOR");
				 } 
			 }
		 }
	}
}
