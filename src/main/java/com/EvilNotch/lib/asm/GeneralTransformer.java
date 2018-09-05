package com.EvilNotch.lib.asm;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.EvilNotch.lib.Api.MCPSidedString;

public class GeneralTransformer {
	/**
	 * changes the execution of where the invoking static method takes the middle click to my method
	 * @param classNode
	 */
	public static void transformMC(ClassNode classNode) 
	{
    	MethodNode mnode = TestTransformer.getMethodNode(classNode, new MCPSidedString("middleClickMouse","aH").toString(), "()V");
    	for(AbstractInsnNode node : mnode.instructions.toArray())
    	{
    		if(node.getOpcode() == Opcodes.INVOKESTATIC && node instanceof MethodInsnNode)
    		{
    			MethodInsnNode n = (MethodInsnNode)node;
    			if(n.owner.equals("net/minecraftforge/common/ForgeHooks"))
    			{
    				n.name = "pickBlock";
    				n.owner = "com/EvilNotch/lib/asm/PickBlock";
    				System.out.println("patched Minecraft#middleClickMouse()");
    				break;
    			}
    		}
    	}
	}
	/**
	 * injects line EntityUtil.patchUUID(profile); into the classNode
	 * @param playerList
	 * @param obfuscated
	 */
	public static void injectUUIDPatcher(ClassNode playerList, boolean obfuscated) 
	{
		 final String method_name = obfuscated ? "g" : "createPlayerForUser";
		 final String method_desc = obfuscated ? "(Lcom/mojang/authlib/GameProfile;)Loq;" : "(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/entity/player/EntityPlayerMP;";
		 
		 MethodNode method = TestTransformer.getMethodNode(playerList, method_name, method_desc);
		 InsnList toInsert = new InsnList();
         toInsert.add(new VarInsnNode(ALOAD,1));
         toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/EvilNotch/lib/minecraft/EntityUtil", "patchUUID", "(Lcom/mojang/authlib/GameProfile;)V", false));
         method.instructions.insertBefore(TestTransformer.getFirstInstruction(method, false, Opcodes.ALOAD),toInsert);
	}

}
