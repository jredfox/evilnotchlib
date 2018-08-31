package com.EvilNotch.lib.asm;

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
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.EvilNotch.lib.minecraft.content.capabilites.registry.CapContainer;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.entity.Entity;

public class TestTransformer 
{	
	public static HashMap<String,ClassNode> cacheNodes = new HashMap();
	
	public static void transformMethod(ClassNode classToTransform,String className,String inputStream,String method_name,String method_desc,String c,String v,String obMethod) 
	{
		long time = System.currentTimeMillis();
		MethodNode method = FMLCorePlugin.isObf ? getMethodNode(classToTransform,c,v) : getMethodNode(classToTransform,method_name,method_desc);
		try
		{
			MethodNode mn = getCachedMethodNode(inputStream, FMLCorePlugin.isObf ? obMethod : method_name, method_desc);
			method.localVariables.clear();
			method.instructions = mn.instructions;
			method.localVariables = getLocalVar(mn,className);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		JavaUtil.printTime(time, "Done Patching " + className + "." + method_name + "() :");
	}
	
	public static MethodNode getCachedMethodNode(String inputStream, String obMethod, String method_desc) throws IOException 
	{
		if(cacheNodes.containsKey(inputStream))
		{
			System.out.println("using cached node:");
			ClassNode node = cacheNodes.get(inputStream);
			return getMethodNode(node,obMethod,method_desc);
		}
		InputStream stream = TestTransformer.class.getClassLoader().getResourceAsStream(inputStream);
		ClassNode node = getClassNode(stream);
		cacheNodes.put(inputStream, node);
		return getMethodNode(node,obMethod,method_desc);
	}
	public static MethodNode GetMethodNode(Class ourClass, String method_name,String method_desc) throws IOException 
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
	
	public static ClassNode getClassNode(InputStream stream) throws IOException 
	{
		byte[] newbyte = IOUtils.toByteArray(stream);
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(newbyte);
		classReader.accept(classNode,0);
		return classNode;
	}

	public static List<LocalVariableNode> getLocalVar(MethodNode method, String name) throws IOException 
	{
		name = name.replaceAll("\\.", "/");
		List<LocalVariableNode> l = method.localVariables;
		for(LocalVariableNode lvn : l)
		{
			if(lvn.name.equals("this"))
			{
				lvn.desc = "L" + name + ";";
			}
		}
		return l;
	}
	public static void patchLocals(MethodNode method,String name)
	{
		List<LocalVariableNode> l = method.localVariables;
		for(LocalVariableNode lvn : l)
		{
			if(lvn.name.equals("this"))
			{
				lvn.desc = "L" + name.replace('.', '/') + ";";
			}
		}
	}
	
	public static void patchInstructions(MethodNode mn, String className, String oldClassName) 
	{
		InsnList il = mn.instructions;
		AbstractInsnNode[] al = il.toArray();
		for(AbstractInsnNode ain : al)
		{
			if(ain instanceof MethodInsnNode)
			{
				MethodInsnNode min = (MethodInsnNode)ain;
				System.out.println(min.owner);
				if(min.owner.equals(oldClassName))
				{
					min.owner=className.replaceAll("\\.", "/");
//					System.out.println("Patched: " + min.owner);
				}
			}
			else if(ain instanceof FieldInsnNode)
			{
				FieldInsnNode fin = (FieldInsnNode)ain;
				if(fin.owner.equals(oldClassName))
				{
					fin.owner=className.replaceAll("\\.", "/");
//					System.out.println("Patched: " + fin.owner);
				}
			}
		}
	}

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
		InputStream initialStream = TestTransformer.class.getClassLoader().getResourceAsStream(inputStream);
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
	 * add a method no obfuscated checks you have to do that yourself if you got a deob compiled class
	 * no checks for patching the local variables nor the instructions
	 */
	public static MethodNode addMethod(ClassNode classNode, String name, String inputStream, String method_name, String descriptor) throws IOException 
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
	public static void removeMethod(ClassNode classNode, String name, String inputStream, String method_name, String descriptor) throws IOException
	{
		MethodNode method = getCachedMethodNode(inputStream, method_name, descriptor);
		if(method != null)
		{
			System.out.println("removing method:" + method_name);
			classNode.methods.remove(method);
		}
	}
}
