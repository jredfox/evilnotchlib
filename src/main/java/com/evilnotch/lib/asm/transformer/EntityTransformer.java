package com.evilnotch.lib.asm.transformer;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.ConfigCore;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.classwriter.MCWriter;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.server.management.PlayerList;

public class EntityTransformer implements IClassTransformer{
	
    public static final List<String> clazzes = (List<String>)JavaUtil.<String>asArray(new Object[]
    {
    	"net.minecraft.entity.EntityHanging",
    	"net.minecraft.entity.item.EntityFallingBlock",
    	"net.minecraft.entity.item.EntityPainting",
    	"net.minecraft.entity.player.EntityPlayerMP",
    	"net.minecraft.entity.monster.EntityZombie",
    	"net.minecraft.entity.monster.EntityShulker",
    	"net.minecraft.client.entity.EntityPlayerSP"
    });

	@Override
	public byte[] transform(String name, String transformedName, byte[] classToTransform) 
	{
		 int index = clazzes.indexOf(transformedName);
	     return index != -1 ? transform(index, classToTransform, FMLCorePlugin.isObf) : classToTransform;
	}

	private byte[] transform(int index, byte[] classToTransform, boolean isObf) 
	{
		String name = clazzes.get(index);
		
		if(!ConfigCore.asm_entityPatch && !name.equals("net.minecraft.entity.player.EntityPlayerMP"))
		{
			return classToTransform;
		}

    	System.out.println("Transforming: " + name + " index:" + index);
    	
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classToTransform);
            classReader.accept(classNode, 0);
            
            switch(index)
            {
                case 0:
                	transformEntityHanging(classNode);
                break;
                
                case 1:
                	transformEntityFalling(classNode);
                break;
                
                case 2:
                	transformEntityPainting(classNode);
                break;
                
                case 3:
                	patchPlayer(classNode);
                break;
                
                case 4:
                	patchZombie(classNode);
                break;
                
                case 5:
                	patchShulker(classNode);
                break;
                
                case 6:
                	patchPlayerClient(classNode);
                break;
            }
            
