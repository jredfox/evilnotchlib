package com.evilnotch.lib.asm.transformer;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.io.IOException;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.util.ASMHelper;


public class CapTransformer {

	public static void transFormEntityCaps(String name, ClassNode classNode, boolean obfuscated) throws IOException 
	{
    	implementICapProvider(classNode, name, "Lnet/minecraft/entity/Entity;");
    	
    	//Serialization and ticks
    	String owner = "net/minecraft/entity/Entity";
    	String desc_nbt = "Lnet/minecraft/nbt/NBTTagCompound;";
    	String readDesc = "(Ljava/lang/Object;" + desc_nbt + ")V";
    	
     	String method_readFromNBT = new MCPSidedString("readFromNBT","func_70020_e").toString();
    	MethodNode readFromNBT = ASMHelper.getMethodNode(classNode, method_readFromNBT, "(" + desc_nbt + ")V");
    	
    	//Entity Constructor since forge events appear to be unreliable somehow someway
    	MethodNode constructor = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;)V");
    	InsnList toInsert0 = new InsnList();
    	toInsert0.add(new VarInsnNode(ALOAD,0));
    	toInsert0.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/evilnotch/lib/minecraft/capability/registry/CapabilityRegistry", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
    	constructor.instructions.insert(ASMHelper.getLastPutField(constructor),toInsert0);
    	
