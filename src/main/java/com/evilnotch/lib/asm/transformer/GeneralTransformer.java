package com.evilnotch.lib.asm.transformer;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.io.IOException;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.util.ASMHelper;

public class GeneralTransformer {
	
	/**
	 * changes the execution of where the invoking static method takes the middle click to my method
	 */
	public static void transformMC(ClassNode classNode) 
	{
		if(ConfigCore.asm_middleClickEvent)
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
    	
    	//make profileProperties public minus final
    	String props = new MCPSidedString("profileProperties", "field_181038_N").toString();
    	String getprops = new MCPSidedString("getProfileProperties", "func_181037_M").toString();
    	for(FieldNode f : classNode.fields)
    	{
    		if(f.name.equals(props))
    		{
    			f.access = Opcodes.ACC_PUBLIC;
    			break;
    		}
    	}
    	
    	if(!ConfigCore.asm_patchLanSkins)
    		return;
    	
    	//VanillaBugFixes.fixMcProfileProperties();
    	MethodNode m = ASMHelper.getMethodNode(classNode, getprops, "()Lcom/mojang/authlib/properties/PropertyMap;");
    	m.instructions.insert(ASMHelper.getFirstInstruction(m), new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/main/eventhandler/VanillaBugFixes", "fixMcProfileProperties", "()V", false));
    	
    	//append && !Config.skinCache if found
    	JumpInsnNode jumpP = null;
    	try
    	{
    		jumpP = ASMHelper.nextJumpInsnNode(ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "com/mojang/authlib/properties/PropertyMap", "isEmpty", "()Z", false));
	    	InsnList plist = new InsnList();
	    	plist.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/evilnotch/lib/main/Config", "skinCache", "Z"));
	    	plist.add(new JumpInsnNode(Opcodes.IFNE, jumpP.label));
	    	m.instructions.insert(jumpP, plist);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	//change fillProfileProperties(profile, false) to fillProfileProperties(profile, true);
    	if(ConfigCore.asm_patchLanSkinsInsecure)
    	{
	    	AbstractInsnNode a = jumpP != null ? jumpP : m.instructions.getFirst();
	    	MethodInsnNode insn = new MethodInsnNode(Opcodes.INVOKEINTERFACE, "com/mojang/authlib/minecraft/MinecraftSessionService", "fillProfileProperties", "(Lcom/mojang/authlib/GameProfile;Z)Lcom/mojang/authlib/GameProfile;", true);
	    	while(a != null)
	    	{
	    		if(a instanceof MethodInsnNode && ASMHelper.equals(insn, (MethodInsnNode) a) && a.getPrevious() instanceof InsnNode)
	    		{
	    			InsnNode prev = (InsnNode) a.getPrevious();
	    			if(prev.getOpcode() == Opcodes.ICONST_0)
	    			{
	    				m.instructions.insert(prev, new InsnNode(Opcodes.ICONST_1));
	    				m.instructions.remove(prev);
	    				break;
	    			}
	    		}
	    		a = a.getNext();
	    	}
    	}

    	MethodNode method = ASMHelper.getMethodNode(classNode, new MCPSidedString("launchIntegratedServer", "func_71371_a").toString(), "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V");
    	
    	//this.getProfileProperties();
    	InsnList li = new InsnList();
    	li.add(new VarInsnNode(Opcodes.ALOAD, 0));
    	li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", getprops, "()Lcom/mojang/authlib/properties/PropertyMap;", false));
    	li.add(new InsnNode(Opcodes.POP));
    	method.instructions.insert(ASMHelper.getFirstInstruction(method), li);
    	
    	try
    	{
    		//append && JavaUtil.returnFalse() to if statement
	    	AbstractInsnNode pretarg = ASMHelper.getMethodInsnNode(method, Opcodes.INVOKEVIRTUAL, "net/minecraft/util/Session", "hasCachedProperties", "()Z", false);
	    	JumpInsnNode jump = ASMHelper.nextJumpInsnNode(pretarg);
	    	
	    	InsnList list = new InsnList();
	    	list.add(new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/util/JavaUtil", "returnFalse", "()Z", false));
	    	list.add(new JumpInsnNode(Opcodes.IFEQ, jump.label));
	    	method.instructions.insert(jump, list);
    	}
    	catch(Throwable t)
    	{
    		System.err.println("Error Unable to append JavaUtil#returnFalse to prevent mojang 429 error for skins. Please report to EvilNotchLib's github");
    		t.printStackTrace();
    	}
	}
	
	/**
	 * injects line UUIDPatcher V2 into the classNode
	 */
	public static void injectUUIDPatcher(ClassNode classNode) 
	{
		 MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("createPlayerForUser", "func_148545_a").toString(), "(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/entity/player/EntityPlayerMP;");
		 
		 //profile = UUIDPatcher.patchCheck(profile);
		 InsnList li = new InsnList();
		 li.add(new VarInsnNode(ALOAD, 1));
		 li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "patchCheck", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;", false));
		 li.add(new VarInsnNode(Opcodes.ASTORE, 1));
		 node.instructions.insert(ASMHelper.getFirstInstruction(node), li);
		 
		 //Dissallow Reading of Level.DAT when Player is Owner and the GameRule is false
		 MethodNode m = ASMHelper.getMethodNode(classNode, "getPlayerNBT", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;");
		 VarInsnNode spot = ASMHelper.getVarInsnNode(m, new VarInsnNode(Opcodes.ASTORE, 2));
		 
		 //nbttagcompound = UUIDPatcher.getLevelDat(this.mcServer.worlds[0], nbttagcompound);
		 InsnList l = new InsnList();
		 l.add(new VarInsnNode(ALOAD, 0));
		 l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/management/PlayerList", new MCPSidedString("mcServer", "field_72400_f").toString(), "Lnet/minecraft/server/MinecraftServer;"));
		 l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/MinecraftServer", new MCPSidedString("worlds", "field_71305_c").toString(), "[Lnet/minecraft/world/WorldServer;"));
		 l.add(new InsnNode(Opcodes.ICONST_0));
		 l.add(new InsnNode(Opcodes.AALOAD));
		 l.add(new VarInsnNode(ALOAD, 2));
		 l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "getLevelDat", "(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", false));
		 l.add(new VarInsnNode(Opcodes.ASTORE, 2));
		 
		 m.instructions.insert(spot, l);
		 
		 
		 //if(UUIDPatcher.hasLogin(playerIn)) return UUIDPatcher.getLogin(playerIn);
		 InsnList clist = new InsnList();
		 clist.add(new VarInsnNode(ALOAD, 1));
		 clist.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "hasLogin", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Z", false));
		 LabelNode clabel = new LabelNode();
		 clist.add(new JumpInsnNode(Opcodes.IFEQ, clabel));
		 LabelNode clabel2 = new LabelNode();
		 clist.add(clabel2);
		 clist.add(new VarInsnNode(ALOAD, 1));
		 clist.add(new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "getLogin", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", false));
		 clist.add(new InsnNode(Opcodes.ARETURN));
		 clist.add(clabel);
		 m.instructions.insert(ASMHelper.getFirstInstruction(m), clist);
		 
		 //nbttagcompound = UUIDPatcher.getLevelDat(this.mcServer.worlds[0], nbttagcompound);
		 MethodNode method = ASMHelper.getMethodNode(classNode, new MCPSidedString("readPlayerDataFromFile", "func_72380_a").toString(), "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;");
		 InsnList list = new InsnList();
		 list.add(new VarInsnNode(ALOAD, 0));
		 list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/management/PlayerList", new MCPSidedString("mcServer", "field_72400_f").toString(), "Lnet/minecraft/server/MinecraftServer;"));
		 list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/MinecraftServer", new MCPSidedString("worlds", "field_71305_c").toString(), "[Lnet/minecraft/world/WorldServer;"));
		 list.add(new InsnNode(Opcodes.ICONST_0));
		 list.add(new InsnNode(Opcodes.AALOAD));
		 list.add(new VarInsnNode(ALOAD, 2));
		 list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "getLevelDat", "(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", false));
		 list.add(new VarInsnNode(Opcodes.ASTORE, 2));
		 
		 VarInsnNode spot2 = ASMHelper.getVarInsnNode(method, new VarInsnNode(Opcodes.ASTORE, 2));
		 method.instructions.insert(spot2, list);
		 
		 //if(UUIDPatcher.hasLogin(playerIn)) return UUIDPatcher.fireLogin(this, playerIn);
		 InsnList rlist = new InsnList();
		 rlist.add(new VarInsnNode(ALOAD, 1));
		 rlist.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "hasLogin", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Z", false));
		 LabelNode r1 = new LabelNode();
		 rlist.add(new JumpInsnNode(Opcodes.IFEQ, r1));
		 LabelNode r2 = new LabelNode();
		 rlist.add(r2);
		 rlist.add(new VarInsnNode(ALOAD, 0));
		 rlist.add(new VarInsnNode(ALOAD, 1));
		 rlist.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "fireLogin", "(Lnet/minecraft/server/management/PlayerList;Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", false));
		 rlist.add(new InsnNode(Opcodes.ARETURN));
		 rlist.add(r1);
		 
