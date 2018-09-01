package com.EvilNotch.lib.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


public class OtherTransformer 
{
	public static void injectUUIDPatcher(ClassNode playerList, boolean obfuscated) 
	{
		 final String method_name = obfuscated ? "g" : "createPlayerForUser";
		 final String method_desc = obfuscated ? "(Lcom/mojang/authlib/GameProfile;)Loq;" : "(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/entity/player/EntityPlayerMP;";
		 
		 int count = 0;
		 for (MethodNode method : playerList.methods)
		 {
			 if (method.name.equals(method_name) && method.desc.equals(method_desc))
			 {
				 AbstractInsnNode targetNode = null;
				 for (AbstractInsnNode instruction : method.instructions.toArray())
				 {
					 if (instruction instanceof LabelNode)
					 {
						targetNode = instruction;
						System.out.println("found abstractIsnNode for uuidpatcher");
						break;
					 }
				 }
				 if (targetNode != null)
				 {
					 InsnList toInsert = new InsnList();
	                 toInsert.add(new VarInsnNode(ALOAD,1));
	                 toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/EvilNotch/lib/minecraft/EntityUtil", "patchUUID", "(Lcom/mojang/authlib/GameProfile;)V", false));
	                 method.instructions.insert(method.instructions.getFirst(),toInsert);
				 }
				 else
				 {
					 System.out.println("Error Asming UUIDPatcher REPORT THIS TO MOD AUTHOR");
				 }
			 }
		 }
	}

	private static String getNodeString(LocalVariableNode node) {
		return node.name + " desc:" + node.desc + " index:"+ node.index + " signature" + node.signature;
	}
}