    	//readFromNBT
    	InsnList toInsert1 = new InsnList();
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new VarInsnNode(ALOAD,1));
    	toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "readFromNBT", readDesc, false));
    	AbstractInsnNode spotNode = ASMHelper.getFirstInstruction(readFromNBT, Opcodes.ALOAD);
    	readFromNBT.instructions.insertBefore(spotNode,toInsert1);
    	
    	String method_writeToNBT = new MCPSidedString("writeToNBT","func_189511_e").toString();
        MethodNode writeToNBT = ASMHelper.getMethodNode(classNode,method_writeToNBT, "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;");
		
        //writeToNBT
        InsnList toInsert2 = new InsnList();
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,  owner, "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new VarInsnNode(ALOAD,1));
   	    toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "writeToNBT", readDesc,false));
   	    AbstractInsnNode spotWriteNode = ASMHelper.getFirstInstruction(writeToNBT, Opcodes.ALOAD);
   	    writeToNBT.instructions.insertBefore(spotWriteNode,toInsert2);

   	    //tick injection
   	    MethodNode tick = ASMHelper.getMethodNode(classNode, new MCPSidedString("onEntityUpdate","func_70030_z").toString(), "()V");
   	    InsnList isntick = new InsnList();
   	    isntick.add(new VarInsnNode(ALOAD,0));
   	    isntick.add(new FieldInsnNode(Opcodes.GETFIELD,owner, "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
   	    isntick.add(new VarInsnNode(ALOAD,0));
   	    isntick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
   	    AbstractInsnNode spotTickNode = null;
   	    AbstractInsnNode[] arr = tick.instructions.toArray();
   	    String startSection = new MCPSidedString("startSection","func_76320_a").toString();
   	    for(AbstractInsnNode node : arr)
    	{
    		if(node.getOpcode() == Opcodes.INVOKEVIRTUAL && node instanceof MethodInsnNode)
    		{
    			MethodInsnNode m = (MethodInsnNode)node;
    			if(m.name.equals(startSection) && m.desc.equals("(Ljava/lang/String;)V"))
    			{
    				spotTickNode = node;
    				break;
    			}
    		}
    	}
 	    tick.instructions.insert(spotTickNode,isntick);
	}

	public static void transFormTileEntityCaps(String name, ClassNode classNode, boolean obfuscated) throws IOException 
	{
    	//add interface and implement methods
    	implementICapProvider(classNode,name,"Lnet/minecraft/tileentity/TileEntity;");
    	
    	//Serialization
    	String owner = "net/minecraft/tileentity/TileEntity";
    	String desc_nbt = "Lnet/minecraft/nbt/NBTTagCompound;";
    	String readDesc = "(Ljava/lang/Object;" + desc_nbt + ")V";
    	
     	String method_readFromNBT = new MCPSidedString("readFromNBT","func_145839_a").toString();
    	MethodNode readFromNBT = ASMHelper.getMethodNode(classNode, method_readFromNBT, "(" + desc_nbt + ")V");
    	
    	//readFromNBT
    	InsnList toInsert1 = new InsnList();
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, owner, "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
    	toInsert1.add(new VarInsnNode(ALOAD,0));
    	toInsert1.add(new VarInsnNode(ALOAD,1));
    	toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "readFromNBT", readDesc, false));
    	AbstractInsnNode spotNode = ASMHelper.getFirstInstruction(readFromNBT, Opcodes.ALOAD);
    	readFromNBT.instructions.insertBefore(spotNode,toInsert1);
    	
    	String method_writeToNBT = new MCPSidedString("writeToNBT","func_189515_b").toString();
        MethodNode writeToNBT = ASMHelper.getMethodNode(classNode,method_writeToNBT, "(" + desc_nbt + ")" + desc_nbt);
		
        //writeToNBT
        InsnList toInsert2 = new InsnList();
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,  owner, "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
        toInsert2.add(new VarInsnNode(ALOAD,0));
        toInsert2.add(new VarInsnNode(ALOAD,1));
   	    toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "writeToNBT", readDesc,false));
   	    AbstractInsnNode spotWriteNode = ASMHelper.getFirstInstruction(writeToNBT, Opcodes.ALOAD);
   	    writeToNBT.instructions.insertBefore(spotWriteNode,toInsert2);
   	    
   	    //Constructor cap initiation
   	    MethodNode constructor = ASMHelper.getConstructionNode(classNode,"()V");
   	    InsnList toInsert3 = new InsnList();
   		toInsert3.add(new VarInsnNode(ALOAD,0));
   		toInsert3.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/evilnotch/lib/minecraft/capability/registry/CapabilityRegistry", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
   		//find insertion point in the constructor to make capabilities register themselves
   		constructor.instructions.insert(ASMHelper.getLastPutField(constructor), toInsert3);
   	    //tick injection is done by the world since not all tiles are tickable
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
		String desc_nbt = "Lnet/minecraft/nbt/NBTTagCompound;";
		String stackCompound = new MCPSidedString("stackTagCompound","field_77990_d").toString();
		implementICapProvider(classNode,name,"Lnet/minecraft/item/ItemStack;");
		
		MethodNode capNode = ASMHelper.getMethodNode(classNode, "forgeInit", "()V");
		
		InsnList toInsert0 = new InsnList();
		//inject CapRegUtil.registerCapsToObject(this);
		toInsert0.add(new VarInsnNode(ALOAD,0));
		toInsert0.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/evilnotch/lib/minecraft/capability/registry/CapabilityRegistry", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		capNode.instructions.insertBefore(ASMHelper.getFirstInstruction(capNode, Opcodes.ALOAD),toInsert0);
		
		InsnList toInsert1 = new InsnList();

		//inject this.capContainer.readFromNBT(this,this.stackCompound);
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemStack", "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new VarInsnNode(ALOAD,0));
		toInsert1.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/item/ItemStack", stackCompound, "Lnet/minecraft/nbt/NBTTagCompound;"));
		toInsert1.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		//inject all instructions
		AbstractInsnNode spotNode = ASMHelper.getLastInstruction(capNode, Opcodes.RETURN);
		capNode.instructions.insertBefore(spotNode, toInsert1);
		
		//writeToNBT inject this.capContainer.writeToNBT(this,nbt);
		InsnList toInsert2 = new InsnList();
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/item/ItemStack", "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new VarInsnNode(ALOAD,0));
		toInsert2.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/item/ItemStack", stackCompound, "Lnet/minecraft/nbt/NBTTagCompound;"));
		toInsert2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/capability/CapContainer", "writeToNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		
		MethodNode write = ASMHelper.getMethodNode(classNode, new MCPSidedString("writeToNBT","func_77955_b").toString(), "(" + desc_nbt + ")" + desc_nbt);
		AbstractInsnNode spoteWriteNode = ASMHelper.getFirstInstruction(write,Opcodes.GETSTATIC);
		write.instructions.insertBefore(spoteWriteNode,toInsert2);
		
		//patch ItemStack#onCopy() by injecting this.capContainer.readFromNBT(this,this.stackCompound);
		MethodNode copy = ASMHelper.getMethodNode(classNode, new MCPSidedString("copy","func_77946_l").toString(), "()Lnet/minecraft/item/ItemStack;");
		InsnList toInsert3 = new InsnList();
		toInsert3.add(new VarInsnNode(ALOAD,0));
		toInsert3.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/item/ItemStack", "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
		toInsert3.add(new VarInsnNode(ALOAD,0));
		toInsert3.add(new VarInsnNode(ALOAD,0));
		toInsert3.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/item/ItemStack", stackCompound, "Lnet/minecraft/nbt/NBTTagCompound;"));
		toInsert3.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		AbstractInsnNode lastSpot = ASMHelper.getLastInstruction(copy, Opcodes.ALOAD);
		copy.instructions.insertBefore(lastSpot,toInsert3);
		
		//patch ItemStack#setTagCompound() by injecting this.capContainer.readFromNBT(this,nbt);
		InsnList toInsert4 = new InsnList();
		toInsert4.add(new VarInsnNode(ALOAD,0));
		toInsert4.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/item/ItemStack", "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
		toInsert4.add(new VarInsnNode(ALOAD,0));
		toInsert4.add(new VarInsnNode(ALOAD,1));
		toInsert4.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/capability/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		MethodNode set = ASMHelper.getMethodNode(classNode, new MCPSidedString("setTagCompound","func_77982_d").toString(), "(Lnet/minecraft/nbt/NBTTagCompound;)V");
		set.instructions.insertBefore(ASMHelper.getFirstInstruction(set, Opcodes.ALOAD), toInsert4);
	}
	
	/**
	 * add world capabilities
	 * @throws IOException 
	 */
	public static void transformWorld(String name,ClassNode classNode,String inputBase,boolean obfuscated) throws IOException
	{
		implementICapProvider(classNode, name, "Lnet/minecraft/world/World;");
    	
		ASMHelper.addMethod(classNode, inputBase + "World", "initWorldCaps", "()V");
    	
    	//add the method of World#tickChunks
    	MethodNode tickChunks = ASMHelper.addMethod(classNode, inputBase + "World", "tickChunks", "()V");
		MethodNode tickTileCaps = ASMHelper.addMethod(classNode, inputBase + "World", "tickTileCaps", "()V");
    	
    	//inject line this.initWorldCaps();
    	InsnList toInject = new InsnList();
    	toInject.add(new VarInsnNode(ALOAD,0));
    	toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/World", "initWorldCaps", "()V", false));
    	MethodNode caps = ASMHelper.getMethodNode(classNode, "initCapabilities", "()V");
    	caps.instructions.insertBefore(ASMHelper.getFirstInstruction(caps, Opcodes.ALOAD),toInject);
	}
	
	/**
	 * inject the line to make tile entities caps tick safer then screwing around with other people's classes
	 * @throws IOException 
	 */
	public static void injectWorldTickers(String name, ClassNode classNode,String inputBase, boolean obfuscated) throws IOException
	{	
		MethodNode updateEnts = ASMHelper.getMethodNode(classNode, new MCPSidedString("updateEntities","func_72939_s").toString(), "()V");
    	
		//put both world tickers here
		String worldInfo = new MCPSidedString("worldInfo","field_72986_A").toString();
		MethodNode tick = ASMHelper.getMethodNode(classNode, new MCPSidedString("tick","func_72835_b").toString(), "()V");
		AbstractInsnNode tickPoint = ASMHelper.getFirstInstruction(tick, Opcodes.ALOAD);
		
		InsnList toTick = new InsnList();
		
		//inject line this.capContainer.tick(this);
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/World", "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/evilnotch/lib/minecraft/capability/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
		//inject ((ICapProvider)this.worldInfo).getCapContainer().tick(this.worldInfo); right after the profilers start
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/World", worldInfo, "Lnet/minecraft/world/storage/WorldInfo;"));
		toTick.add(new TypeInsnNode(Opcodes.CHECKCAST,"com/evilnotch/lib/minecraft/capability/ICapabilityProvider"));
		toTick.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"com/evilnotch/lib/minecraft/capability/ICapabilityProvider", "getCapContainer", "()Lcom/evilnotch/lib/minecraft/capability/CapContainer;", true));
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/World", worldInfo, "Lnet/minecraft/world/storage/WorldInfo;"));
		toTick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "tick", "(Ljava/lang/Object;)V", false));
		//inject this.tickChunkOb/Deob
		toTick.add(new VarInsnNode(ALOAD,0));
		toTick.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/world/World", "tickChunks", "()V", false));
		
		tick.instructions.insertBefore(tickPoint,toTick);
    	
    	//look for first instanceof tickabletiles and the first instance it calls upon an iterator
		String fname = new MCPSidedString("tickableTileEntities","field_175730_i").toString();
		for(AbstractInsnNode obj : updateEnts.instructions.toArray())
		{
			if(obj instanceof FieldInsnNode)
			{
				FieldInsnNode isn = (FieldInsnNode)obj;
				if(isn.name.equals(fname))
				{
					AbstractInsnNode point = isn.getNext();
					if(point instanceof MethodInsnNode)
					{
						MethodInsnNode mnode = (MethodInsnNode)point;
						if(mnode.getOpcode() == Opcodes.INVOKEINTERFACE && mnode.name.equals("iterator"))
						{
							InsnList toInsert = new InsnList();
							toInsert.add(new VarInsnNode(ALOAD,0));
							toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/World", "tickTileCaps", "()V", false));
		
							point = point.getNext();
							updateEnts.instructions.insert(point,toInsert);
							break;
						}
					}
				}	
			}
		}
	}
	
	/**
	 * add world caps to WorldInfo.class yes there is alot of line injections blame there being no default constructor
	 */
	public static void transformWorldInfo(ClassNode classNode,String name, boolean obfuscated) throws IOException
	{
		implementICapProvider(classNode,name,"Lnet/minecraft/world/storage/WorldInfo;");
		
		//inject caps to default constructor
		MethodNode defaultConstruct = ASMHelper.getConstructionNode(classNode, "()V");
		InsnList toInsertDefault = new InsnList();
		//make it visit a line so the decompiler loads right
		LabelNode labelnode = new LabelNode(new Label());
		LineNumberNode lineNode = new LineNumberNode(95,labelnode);
		toInsertDefault.add(labelnode);
		toInsertDefault.add(lineNode);
		toInsertDefault.add(new VarInsnNode(ALOAD,0));
		toInsertDefault.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/evilnotch/lib/minecraft/capability/registry/CapabilityRegistry", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		defaultConstruct.instructions.insert(ASMHelper.getLastPutField(defaultConstruct),toInsertDefault);
		
		//inject readFromNBT constructor
		MethodNode constructNBT = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/nbt/NBTTagCompound;)V");
		for(AbstractInsnNode obj : constructNBT.instructions.toArray())
		{
			if(obj instanceof LdcInsnNode)
			{
				LdcInsnNode ldc = (LdcInsnNode)obj;
				if(ldc.cst.toString().equals("Version"))
				{
					AbstractInsnNode spot = ldc.getPrevious().getPrevious();
					InsnList toInsert = new InsnList();
					
					//inject CapabilityRegistry.registerCapsToObj(this)
					toInsert.add(new VarInsnNode(ALOAD,0));
					toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/evilnotch/lib/minecraft/capability/registry/CapabilityRegistry", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
					
					//inject line this.capContainer.readFromNBT(this,nbt);
					toInsert.add(new VarInsnNode(ALOAD,0));
					toInsert.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/storage/WorldInfo", "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
					toInsert.add(new VarInsnNode(ALOAD,0));
					toInsert.add(new VarInsnNode(ALOAD,1));
					toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
					
					constructNBT.instructions.insert(spot,toInsert);
					break;
				}
			}
		 }
		
		//writeToNBT
		MethodNode writeToNBT = ASMHelper.getMethodNode(classNode, new MCPSidedString("updateTagCompound","func_76064_a").toString(), "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/nbt/NBTTagCompound;)V");
		AbstractInsnNode spot = ASMHelper.getFirstInstruction(writeToNBT);
		//inject line this.capContainer.writeToNBT(this,nbt);
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(ALOAD,0));
		toInsert.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/storage/WorldInfo", "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
		toInsert.add(new VarInsnNode(ALOAD,0));
		toInsert.add(new VarInsnNode(ALOAD,1));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "writeToNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		writeToNBT.instructions.insert(spot,toInsert);
		
		//patch the clone the cap container
		MethodNode constructCopy = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/storage/WorldInfo;)V");
		AbstractInsnNode injectionPoint = ASMHelper.getLastPutField(constructCopy);
		
		//inject CapabilityRegistry.registerCapsToObj(this);
		InsnList toCopy = new InsnList();
		toCopy.add(new VarInsnNode(ALOAD,0));
		toCopy.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/evilnotch/lib/minecraft/capability/registry/CapabilityRegistry", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		
		//inject this.capContainer.readFromNBT(worldinfo,worldinfo.cloneNBT(null));
		toCopy.add(new VarInsnNode(ALOAD,0));
		toCopy.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/world/storage/WorldInfo", "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;"));
		toCopy.add(new VarInsnNode(ALOAD,1));
		toCopy.add(new VarInsnNode(ALOAD,1));
		toCopy.add(new InsnNode(Opcodes.ACONST_NULL));
		toCopy.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"net/minecraft/world/storage/WorldInfo", new MCPSidedString("cloneNBTCompound","func_76082_a").toString(), "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;", false));
		toCopy.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		constructCopy.instructions.insert(injectionPoint,toCopy);
		
		//inject CapabilityRegistry.registerCapsToObj(this) into the other constructor
		MethodNode construct = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V");
		InsnList toConstruct = new InsnList();
		toConstruct.add(new VarInsnNode(ALOAD,0));
		toConstruct.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/evilnotch/lib/minecraft/capability/registry/CapabilityRegistry", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		AbstractInsnNode constructSpot = ASMHelper.getLastPutField(construct);
		construct.instructions.insert(constructSpot, toConstruct);
	}
	
	/**
	 * injects registering of the caps to chunk and capContainer with interface the rest is handled via forge event since chunks really don't self serialize
	 * @param classNode
	 * @param name
	 * @param obfuscated
	 * @throws IOException
	 */
	public static void transformChunk(ClassNode classNode,String name,boolean obfuscated) throws IOException
	{
		implementICapProvider(classNode, name, "Lnet/minecraft/world/chunk/Chunk;");
		
		MethodNode constructor = ASMHelper.getConstructionNode(classNode, "(Lnet/minecraft/world/World;II)V");
		AbstractInsnNode spot = ASMHelper.getLastPutField(constructor);
		//inject CapabilityRegistry.registerCapToObj(this);
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(ALOAD,0));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"com/evilnotch/lib/minecraft/capability/registry/CapabilityRegistry", "registerCapsToObj", "(Ljava/lang/Object;)V", false));
		constructor.instructions.insert(spot,toInsert);
	}
	
	/**
	 * we are not done patching the chunks until serialization is patched as well
	 */
	public static void transformAnvilChunkLoader(ClassNode classNode, String name, boolean obfuscated) 
	{
		MethodNode read = ASMHelper.getMethodNode(classNode, new MCPSidedString("readChunkFromNBT","func_75823_a").toString(),"(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;");
		//inject line ((ICapProvider)chunk).getCapContainer().readFromNBT(chunk, compound); right before the return statement
		InsnList toInsert = new InsnList();
		toInsert.add(new VarInsnNode(ALOAD,5));
		toInsert.add(new TypeInsnNode(Opcodes.CHECKCAST,"com/evilnotch/lib/minecraft/capability/ICapabilityProvider"));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"com/evilnotch/lib/minecraft/capability/ICapabilityProvider", "getCapContainer", "()Lcom/evilnotch/lib/minecraft/capability/CapContainer;", true));			
		toInsert.add(new VarInsnNode(ALOAD,5));
		toInsert.add(new VarInsnNode(ALOAD,2));
		toInsert.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "readFromNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		AbstractInsnNode readSpot = null;
		boolean nextALOAD = false;
		String chunkClass = "net/minecraft/world/chunk/Chunk";
		for(AbstractInsnNode node : read.instructions.toArray())
		{
			if(node.getOpcode() == Opcodes.INVOKESPECIAL && node instanceof MethodInsnNode)
			{
				MethodInsnNode mNode = (MethodInsnNode)node;
				if(mNode.name.equals("<init>") && mNode.owner.equals(chunkClass))
				{
					nextALOAD = true;
				}
			}
			if(nextALOAD && node.getOpcode() == Opcodes.ALOAD)
			{
				readSpot = node;
				break;
			}
		}
		read.instructions.insertBefore(readSpot,toInsert);

		//writeToNBT inject this line ((ICapProvider)chunkIn).getCapContainer().writeToNBT(chunkIn, compound);
		MethodNode write = ASMHelper.getMethodNode(classNode, new MCPSidedString("writeChunkToNBT","func_75820_a").toString(), "(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)V");
		InsnList writeIsn = new InsnList();
		writeIsn.add(new VarInsnNode(ALOAD,1));
		writeIsn.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/evilnotch/lib/minecraft/capability/ICapabilityProvider"));
		writeIsn.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,"com/evilnotch/lib/minecraft/capability/ICapabilityProvider", "getCapContainer", "()Lcom/evilnotch/lib/minecraft/capability/CapContainer;", true));
		writeIsn.add(new VarInsnNode(ALOAD,1));
		writeIsn.add(new VarInsnNode(ALOAD,3));
		writeIsn.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,"com/evilnotch/lib/minecraft/capability/CapContainer", "writeToNBT", "(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V", false));
		
		AbstractInsnNode insertPoint = ASMHelper.getFirstInstruction(write, Opcodes.ALOAD);
		write.instructions.insertBefore(insertPoint, writeIsn);
	}

	/**
	 * make a class automatically implement ICapProvider without readFromNBT(),writeToNBT(), or tick() implementations
	 * @param classNode
	 * @param name
	 * @throws IOException
	 */
	public static void implementICapProvider(ClassNode classNode, String name,String classType) throws IOException 
	{
		ASMHelper.addFeild(classNode, "capContainer", "Lcom/evilnotch/lib/minecraft/capability/CapContainer;","Lcom/evilnotch/lib/minecraft/capability/CapContainer<" + classType + ">;");
		
    	ASMHelper.addInterface(classNode, "com/evilnotch/lib/minecraft/capability/ICapabilityProvider");
    	MethodNode getCap = ASMHelper.addMethod(classNode,"com/evilnotch/lib/asm/gen/Caps.class", "getCapContainer", "()Lcom/evilnotch/lib/minecraft/capability/CapContainer;");
    	ASMHelper.patchMethod(getCap, name, "com/evilnotch/lib/asm/gen/Caps");
    	
    	MethodNode setCap = ASMHelper.addMethod(classNode,"com/evilnotch/lib/asm/gen/Caps.class", "setCapContainer", "(Lcom/evilnotch/lib/minecraft/capability/CapContainer;)V");
    	ASMHelper.patchMethod(setCap, name, "com/evilnotch/lib/asm/gen/Caps");
    	
    	MethodNode get = ASMHelper.addMethod(classNode,"com/evilnotch/lib/asm/gen/Caps.class", "getCapability", "(Lnet/minecraft/util/ResourceLocation;)Lcom/evilnotch/lib/minecraft/capability/ICapability;");
    	ASMHelper.patchMethod(get, name, "com/evilnotch/lib/asm/gen/Caps");
    	
    	MethodNode getTick = ASMHelper.addMethod(classNode,"com/evilnotch/lib/asm/gen/Caps.class", "getCapabilityTick", "(Lnet/minecraft/util/ResourceLocation;)Lcom/evilnotch/lib/minecraft/capability/ICapabilityTick;");
    	ASMHelper.patchMethod(getTick, name, "com/evilnotch/lib/asm/gen/Caps");
	}
}