            ClassWriter classWriter = new MCWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
            byte[] bytes = classWriter.toByteArray();
            if(ConfigCore.dumpASM)
            {
            	ASMHelper.dumpFile(name, bytes);
            }
            return bytes;
        }
        catch(Throwable t)
        {
        	t.printStackTrace();
        }
		return null;
	}

	public static void patchPlayerClient(ClassNode classNode) 
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("sendStatusMessage", "func_146105_b").toString(), "(Lnet/minecraft/util/text/ITextComponent;Z)V");
		InsnList list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
		list.add(new TypeInsnNode(Opcodes.NEW, "com/evilnotch/lib/minecraft/event/MessageEvent$PlayerStatusEvent"));
		list.add(new InsnNode(Opcodes.DUP));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ILOAD, 2));
		list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/evilnotch/lib/minecraft/event/MessageEvent$PlayerStatusEvent", "<init>", "(Lnet/minecraft/entity/Entity;Z)V", false));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
		LabelNode l1 = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFEQ, l1));
		LabelNode l2 = new LabelNode();
		list.add(l2);
		list.add(new InsnNode(Opcodes.RETURN));
		list.add(l1);
		node.instructions.insert(ASMHelper.getFirstInstruction(node), list);
		
		MethodNode node2 = ASMHelper.getMethodNode(classNode, new MCPSidedString("sendMessage","func_145747_a").toString(), "(Lnet/minecraft/util/text/ITextComponent;)V");
		node2.instructions.insert(ASMHelper.getFirstInstruction(node2), list);
	}

	public static void patchShulker(ClassNode classNode) 
	{
		//insert EntityUtil#patchShulker into the constructor
		MethodNode node = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/EntityUtil", "patchShulker", "(Lnet/minecraft/entity/monster/EntityShulker;)V", false));
		node.instructions.insert(ASMHelper.getLastPutField(node), list);
		
		//append && EntityUtil.addedToWorld(this) to see if it can teleport or not
		MethodNode pos = ASMHelper.getMethodNode(classNode, new MCPSidedString("setPosition", "func_70107_b").toString(), "(DDD)V");
		JumpInsnNode spot = null;
		FieldInsnNode check = new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/monster/EntityShulker", new MCPSidedString("ticksExisted","field_70173_aa").toString(), "I");
		for(AbstractInsnNode ab : pos.instructions.toArray())
		{
			if(ab.getOpcode() == Opcodes.GETFIELD && ab.getNext() instanceof JumpInsnNode)
			{
				if(ASMHelper.equals(check, (FieldInsnNode)ab))
				{
					spot = (JumpInsnNode) ab.getNext();
				}
			}
		}
		
		InsnList list2 = new InsnList();
		list2.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/EntityUtil", "addedToWorld", "(Lnet/minecraft/entity/Entity;)Z", false));
		list2.add(new JumpInsnNode(Opcodes.IFEQ, spot.label));
		pos.instructions.insert(spot, list2);
	}

	public static void patchZombie(ClassNode classNode)
	{
		MethodNode node = ASMHelper.getMethodNode(classNode, new MCPSidedString("readEntityFromNBT", "func_70037_a").toString(), "(Lnet/minecraft/nbt/NBTTagCompound;)V");
		
		//stop broken method from firing to begin with
		AbstractInsnNode spot = null;
		LabelNode l2 = null;
		for(AbstractInsnNode ab : node.instructions.toArray())
		{
			if(ab instanceof LdcInsnNode)
			{	
				if("IsBaby".equals(((LdcInsnNode)ab).cst))
				{
					spot = ab.getPrevious();//ALOAD INSTRUCTION FOUND
					AbstractInsnNode compare = ab;
					while(compare != null)
					{
						compare = compare.getNext();
						if(compare instanceof JumpInsnNode)
						{
							l2 = ((JumpInsnNode)compare).label;
							break;
						}
					}
					break;
				}
			}
		}
		InsnList list = new InsnList();
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/util/JavaUtil",  "returnFalse", "()Z", false));
		list.add(new JumpInsnNode(Opcodes.IFEQ, l2));
		node.instructions.insertBefore(spot, list);
		
		//inject this.setChild(nbt.getBoolean("isBaby")); to the end of the method
		AbstractInsnNode spot2 = ASMHelper.getLastInstruction(node, Opcodes.RETURN);
		InsnList list2 = new InsnList();
		list2.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list2.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list2.add(new LdcInsnNode("IsBaby"));
		list2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", new MCPSidedString("getBoolean", "func_74767_n").toString(), "(Ljava/lang/String;)Z", false));
		list2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/monster/EntityZombie", new MCPSidedString("setChild", "func_82227_f").toString(), "(Z)V", false));
		node.instructions.insertBefore(spot2, list2);
	}

	public static void transformEntityHanging(ClassNode classNode) 
	{
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
		AbstractInsnNode point = ASMHelper.getLastMethodInsn(construct, Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/EntityHanging", new MCPSidedString("setSize", "func_70105_a").toString(), "(FF)V", false);
		
		InsnList list = new InsnList();
		//this.facingDirection = EnumFacing.NORTH;
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/util/EnumFacing", "NORTH", "Lnet/minecraft/util/EnumFacing;"));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/EntityHanging", new MCPSidedString("facingDirection", "field_174860_b").toString(), "Lnet/minecraft/util/EnumFacing;"));
		
		//this.hangingPosition = this.getPosition().offset(this.facingDirection);
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/EntityHanging", new MCPSidedString("getPosition", "func_180425_c").toString(), "()Lnet/minecraft/util/math/BlockPos;", false));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/EntityHanging", new MCPSidedString("facingDirection", "field_174860_b").toString(), "Lnet/minecraft/util/EnumFacing;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/math/BlockPos", new MCPSidedString("offset", "func_177972_a").toString(), "(Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/util/math/BlockPos;", false));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/EntityHanging", new MCPSidedString("hangingPosition", "field_174861_a").toString(), "Lnet/minecraft/util/math/BlockPos;"));
		
		construct.instructions.insert(point, list);
	}

	public static void transformEntityPainting(ClassNode classNode) 
	{
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
		
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/entity/item/EntityPainting$EnumArt", "KEBAB", "Lnet/minecraft/entity/item/EntityPainting$EnumArt;"));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityPainting", new MCPSidedString("art", "field_70522_e").toString(), "Lnet/minecraft/entity/item/EntityPainting$EnumArt;"));
		
		construct.instructions.insertBefore(ASMHelper.getLastInstruction(construct, Opcodes.RETURN), list);
	}
	
	public static void transformEntityFalling(ClassNode classNode) 
	{
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
		
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new FieldInsnNode(Opcodes.GETSTATIC,"net/minecraft/init/Blocks", new MCPSidedString("STONE", "field_150348_b").toString(), "Lnet/minecraft/block/Block;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/Block", new MCPSidedString("getDefaultState", "func_176223_P").toString(), "()Lnet/minecraft/block/state/IBlockState;", false));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/entity/item/EntityFallingBlock", new MCPSidedString("fallTile", "field_175132_d").toString(), "Lnet/minecraft/block/state/IBlockState;"));
		
		construct.instructions.insertBefore(ASMHelper.getLastInstruction(construct, Opcodes.RETURN), list);
	}
	
	/**
	 * patch seed check
	 */
	public static void patchPlayer(ClassNode classNode) 
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
      	insert.add(new VarInsnNode(Opcodes.ALOAD,0));
      	insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/util/PlayerUtil", "isPlayerOwner", "(Lnet/minecraft/entity/player/EntityPlayerMP;)Z", false));
        insert.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode)start).label));
        node.instructions.insert(start, insert);
        
        //add if(permisionLevel == 0) return true;
        start = node.instructions.getFirst();
        
        InsnList insert2 = new InsnList();
      	insert2.add(new VarInsnNode(Opcodes.ILOAD, 1));
      	LabelNode label = new LabelNode();
      	insert2.add(new JumpInsnNode(Opcodes.IFNE, label) );
      	LabelNode label2 = new LabelNode();
      	insert2.add(label2);
      	insert2.add(new InsnNode(Opcodes.ICONST_1));
      	insert2.add(new InsnNode(Opcodes.IRETURN));
      	insert2.add(label);
      	
        node.instructions.insertBefore(start, insert2);
        
        //add the chat event
        /*EntityPlayerMP
		MethodNode node2 = ASMHelper.getMethodNode(classNode, new MCPSidedString("sendStatusMessage", "func_146105_b").toString(), "(Lnet/minecraft/util/text/ITextComponent;Z)V");
		InsnList list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;"));
		list.add(new TypeInsnNode(Opcodes.NEW, "com/evilnotch/lib/minecraft/event/MessageEvent$PlayerStatusEvent"));
		list.add(new InsnNode(Opcodes.DUP));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ILOAD, 2));
		list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "com/evilnotch/lib/minecraft/event/MessageEvent$PlayerStatusEvent", "<init>", "(Lnet/minecraft/entity/Entity;Z)V", false));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false));
		LabelNode l1 = new LabelNode();
		list.add(new JumpInsnNode(Opcodes.IFEQ, l1));
		LabelNode l2 = new LabelNode();
		list.add(l2);
		list.add(new InsnNode(Opcodes.RETURN));
		list.add(l1);
		node2.instructions.insert(ASMHelper.getFirstInstruction(node2), list);*/

		/*
		 * 
mv.visitFieldInsn(GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
mv.visitTypeInsn(NEW, "com/evilnotch/lib/minecraft/event/MessageEvent$SendMessage");
mv.visitInsn(DUP);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "com/evilnotch/lib/minecraft/event/MessageEvent$SendMessage", "<init>", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/text/ITextComponent;)V", false);
mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
Label l1 = new Label();
mv.visitJumpInsn(IFNE, l1);
Label l2 = new Label();
mv.visitLabel(l2);
mv.visitLineNumber(3292, l2);
mv.visitInsn(RETURN);
mv.visitLabel(l1);
		 */
		InsnList list2 = new InsnList();
		MethodNode node3 = ASMHelper.getMethodNode(classNode, new MCPSidedString("sendMessage","func_145747_a").toString(), "(Lnet/minecraft/util/text/ITextComponent;)V");
		node3.instructions.insert(ASMHelper.getFirstInstruction(node3), list2);
	}

}
