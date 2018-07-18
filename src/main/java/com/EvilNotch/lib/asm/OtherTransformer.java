package com.EvilNotch.lib.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


public class OtherTransformer 
{
	public static void injectUUIDPatcher(ClassNode playerList, boolean obfuscated) 
	{
		 final String method_name = obfuscated ? "g" : "createPlayerForUser";
		 final String method_desc = obfuscated ? "(Lcom/mojang/authlib/GameProfile;)Loq;" : "(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/entity/player/EntityPlayerMP;";
		 
		 for (MethodNode method : playerList.methods)
		 {
			 if (method.name.equals(method_name) && method.desc.equals(method_desc))
			 {
				 AbstractInsnNode targetNode = null;
				 for (AbstractInsnNode instruction : method.instructions.toArray())
				 {
					 if (instruction.getOpcode() == ALOAD)
					 {
						 if (((VarInsnNode)instruction).var==1 && instruction.getNext().getOpcode() == INVOKESTATIC)
						 {
							 targetNode = instruction;
							 break;
						 }
					 }
				 }
				 if (targetNode != null)
				 {
					 
					 InsnList toInsert = new InsnList();
	                 toInsert.add(new VarInsnNode(ALOAD,1));
	                 toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/EvilNotch/lib/minecraft/EntityUtil", "patchUUID", "(Lcom/mojang/authlib/GameProfile;)V", false));
	               
	                 method.instructions.insertBefore(targetNode, toInsert);
				 }
				 else
				 {
					 System.out.println("Error Asming UUIDPatcher REPORT THIS TO MOD AUTHOR");
				 }
	
			 }
		 }
	}
}
