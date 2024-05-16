package com.evilnotch.lib.asm.transformer;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.io.IOException;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.minecraft.event.client.MessageEvent;
import com.evilnotch.lib.minecraft.event.client.SkinTransparencyEvent;
import com.evilnotch.lib.minecraft.util.EntityUtil;

import net.minecraftforge.common.MinecraftForge;

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
         method.instructions.insertBefore(ASMHelper.getFirstInstruction(method, Opcodes.ALOAD), toInsert);
	}
    
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
				List<Object> objs = ((FrameNode)ab.getPrevious()).stack;
				if(objs != null && objs.contains("net/minecraft/client/entity/EntityPlayerSP"))
				{
					spot = ab;
					break;
				}
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
	
	/**
	 * adds a method call fixNBT() via constructor and adds the method
	 * @throws IOException 
	 */
	public static void patchWeightedSpawner(ClassNode classNode, String inputBase, String className) throws IOException 
	{
		MethodNode fixId = ASMHelper.addMethod(classNode, inputBase + "Methods", "fixIdLib", "(Lnet/minecraft/nbt/NBTTagCompound;)V");
		ASMHelper.patchMethod(fixId, className, "com/evilnotch/lib/asm/gen/Methods");
		
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(ILnet/minecraft/nbt/NBTTagCompound;)V");
		AbstractInsnNode point = ASMHelper.getLastPutField(construct);
		
		InsnList list = new InsnList();
		list.add(new VarInsnNode(ALOAD, 0));
		list.add(new VarInsnNode(ALOAD, 2));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/WeightedSpawnerEntity", "fixIdLib", "(Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		construct.instructions.insert(point, list);
	}
	

	public static void patchWorldClient(ClassNode classNode)
	{
		//add if(MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(entityIn, this)) return;
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("spawnEntity", "func_72838_d").toString(), "(Lnet/minecraft/entity/Entity;)Z");
		AbstractInsnNode spot = ASMHelper.getFirstInstruction(node, Opcodes.ISTORE);
		InsnList list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
		list.add(new TypeInsnNode(Opcodes.NEW, "net/minecraftforge/event/entity/EntityJoinWorldEvent"));
		list.add(new InsnNode(Opcodes.DUP));
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraftforge/event/entity/EntityJoinWorldEvent", "<init>", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/World;)V", false));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
		LabelNode l2 = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFEQ, l2));
		LabelNode l3 = new LabelNode();
		list.add(l3);
		list.add(new InsnNode(Opcodes.ICONST_0));
		list.add(new InsnNode(Opcodes.IRETURN));
		list.add(l2);
		node.instructions.insert(spot, list);
	}
	
	public static void patchWorld(ClassNode classNode)
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("spawnEntity","func_72838_d").toString(), "(Lnet/minecraft/entity/Entity;)Z");
		AbstractInsnNode spot = null;
		MethodInsnNode check = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
		for(AbstractInsnNode ab : node.instructions.toArray())
		{
			if(ab instanceof MethodInsnNode && ASMHelper.equals(check, (MethodInsnNode)ab))
			{
				spot = ab.getNext().getNext();
			}
		}
		
		if(spot.getOpcode() == Opcodes.ILOAD)
		{
			node.instructions.remove(spot.getNext());
			node.instructions.remove(spot);
		}
		
		//capaiblity hooks for whether or not the entity is added into the world
		MethodNode node2 = ASMHelper.getMethodNode(classNode, new MCPSidedString("onEntityAdded", "func_72923_a").toString(), "(Lnet/minecraft/entity/Entity;)V");
		InsnList list3 = new InsnList();
		list3.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list3.add(new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/minecraft/util/EntityUtil", "patchEntityAdded", "(Lnet/minecraft/entity/Entity;)V", false));
		node2.instructions.insertBefore(ASMHelper.getFirstInstruction(node2, Opcodes.RETURN), list3);
		
		//capaiblity hooks for whether or not the entity is added into the world
		MethodNode node3 = ASMHelper.getMethodNode(classNode, new MCPSidedString("onEntityRemoved", "func_72847_b").toString(), "(Lnet/minecraft/entity/Entity;)V");
		InsnList list4 = new InsnList();
		list4.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list4.add(new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/minecraft/util/EntityUtil", "patchEntityRemoved", "(Lnet/minecraft/entity/Entity;)V", false));
		node3.instructions.insertBefore(ASMHelper.getFirstInstruction(node3, Opcodes.RETURN), list4);
		
		//add if(!EntityUtil#patchEntityUpdate(entityIn)) return;
		MethodNode update = ASMHelper.getMethodNode(classNode, new MCPSidedString("updateEntityWithOptionalForce", "func_72866_a").toString(), "(Lnet/minecraft/entity/Entity;Z)V");
		LabelNode l1 = new LabelNode();
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/EntityUtil", "patchEntityUpdate", "(Lnet/minecraft/entity/Entity;)Z", false));
		toInsert.add(new JumpInsnNode(Opcodes.IFNE, l1));
		LabelNode l15 = new LabelNode();
		toInsert.add(l15);
		toInsert.add(new InsnNode(Opcodes.RETURN));
		toInsert.add(l1);
		update.instructions.insert(ASMHelper.getFirstInstruction(update, Opcodes.RETURN).getNext().getNext(), toInsert);
	}
	
	/**
	 * patch the cull being disabled
	 */
	public static void patchRenderManager(ClassNode classNode)
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("renderEntity", "func_188391_a").toString(), "(Lnet/minecraft/entity/Entity;DDDFFZ)V");
		AbstractInsnNode spot = null;
		
		MethodInsnNode check = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/entity/Render", new MCPSidedString("doRender", "func_76986_a").toString(), "(Lnet/minecraft/entity/Entity;DDDFF)V", false);
		for(AbstractInsnNode ab : node.instructions.toArray())
		{
			if(ab instanceof MethodInsnNode && ASMHelper.equals(check, (MethodInsnNode)ab))
			{
				AbstractInsnNode compare = ab;
				while(compare != null)
				{
					compare = compare.getPrevious();
					if(compare instanceof LineNumberNode)
					{
						spot = compare;
						break;
					}
				}
				break;
			}
		}
		
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/entity/item/EntityItem"));
		LabelNode l15 = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFEQ, l15));
		LabelNode l16 = new LabelNode();
		list.add(l16);
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/renderer/GlStateManager", new MCPSidedString("enableCull", "func_179089_o").toString(), "()V", false));
		list.add(l15);
		node.instructions.insert(spot, list);
	}

	public static void transformChat(ClassNode classNode) 
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("printChatMessageWithOptionalDeletion", "func_146234_a").toString(), "(Lnet/minecraft/util/text/ITextComponent;I)V");
		InsnList list = new InsnList();
		
		//if(!MinecraftForge.EVENT_BUS.post(new MessageEvent.Print(chatComponent, chatLineId))) return;
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
		list.add(new TypeInsnNode(Opcodes.NEW, "com/evilnotch/lib/minecraft/event/client/MessageEvent$Print"));
		list.add(new InsnNode(Opcodes.DUP));
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new VarInsnNode(Opcodes.ILOAD, 2));
		list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/evilnotch/lib/minecraft/event/client/MessageEvent$Print", "<init>", "(Lnet/minecraft/util/text/ITextComponent;I)V", false));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
		LabelNode l1 = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFEQ, l1));
		LabelNode l2 = new LabelNode();
		list.add(l2);
		list.add(new InsnNode(Opcodes.RETURN));
		list.add(l1);
		
		AbstractInsnNode spot = ASMHelper.getFirstInstruction(node);
		node.instructions.insert(spot, list);
		
		//if(MinecraftForge.EVENT_BUS.post(new MessageEvent.Add(message))) return
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("addToSentMessages", "func_146239_a").toString(), "(Ljava/lang/String;)V");
		InsnList l = new InsnList();
		l.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
		l.add(new TypeInsnNode(Opcodes.NEW, "com/evilnotch/lib/minecraft/event/client/MessageEvent$Add"));
		l.add(new InsnNode(Opcodes.DUP));
		l.add(new VarInsnNode(Opcodes.ALOAD, 1));
		l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/evilnotch/lib/minecraft/event/client/MessageEvent$Add", "<init>", "(Ljava/lang/String;)V", false));
		l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
		LabelNode label1 = new LabelNode();
		l.add(new JumpInsnNode(Opcodes.IFEQ, label1));
		LabelNode label2 = new LabelNode();
		l.add(label2);
		l.add(new InsnNode(Opcodes.RETURN));
		l.add(label1);
		
		AbstractInsnNode spot2 = ASMHelper.getFirstInstruction(m);
		m.instructions.insert(spot2, l);
	}
	
	public static void transformChatOverlay(ClassNode classNode) 
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("setOverlayMessage", "func_110326_a").toString(), "(Ljava/lang/String;Z)V");
		InsnList li = new InsnList();
		
		// if(MinecraftForge.EVENT_BUS.post(new MessageEvent.Overlay(message, animateColor))) return;
		li.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
		li.add(new TypeInsnNode(Opcodes.NEW, "com/evilnotch/lib/minecraft/event/client/MessageEvent$Overlay"));
		li.add(new InsnNode(Opcodes.DUP));
		li.add(new VarInsnNode(ALOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/evilnotch/lib/minecraft/event/client/MessageEvent$Overlay", "<init>", "(Ljava/lang/String;Z)V", false));
		li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
		LabelNode l1 = new LabelNode();
		li.add(new JumpInsnNode(Opcodes.IFEQ, l1));
		LabelNode l2 = new LabelNode();
		li.add(l2);
		li.add(new InsnNode(Opcodes.RETURN));
		li.add(l1);
		AbstractInsnNode spot = ASMHelper.getFirstInstruction(node);
		node.instructions.insert(spot, li);
		
		//if(MinecraftForge.EVENT_BUS.post(new MessageEvent.Say(chatTypeIn, message))) return;
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("addChatMessage", "func_191742_a").toString(), "(Lnet/minecraft/util/text/ChatType;Lnet/minecraft/util/text/ITextComponent;)V");
		InsnList list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
		list.add(new TypeInsnNode(Opcodes.NEW, "com/evilnotch/lib/minecraft/event/client/MessageEvent$Say"));
		list.add(new InsnNode(Opcodes.DUP));
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new VarInsnNode(Opcodes.ALOAD, 2));
		list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/evilnotch/lib/minecraft/event/client/MessageEvent$Say", "<init>", "(Lnet/minecraft/util/text/ChatType;Lnet/minecraft/util/text/ITextComponent;)V", false));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
		LabelNode label1 = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFEQ, label1));
		LabelNode label2 = new LabelNode();
		list.add(label2);
		list.add(new InsnNode(Opcodes.RETURN));
		list.add(label1);
		m.instructions.insert(ASMHelper.getFirstInstruction(m), list);
		
	}

	public static void transformSkinTrans(ClassNode classNode)
	{
		//add the allowtrans field to the class
		ASMHelper.addFeild(classNode, "allowtrans", "Z");
		
		//this.allowtrans = SkinTransparencyEvent.allow(this.imageData);
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("parseUserSkin", "func_78432_a").toString(), "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;");
		AbstractInsnNode spot = ASMHelper.getFieldNode(node, Opcodes.PUTFIELD, "net/minecraft/client/renderer/ImageBufferDownload", new MCPSidedString("imageData", "field_78438_a").toString(), "[I");
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
		toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
		toInsert.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ImageBufferDownload", new MCPSidedString("imageData", "field_78438_a").toString(), "[I"));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/event/client/SkinTransparencyEvent", "allow", "([I)Z", false));
		toInsert.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/ImageBufferDownload", "allowtrans", "Z"));
		node.instructions.insert(spot, toInsert);
		
		//if(this.allowtrans) return;
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("setAreaOpaque", "func_78433_b").toString(), "(IIII)V");
		InsnList li = new InsnList();
		li.add(new VarInsnNode(ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/ImageBufferDownload", "allowtrans", "Z"));
		LabelNode l1 = new LabelNode();
		li.add(new JumpInsnNode(Opcodes.IFEQ, l1));
		LabelNode l2 = new LabelNode();
		li.add(l2);
		li.add(new InsnNode(Opcodes.RETURN));
		li.add(l1);
		m.instructions.insert(ASMHelper.getFirstInstruction(m), li);
	}

	public static void patchUUID(ClassNode classNode)
	{
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("tryAcceptPlayer", "func_147326_c").toString(), "()V");
		String loginGameProfile = new MCPSidedString("loginGameProfile", "field_147337_i").toString();
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/network/NetHandlerLoginServer", loginGameProfile, "Lcom/mojang/authlib/GameProfile;"));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "patch", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;", false));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/server/network/NetHandlerLoginServer", loginGameProfile, "Lcom/mojang/authlib/GameProfile;"));
	
		AbstractInsnNode spot = ASMHelper.getTypeInsnNode(m, new TypeInsnNode(Opcodes.NEW, "net/minecraft/network/login/server/SPacketLoginSuccess")).getPrevious().getPrevious();
		m.instructions.insertBefore(spot, list);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
