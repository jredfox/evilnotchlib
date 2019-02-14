package com.evilnotch.lib.asm.transformer;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.util.ASMHelper;

public class GeneralTransformer {
	
	/**
	 * changes the execution of where the invoking static method takes the middle click to my method
	 */
	public static void transformMC(ClassNode classNode) 
	{
    	MethodNode mnode = ASMHelper.getMethodNode(classNode, new MCPSidedString("middleClickMouse","func_147112_ai").toString(), "()V");
    	for(AbstractInsnNode node : mnode.instructions.toArray())
    	{
    		if(node.getOpcode() == Opcodes.INVOKESTATIC && node instanceof MethodInsnNode)
    		{
    			MethodInsnNode n = (MethodInsnNode)node;
    			if(n.owner.equals("net/minecraftforge/common/ForgeHooks"))
    			{
    				n.name = "pickBlock";
    				n.owner = "com/evilnotch/lib/main/eventhandler/PickBlock";
    				System.out.println("patched Minecraft#middleClickMouse()");
    				break;
    			}
    		}
    	}
	}
	
	/**
	 * injects line EntityUtil.patchUUID(profile); into the classNode
	 */
	public static void injectUUIDPatcher(ClassNode playerList, boolean obfuscated) 
	{
		 final String method_name = obfuscated ? "func_148545_a" : "createPlayerForUser";
		 final String method_desc = "(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/entity/player/EntityPlayerMP;";
		 
		 MethodNode method = ASMHelper.getMethodNode(playerList, method_name, method_desc);
		 InsnList toInsert = new InsnList();
         toInsert.add(new VarInsnNode(ALOAD, 1));
         toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/minecraft/util/PlayerUtil", "patchUUID", "(Lcom/mojang/authlib/GameProfile;)V", false));
         method.instructions.insertBefore(ASMHelper.getFirstInstruction(method, Opcodes.ALOAD),toInsert);
	}
	
	/**
	 * patch seed check
	 */
  /*  public static void patchPlayer(ClassNode classNode) 
    {    
      	//append && PlayerUtil.isPlayerOwner(this) to EntityPlayerMP#canUseCommand if("seed".equals(cmdName) && mc.isdedicatedServer())
      	MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("canUseCommand","func_70003_b").toString(), "(ILjava/lang/String;)Z");
      	AbstractInsnNode start = null;
      	for(AbstractInsnNode ab : node.instructions.toArray())
      	{
      	   if(ab.getOpcode() == Opcodes.INVOKEVIRTUAL && ab instanceof MethodInsnNode)
      	   {
      		  MethodInsnNode m = (MethodInsnNode)ab;
      		  if(m.owner.equals("net/minecraft/server/MinecraftServer") && m.name.equals(new MCPSidedString("isDedicatedServer","func_71262_S").toString()) && m.desc.equals("()Z"))
      		  {
					start = ab.getNext();
					break;
      		  }
      	   }
      	}
      	
      	InsnList insert = new InsnList();
      	insert.add(new VarInsnNode(ALOAD,0));
      	insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/PlayerUtil", "isPlayerOwner", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Z", false));
        insert.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode)start).label));
        node.instructions.insert(start, insert);
	}*/
    
    /**
     * patch the bug when opening to lan where the host cannot open command blocks even though cheats are enabled for him but, not everybody else
     */
	public static void patchOpenToLan(ClassNode classNode) 
	{
		MethodNode method = ASMHelper.getMethodNode(classNode, new MCPSidedString("shareToLAN","func_71206_a").toString(), "(Lnet/minecraft/world/GameType;Z)Ljava/lang/String;");
		AbstractInsnNode spot = null;
		AbstractInsnNode[] arr = method.instructions.toArray();
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode ab = arr[i];
			if(ab.getOpcode() == Opcodes.ICONST_0 && ab.getPrevious() instanceof FrameNode)
			{
				spot = ab;
				break;
			}
		}
		
		InsnList toInsert = new InsnList();
	    toInsert.add(new VarInsnNode(ALOAD, 0));
	   	toInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/integrated/IntegratedServer", new MCPSidedString("mc","field_71349_l").toString(), "Lnet/minecraft/client/Minecraft;"));
	   	toInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", new MCPSidedString("player","field_71439_g").toString(), "Lnet/minecraft/client/entity/EntityPlayerSP;"));
	   	toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", new MCPSidedString("getPermissionLevel","func_184840_I").toString(), "()I", false));
	   	method.instructions.insert(spot, toInsert);
	   
	   	method.instructions.remove(spot);
	}

}
