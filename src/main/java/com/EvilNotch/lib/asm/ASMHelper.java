package com.EvilNotch.lib.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.EvilNotch.lib.Api.MCPSidedString;
import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.entity.Entity;

public class ASMHelper 
{	
	public static HashMap<String,ClassNode> cacheNodes = new HashMap();
	/**
	 * srg support doesn't patch local vars nor instructions
	 */
	public static MethodNode replaceMethod(ClassNode classNode,String inputStream,String method_name,String method_desc,String srgname)
	{
		MethodNode origin = FMLCorePlugin.isObf ? getMethodNode(classNode,srgname,method_desc) : getMethodNode(classNode,method_name,method_desc);
		try
		{
			MethodNode toReplace = getCachedMethodNode(inputStream, FMLCorePlugin.isObf ? srgname : method_name, method_desc);
			origin.localVariables.clear();
			origin.instructions = toReplace.instructions;
			origin.localVariables = toReplace.localVariables;
//			origin.access = toReplace.access;//Temporarily commented out till further testing is done doing this isn't safe without checks
			origin.annotationDefault = toReplace.annotationDefault;
			origin.tryCatchBlocks = toReplace.tryCatchBlocks;
			origin.visibleAnnotations = toReplace.visibleAnnotations;
			origin.visibleLocalVariableAnnotations = toReplace.visibleLocalVariableAnnotations;
			origin.visibleTypeAnnotations = toReplace.visibleTypeAnnotations;
			return origin;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * patch a method you can call this directly after replacing it
	 */
	public static void patchMethod(MethodNode node,String className,String oldClassName)
	{
		patchInstructions(node, className, oldClassName);
		patchLocals(node, className);
	}
	
	/**
	 * notch name version of replacing a method don't recommend it at all since you can use regular replace method to do so
	 */
	public static void replaceMethodNotch(ClassNode classToTransform,String inputStream,MCPSidedString method_name,MCPSidedString method_desc,MCPSidedString methodNameInject) 
	{
		long time = System.currentTimeMillis();
		MethodNode origin = getMethodNode(classToTransform,method_name.toString(),method_desc.toString());
		try
		{
			MethodNode toReplace = getCachedMethodNode(inputStream,methodNameInject.toString(), method_desc.toString());
			origin.localVariables.clear();
			origin.instructions = toReplace.instructions;
			origin.localVariables = toReplace.localVariables;
//			origin.access = toReplace.access;
			origin.annotationDefault = toReplace.annotationDefault;
			origin.tryCatchBlocks = toReplace.tryCatchBlocks;
			origin.visibleAnnotations = toReplace.visibleAnnotations;
			origin.visibleLocalVariableAnnotations = toReplace.visibleLocalVariableAnnotations;
			origin.visibleTypeAnnotations = toReplace.visibleTypeAnnotations;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * get a method node from a possble cached classnode
	 */
	public static MethodNode getCachedMethodNode(String inputStream, String obMethod, String method_desc) throws IOException 
	{
		if(cacheNodes.containsKey(inputStream))
		{
			System.out.println("using cached node:");
			ClassNode node = cacheNodes.get(inputStream);
			return getMethodNode(node,obMethod,method_desc);
		}
		InputStream stream = ASMHelper.class.getClassLoader().getResourceAsStream(inputStream);
		ClassNode node = getClassNode(stream);
		cacheNodes.put(inputStream, node);
		return getMethodNode(node,obMethod,method_desc);
	}
	public static MethodNode getMethodNode(Class ourClass, String method_name,String method_desc) throws IOException 
	{
		String className = ourClass.getName();
		String classAsPath = className.replaceAll("\\.", "/") + ".class";
		InputStream stream = ourClass.getClassLoader().getResourceAsStream(classAsPath);
		ClassNode node = getClassNode(stream);
		return getMethodNode(node,method_name,method_desc);
	}
	public static MethodNode getMethodNode(ClassNode classNode, String method_name, String method_desc) 
	{
		for (MethodNode method : classNode.methods)
		{
			if (method.name.equals(method_name) && method.desc.equals(method_desc))
			{
				return method;
			}
		}
		return null;
	}
	/**
	 * get a ClassNode from an input stream
	 * @throws IOException
	 */
	public static ClassNode getClassNode(InputStream stream) throws IOException 
	{
		byte[] newbyte = IOUtils.toByteArray(stream);
		return getClassNode(newbyte);
	}
	/**
	 * if you already have the bytes and you don't need the class reader
	 */
	public static ClassNode getClassNode(byte[] newbyte) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(newbyte);
		classReader.accept(classNode,0);
		return classNode;
	}
	/**
	 * patch all references on the local variable table instanceof of this to a new class
	 */
	public static void patchLocals(MethodNode method,String name)
	{
		for(LocalVariableNode lvn : method.localVariables)
		{
			if(lvn.name.equals("this"))
			{
				lvn.desc = "L" + name.replace('.', '/') + ";";
			}
		}
	}
	/**
	 * patch previous object owner instructions to new owner with filtering out static fields/method calls
	 */
	public static void patchInstructions(MethodNode mn, String className, String oldClassName) 
	{
		for(AbstractInsnNode ain : mn.instructions.toArray())
		{
			if(ain instanceof MethodInsnNode)
			{
				MethodInsnNode min = (MethodInsnNode)ain;
				if(min.owner.equals(oldClassName) && !isStaticMethodNode(min))
				{
					min.owner=className.replaceAll("\\.", "/");
				}
			}
			else if(ain instanceof FieldInsnNode)
			{
				FieldInsnNode fin = (FieldInsnNode)ain;
				if(fin.owner.equals(oldClassName) && !isStaticFeild(fin))
				{
					fin.owner=className.replaceAll("\\.", "/");
				}
			}
		}
	}
	/**
	 * this will determine if the node is static or not
	 */
	public static boolean isStaticMethodNode(MethodInsnNode min) {
		int opcode = min.getOpcode();
		return Opcodes.INVOKESTATIC == opcode;
	}
	/**
	 *this will determine if the node is static or not
	 */
	public static boolean isStaticFeild(FieldInsnNode fin) {
		int opcode = fin.getOpcode();
		return Opcodes.GETSTATIC == opcode || Opcodes.PUTSTATIC == opcode;
	}
	/**
	 * after editing one class call this for cleanup
	 */
	public static void clearCacheNodes() {
		cacheNodes.clear();
	}

	/**
	 * try not to use this replacing methods adding fields is more acceptable rather then replacing classes thus throwing out other people's asm
	 * and causing mod incompatibilities
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] replaceClass(String inputStream) throws IOException {
		InputStream initialStream = ASMHelper.class.getClassLoader().getResourceAsStream(inputStream);
		return IOUtils.toByteArray(initialStream);
	}
	/**
	 * add an interface to a class
	 */
	public static void addInterface(ClassNode node,String theInterface)
	{
		node.interfaces.add(theInterface);
	}
	
	/**
	 * add a object field to the class
	 */
	public static void addFeild(ClassNode node,String feildName,String desc)
	{
		addFeild(node,feildName,desc,null);
	}
	/**
	 * add a object field to the class with optional signature. The paramDesc is a descriptor of the types of a class HashMap<key,value>
	 */
	public static void addFeild(ClassNode node,String feildName,String desc,String paramDesc)
	{
		FieldNode field = new FieldNode(Opcodes.ACC_PUBLIC, feildName, desc, paramDesc,null);
		node.fields.add(field);
	}
	/**
	 * don't add the method if it's already has it
	 */
	public static void addIfMethod(ClassNode classNode, String inputStream, String method_name, String descriptor) throws IOException
	{
		MethodNode method = getCachedMethodNode(inputStream, method_name, descriptor);
		Class c = method.getClass();
		if(containsMethod(classNode,method_name,descriptor))
			return;
		classNode.methods.add(method);
	}
	/**
	 * search from the class node if it contains the method
	 * @return
	 */
	public static boolean containsMethod(ClassNode classNode, String method_name, String descriptor) {
		for(MethodNode node : classNode.methods)
			if(node.name.equals(method_name) && node.desc.equals(descriptor))
				return true;
		return false;
	}

	/**
	 * add a method no obfuscated checks you have to do that yourself if you got a deob compiled class
	 * no checks for patching the local variables nor the instructions
	 */
	public static MethodNode addMethod(ClassNode classNode, String inputStream, String method_name, String descriptor) throws IOException 
	{
		MethodNode method = getCachedMethodNode(inputStream, method_name, descriptor);
		Class c = method.getClass();
		classNode.methods.add(method);
		return method;
	}
	/**
	 * remove a method don't remove ones that are going to get executed unless you immediately add the same method and descriptor back
	 * @throws IOException 
	 */
	public static void removeMethod(ClassNode classNode, String method_name, String method_desc) throws IOException
	{
		MethodNode method = getMethodNode(classNode, method_name, method_desc);
		if(method != null)
		{
			System.out.println("removing method:" + classNode.methods.remove(method));
		}
	}
	/**
	 * find the first instruction to inject
	 */
	public static AbstractInsnNode getFirstInstruction(MethodNode method,int opcode) 
	{
		for(AbstractInsnNode node : method.instructions.toArray())
		{
			if(node.getOpcode() == opcode)
			{
				return node;
			}
		}
		return null;
	}
	/**
	 * getting the first instanceof of this will usually tell you where the initial injection point should be after
	 */
	public static LineNumberNode getLineNumberNode(MethodNode method) {
		for(AbstractInsnNode obj : method.instructions.toArray())
			if(obj instanceof LineNumberNode)
				return (LineNumberNode) obj;
		return null;
	}
	
	/**
	 * use this if you ever are adding local variables to the table to dynamically get them
	 * @return -1 if doesn't exist
	 */
	public static int getLocalVarIndex(MethodNode node,String varName)
	{
		for(LocalVariableNode n : node.localVariables)
		{
			if(n.name.equals(varName))
			{
				return n.index;
			}
		}
		return -1;
	}
	/**
	 * get a constructor since they are MethodNodes
	 */
	public static MethodNode getConstructionNode(ClassNode classNode, String desc) {
		return getMethodNode(classNode, "<init>", desc);
	}
	/**
	 * helpful for finding injection point to the end of constructors
	 */
	public static AbstractInsnNode getLastPutField(MethodNode mn) {
		return getLastInstruction(mn, Opcodes.PUTFIELD);
	}
	/**
	 * optimized way of getting a last instruction
	 */
	public static AbstractInsnNode getLastInstruction(MethodNode method, int opCode) 
	{
		AbstractInsnNode[] arr = method.instructions.toArray();
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode node = arr[i];
			if(node.getOpcode() == opCode)
				return node;
		}
		return null;
	}
}