		 method.instructions.insert(ASMHelper.getFirstInstruction(method), rlist);
	}
    
    /**
     * patch the bug when opening to lan where the host cannot open command blocks even though cheats are enabled for him but, not everybody else
     */
	public static void patchOpenToLan(ClassNode classNode) 
	{
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("shareToLAN", "func_71206_a").toString(), "(Lnet/minecraft/world/GameType;Z)Ljava/lang/String;");
		AbstractInsnNode targ = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", new MCPSidedString("setPermissionLevel", "func_184839_n").toString(), "(I)V", false);
		
		//If target line doesn't exist inject it before the first return
		if(targ == null)
			targ = ASMHelper.prevLabelNode(ASMHelper.getFirstInstruction(m, Opcodes.ARETURN));
		
		//ClientProxy.cachePlayerPermission();
		InsnList l = new InsnList();
		l.add(new LabelNode());
		l.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/proxy/ClientProxy", "cachePlayerPermission", "()V", false));
		m.instructions.insert(l);
		
		//ClientProxy.fixPermissionLevel(allowCheats);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/proxy/ClientProxy", "fixPermissionLevel", "(Z)V", false));
		m.instructions.insert(targ, li);
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
		//public String skindata;
		ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "skindata", "Ljava/lang/String;", null, null));
		//public NBTTagCompound evlNBT;
		ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "evlNBT", "Lnet/minecraft/nbt/NBTTagCompound;", null, null));
		
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("tryAcceptPlayer", "func_147326_c").toString(), "()V");
		String loginGameProfile = new MCPSidedString("loginGameProfile", "field_147337_i").toString();
		
		InsnList list = new InsnList();
		
		//SkinCache.setSkin(this.loginGameProfile.getProperties(), this.skindata);
		list.add(new VarInsnNode(ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/network/NetHandlerLoginServer", loginGameProfile, "Lcom/mojang/authlib/GameProfile;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getProperties", "()Lcom/mojang/authlib/properties/PropertyMap;", false));
		list.add(new VarInsnNode(ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/network/NetHandlerLoginServer", "skindata", "Ljava/lang/String;"));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/main/skin/SkinCache", "setSkin", "(Lcom/mojang/authlib/properties/PropertyMap;Ljava/lang/String;)V", false));
		
		//this.loginGameProfile = UUIDPatcher.patch(gameprofile)
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/network/NetHandlerLoginServer", loginGameProfile, "Lcom/mojang/authlib/GameProfile;"));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "patch", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;", false));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/server/network/NetHandlerLoginServer", loginGameProfile, "Lcom/mojang/authlib/GameProfile;"));
	
		//UUIDPatcher.setLoginHooks(this.loginGameProfile, this.evlNBT);
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/network/NetHandlerLoginServer", loginGameProfile, "Lcom/mojang/authlib/GameProfile;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/network/NetHandlerLoginServer", "evlNBT", "Lnet/minecraft/nbt/NBTTagCompound;"));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "setLoginHooks", "(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
				
		AbstractInsnNode spot = ASMHelper.getTypeInsnNode(m, new TypeInsnNode(Opcodes.NEW, "net/minecraft/network/login/server/SPacketLoginSuccess")).getPrevious().getPrevious();
		m.instructions.insertBefore(spot, list);
		
		//this.skindata = packetIn.skindata;
		MethodNode login = ASMHelper.getMethodNode(classNode, new MCPSidedString("processLoginStart", "func_147316_a").toString(), "(Lnet/minecraft/network/login/client/CPacketLoginStart;)V");
		InsnList l = new InsnList();
		l.add(new LabelNode());
		l.add(new VarInsnNode(Opcodes.ALOAD, 0));
		l.add(new VarInsnNode(Opcodes.ALOAD, 1));
		l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "skindata", "Ljava/lang/String;"));
		l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/server/network/NetHandlerLoginServer", "skindata", "Ljava/lang/String;"));
		
		//this.evlNBT = packetIn.evlNBT;
		l.add(new VarInsnNode(Opcodes.ALOAD, 0));
		l.add(new VarInsnNode(Opcodes.ALOAD, 1));
		l.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/login/client/CPacketLoginStart", "evlNBT", "Lnet/minecraft/nbt/NBTTagCompound;"));
		l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/server/network/NetHandlerLoginServer", "evlNBT", "Lnet/minecraft/nbt/NBTTagCompound;"));
		
		login.instructions.insert(ASMHelper.getFieldNode(login, Opcodes.PUTFIELD, "net/minecraft/server/network/NetHandlerLoginServer", loginGameProfile, "Lcom/mojang/authlib/GameProfile;"), l);
	}

	public static void patchLoginNBT(ClassNode classNode) 
	{	
		MethodNode m = ASMHelper.getMethodNode(classNode, "serverInitiateHandshake", "()I");
		
		InsnList li = new InsnList();
		li.add(new VarInsnNode(ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/fml/common/network/handshake/NetworkDispatcher", "player", "Lnet/minecraft/entity/player/EntityPlayerMP;"));
		li.add(new VarInsnNode(ALOAD, 1));
		li.add(new MethodInsnNode(INVOKESTATIC, "com/evilnotch/lib/minecraft/util/UUIDPatcher", "setLogin", "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		
		AbstractInsnNode spot = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "net/minecraft/server/management/PlayerList", "getPlayerNBT", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Lnet/minecraft/nbt/NBTTagCompound;", false).getNext();
		m.instructions.insert(spot, li);
	}

	public static void patchUnloadDim(ClassNode classNode)
	{
		//AT the class
		ASMHelper.pubMinusFinal(classNode, true);
		
		//public static IntSet keepLoaded = IntSets.synchronize(new IntOpenHashSet());
		if(!ASMHelper.hasFieldNode(classNode, "keepLoaded"))
		{
			classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "keepLoaded", "Lit/unimi/dsi/fastutil/ints/IntSet;", null, null));
			MethodNode clinit = ASMHelper.getMethodNode(classNode, "<clinit>", "()V");
			InsnList list = new InsnList();
			list.add(new TypeInsnNode(Opcodes.NEW, "it/unimi/dsi/fastutil/ints/IntOpenHashSet"));
			list.add(new InsnNode(Opcodes.DUP));
			list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "it/unimi/dsi/fastutil/ints/IntOpenHashSet", "<init>", "()V", false));
			list.add(new MethodInsnNode(INVOKESTATIC, "it/unimi/dsi/fastutil/ints/IntSets", "synchronize", "(Lit/unimi/dsi/fastutil/ints/IntSet;)Lit/unimi/dsi/fastutil/ints/IntSet;", false));
			list.add(new FieldInsnNode(Opcodes.PUTSTATIC, "net/minecraftforge/common/DimensionManager", "keepLoaded", "Lit/unimi/dsi/fastutil/ints/IntSet;"));
			AbstractInsnNode spot = ASMHelper.getLastInstruction(clinit);
			clinit.instructions.insert(spot, list);
		}
	}

	/**
	 * patches fullscreen for versions of forge that hasn't patched in in 1.12.2
	 * Doesn't Fix all FullScreen Issues Use DPI-Fix Mod for this purpose. This only Fixes the Critical Maximize Button After FullScreen
	 */
	public static void patchFullScreen(ClassNode classNode) 
	{
		if(!ConfigCore.asm_FSFix)
			return;
		else if(GeneralTransformer.class.getClassLoader().getResource("jredfox/DpiFix.class") != null)
		{
			System.out.println("FullScreen Already Patched!");
			return;
		}
		
		/*
		 * Fixes MC-68754
		 * if(!this.fullscreen)
		 * {
		 * 		if(isWindows)
		 * 			Display.setResizable(false);
		 * 		Display.setResizable(true);
		 * }
		 */
		MethodNode m = ASMHelper.getMethodNode(classNode, new MCPSidedString("toggleFullscreen", "func_71352_k").toString(), "()V");
			
		//Disable all instances of Forge's / Optifine's Fullscreen "Fix"
		MethodInsnNode startResize = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false);
		if(startResize != null)
		{
			System.err.println("Disabling Forge's \"FIX\" for Fullscreen!");
			MethodInsnNode resizeInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false);
			AbstractInsnNode ab = startResize;
			while(ab != null)
			{
				if(ab instanceof MethodInsnNode && ASMHelper.equals(resizeInsn, (MethodInsnNode) ab))
				{
					MethodInsnNode minsn = (MethodInsnNode)ab;
					minsn.owner = "com/evilnotch/lib/asm/util/ASMHelper";
				}
				ab = ab.getNext();
			}
		}
			
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", new MCPSidedString("fullscreen", "field_71431_Q").toString(), "Z"));
		LabelNode l26 = new LabelNode();
		li.add(new JumpInsnNode(Opcodes.IFNE, l26));
		if(System.getProperty("os.name").toLowerCase().startsWith("windows"))
		{
			LabelNode l27 = new LabelNode();
			li.add(l27);
			li.add(new InsnNode(Opcodes.ICONST_0));
			li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false));
		}
		LabelNode l28 = new LabelNode();
		li.add(l28);
		li.add(new InsnNode(Opcodes.ICONST_1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setResizable", "(Z)V", false));
		li.add(l26);
		
		m.instructions.insert(ASMHelper.getMethodInsnNode(m, Opcodes.INVOKESTATIC, "org/lwjgl/opengl/Display", "setFullscreen", "(Z)V", false), li);
	}
	
}