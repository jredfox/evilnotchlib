package com.evilnotch.lib.asm.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.util.JavaUtil;

public class ASMHelper 
{	
	public static ThreadLocal<HashMap<String, ClassNode>> cacheNodes = ThreadLocal.withInitial(()-> new HashMap());
	
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
	
	public static void patchMethod(MethodNode node,String className,String oldClassName)
	{
		patchMethod(node,className,oldClassName,false);
	}
	
	/**
	 * patch a method you can call this directly after replacing it
	 */
	public static void patchMethod(MethodNode node,String className,String oldClassName,boolean patchStatic)
	{
		patchInstructions(node, className, oldClassName,patchStatic);
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
		if(cacheNodes.get().containsKey(inputStream))
		{
			ClassNode node = cacheNodes.get().get(inputStream);
			return getMethodNode(node,obMethod,method_desc);
		}
		InputStream stream = ASMHelper.class.getClassLoader().getResourceAsStream(inputStream);
		ClassNode node = getClassNode(stream);
		cacheNodes.get().put(inputStream, node);
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
	public static ClassNode getClassNode(byte[] newbyte)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(newbyte);
		classReader.accept(classNode,0);
		return classNode;
	}
	
	/**
	 * patch all references on the local variable table instanceof of this to a new class
	 */
	public static void patchLocals(MethodNode method, String name)
	{
		for(LocalVariableNode lvn : method.localVariables)
		{
			if(lvn.name.equals("this"))
			{
				lvn.desc = "L" + name.replace('.', '/') + ";";
				break;
			}
		}
	}
	
	/**
	 * patch previous object owner instructions to new owner with filtering out static fields/method calls
	 */
	public static void patchInstructions(MethodNode mn, String class_name, String class_old,boolean patchStatic) 
	{
		String className = class_name.replace('.', '/');
		String oldClassName = class_old.replace('.', '/');
		
		for(AbstractInsnNode ain : mn.instructions.toArray())
		{
			if(ain instanceof MethodInsnNode)
			{
				MethodInsnNode min = (MethodInsnNode)ain;
				if(min.owner.equals(oldClassName) && (!isStaticMethod(min) || patchStatic) )
				{
					min.owner = className;
				}
			}
			else if(ain instanceof FieldInsnNode)
			{
				FieldInsnNode fin = (FieldInsnNode)ain;
				if(fin.owner.equals(oldClassName) && (!isStaticFeild(fin) || patchStatic) )
				{
					fin.owner = className;
				}
			}
		}
	}
	
	/**
	 * this will determine if the node is static or not
	 */
	public static boolean isStaticMethod(MethodInsnNode min) 
	{
		int opcode = min.getOpcode();
		return Opcodes.INVOKESTATIC == opcode;
	}
	
	/**
	 *this will determine if the node is static or not
	 */
	public static boolean isStaticFeild(FieldInsnNode fin) 
	{
		int opcode = fin.getOpcode();
		return Opcodes.GETSTATIC == opcode || Opcodes.PUTSTATIC == opcode;
	}
	
	/**
	 * after editing one class call this for cleanup
	 */
	public static void clearCacheNodes() 
	{
		cacheNodes.get().clear();
	}

	/**
	 * try not to use this replacing methods adding fields is more acceptable rather then replacing classes thus throwing out other people's asm
	 * and causing mod incompatibilities
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] replaceClass(String inputStream) throws IOException 
	{
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
	public static boolean containsMethod(ClassNode classNode, String method_name, String descriptor) 
	{
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
			classNode.methods.remove(method);
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
	public static LineNumberNode getFirstInstruction(MethodNode method) 
	{
		for(AbstractInsnNode obj : method.instructions.toArray())
			if(obj instanceof LineNumberNode)
				return (LineNumberNode) obj;
		return null;
	}
	
	public static LabelNode getFirstLabelNode(MethodNode method) 
	{
		for(AbstractInsnNode obj : method.instructions.toArray())
			if(obj instanceof LabelNode)
				return (LabelNode) obj;
		return null;
	}
	
	/**
	 * Gets the Last LabelNode either before the return of the method or last label
	 */
	public static LabelNode getLastLabelNode(MethodNode method, boolean afterReturn)
	{
		AbstractInsnNode[] arr = method.instructions.toArray();
		boolean found = afterReturn;
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode ab = arr[i];
			if(!found && isReturnOpcode(ab.getOpcode()))
				found = true;
			
			if(found && ab instanceof LabelNode)
			{
				return (LabelNode) ab;
			}
		}
		return null;
	}
	
	public static AbstractInsnNode getLastInstruction(MethodNode method)
	{
		AbstractInsnNode[] arr = method.instructions.toArray();
		for(int i=arr.length-1;i>=0;i--)
		{
			AbstractInsnNode ab = arr[i];
			int opcode = ab.getOpcode();
			if(!isReturnOpcode(opcode))
			{
				return ab;
			}
		}
		return null;
	}
	
	public static boolean isReturnOpcode(int opcode)
	{
		return opcode == Opcodes.RETURN || opcode == Opcodes.ARETURN || opcode == Opcodes.DRETURN || opcode == Opcodes.FRETURN || opcode == Opcodes.IRETURN || opcode == Opcodes.LRETURN;
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
	public static MethodNode getConstructionNode(ClassNode classNode, String desc) 
	{
		return getMethodNode(classNode, "<init>", desc);
	}
	
	/**
	 * helpful for finding injection point to the end of constructors
	 */
	public static AbstractInsnNode getLastPutField(MethodNode mn) 
	{
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
	
	/**
	 * add a brand new method node into the classNode
	 */
	public static void addMethodNodeIf(ClassNode classNode,int opcode, String name, String desc) 
	{
		if(containsMethod(classNode, name, desc))
		{
			System.out.println("returing class has method already!" + name + "," + desc);
			return;
		}
		MethodNode node = new MethodNode(opcode, name, desc, null, null);
		classNode.methods.add(node);
	}
	
	/**
	 * get a local variable index by it's owner name
	 */
	public static int getLocalVarIndexFromOwner(MethodNode method, String owner) 
	{
		for(LocalVariableNode node : method.localVariables)
		{
			if(node.desc.equals(owner))
				return node.index;
		}
		return -1;
	}
	
	public static String toString(FieldNode node) 
	{
		return node.name + " desc:" + node.desc + " signature:" + node.signature + " access:" + node.access;
 	}
	
	public static String toString(MethodNode node) 
	{
		return node.name + " desc:" + node.desc + " signature:" + node.signature + " access:" + node.access;
 	}

	public static MethodInsnNode getLastMethodInsn(MethodNode node, int opcode, String owner, String name, String desc, boolean isInterface) 
	{
		MethodInsnNode compare = new MethodInsnNode(opcode,owner,name,desc,isInterface);
		AbstractInsnNode[] list = node.instructions.toArray();
		for(int i=list.length-1;i>=0;i--)
		{
			AbstractInsnNode ab = list[i];
			if(ab.getOpcode() == opcode && ab instanceof MethodInsnNode && equals(compare, (MethodInsnNode)ab) )
			{
				return (MethodInsnNode)ab;
			}
		}
		return null;
	}
	
	public static MethodInsnNode getFirstMethodInsn(MethodNode node, int opcode, String owner, String name, String desc, boolean isInterface) 
	{
		MethodInsnNode compare = new MethodInsnNode(opcode,owner,name,desc,isInterface);
		for(AbstractInsnNode ab : node.instructions.toArray())
		{
			if(ab.getOpcode() == opcode && ab instanceof MethodInsnNode && equals(compare, (MethodInsnNode)ab) )
			{
				return (MethodInsnNode)ab;
			}
		}
		return null;
	}
	
	public static boolean equals(MethodInsnNode obj1, MethodInsnNode obj2)
	{
		return obj1.getOpcode() == obj2.getOpcode() && obj1.name.equals(obj2.name) && obj1.desc.equals(obj2.desc) && obj1.owner.equals(obj2.owner) && obj1.itf == obj2.itf;
	}
	
	public static boolean equals(FieldInsnNode obj1, FieldInsnNode obj2)
	{
		return obj1.getOpcode() == obj2.getOpcode() && obj1.name.equals(obj2.name) && obj1.desc.equals(obj2.desc) && obj1.owner.equals(obj2.owner);
	}
	
	public static boolean equals(FieldNode obj1, FieldNode obj2)
	{
		return obj1.name.equals(obj2.name) && obj1.desc.equals(obj2.desc);
	}
	
	public static boolean equals(MethodNode obj1, MethodNode obj2)
	{
		return obj1.name.equals(obj2.name) && obj1.desc.equals(obj2.desc);
	}
	
	public static boolean equals(TypeInsnNode compare, TypeInsnNode ab) 
	{
		return compare.getOpcode() == ab.getOpcode() && compare.desc.equals(ab.desc);
	}
	
	public static boolean equals(LdcInsnNode compare, LdcInsnNode ab) 
	{
		return compare.cst.equals(ab.cst);
	}
	
	public static boolean equals(VarInsnNode obj1, VarInsnNode obj2)
	{
		return obj1.getOpcode() == obj2.getOpcode() && obj1.var == obj2.var;
	}
	
	/**
	 * dumps a file from memory
	 */
	public static void dumpFile(String name, ClassWriter classWriter) throws IOException 
	{
		dumpFile(name,classWriter.toByteArray());
	}
	
	/**
	 * dumps a file from memory
	 */
	public static void dumpFile(String name, byte[] bytes) throws IOException 
	{
    	name = name.replace('.', '/');
    	File f = new File(System.getProperty("user.dir") + "/asm/dumps/" + name + ".class");
    	f.getParentFile().mkdirs();
    	FileUtils.writeByteArrayToFile(f, bytes);
	}
	
	public static String getTypeForClass(final Class c)
	{
	    if(c.isPrimitive())
	    {
	        if(c==byte.class)
	            return "B";
	        if(c==char.class)
	            return "C";
	        if(c==double.class)
	            return "D";
	        if(c==float.class)
	            return "F";
	        if(c==int.class)
	            return "I";
	        if(c==long.class)
	            return "J";
	        if(c==short.class)
	            return "S";
	        if(c==boolean.class)
	            return "Z";
	        if(c==void.class)
	            return "V";
	        throw new RuntimeException("Unrecognized primitive "+c);
	    }
	    if(c.isArray()) return c.getName().replace('.', '/');
	    return ('L'+c.getName()+';').replace('.', '/');
	}

	public static String getMethodDescriptor(Class clazz, String name, Class... params)
	{
		Method m = ReflectionUtil.getMethod(clazz, name, params);
		if(m == null)
			return null;
	    String s = "(";
	    for(final Class c:(m.getParameterTypes()))
	        s+=getTypeForClass(c);
	    s+=')';
	    return s+getTypeForClass(m.getReturnType());
	}
	
	public static MethodInsnNode getMethodInsnNode(MethodNode node, int opcode, String owner, String name, String desc, boolean itf)
	{
		AbstractInsnNode[] arr = node.instructions.toArray();
		MethodInsnNode compare = new MethodInsnNode(opcode, owner, name, desc, itf);
		for(AbstractInsnNode ab : arr)
		{
			if(ab instanceof MethodInsnNode)
			{
				if(ASMHelper.equals(compare, (MethodInsnNode)ab))
				{
					return (MethodInsnNode)ab;
				}
			}
		}
		return null;
	}
	
	public static FieldInsnNode getFieldNode(MethodNode node, int opcode, String owner, String name, String desc)
	{
		AbstractInsnNode[] arr = node.instructions.toArray();
		FieldInsnNode compare = new FieldInsnNode(opcode, owner, name, desc);
		for(AbstractInsnNode ab : arr)
		{
			if(ab instanceof FieldInsnNode && ASMHelper.equals(compare, (FieldInsnNode)ab))
			{
				return (FieldInsnNode)ab;
			}
		}
		return null;
	}

	public static TypeInsnNode getTypeInsnNode(MethodNode node, TypeInsnNode compare)
	{
		AbstractInsnNode[] arr = node.instructions.toArray();
		for(AbstractInsnNode ab : arr)
		{
			if(ab instanceof TypeInsnNode && ASMHelper.equals(compare, (TypeInsnNode)ab))
			{
				return (TypeInsnNode)ab;
			}
		}
		return null;
	}

	public static LdcInsnNode getLdcInsnNode(MethodNode node, LdcInsnNode compare) 
	{
		AbstractInsnNode[] arr = node.instructions.toArray();
		for(AbstractInsnNode ab : arr)
		{
			if(ab instanceof LdcInsnNode && ASMHelper.equals(compare, (LdcInsnNode)ab))
			{
				return (LdcInsnNode)ab;
			}
		}
		return null;
	}

	/**
	 * Gets the previous LineNumberNode or LabelNode whichever comes first
	 */
	public static AbstractInsnNode previousLabel(AbstractInsnNode f)
	{
		AbstractInsnNode current = f;
		while(current != null)
		{
			current = current.getPrevious();
			if(current instanceof LineNumberNode || current instanceof LabelNode)
			{
				return current;
			}
		}
		return null;
	}
	
	/**
	 * Gets the previous LineNumberNode or LabelNode whichever comes first
	 */
	public static LabelNode prevLabelR(AbstractInsnNode f)
	{
		AbstractInsnNode current = f;
		while(current != null)
		{
			current = current.getPrevious();
			if(current instanceof LabelNode)
			{
				return (LabelNode) current;
			}
		}
		return null;
	}

	public static VarInsnNode getVarInsnNode(MethodNode node, VarInsnNode compare) 
	{
		AbstractInsnNode[] arr = node.instructions.toArray();
		for(AbstractInsnNode ab : arr)
		{
			if(ab instanceof VarInsnNode && ASMHelper.equals(compare, (VarInsnNode)ab))
			{
				return (VarInsnNode)ab;
			}
		}
		return null;
	}

	public static JumpInsnNode nextJumpInsnNode(AbstractInsnNode pretarg) 
	{
		AbstractInsnNode ab = pretarg;
		while(ab != null)
		{
			ab = pretarg.getNext();
			if(ab instanceof JumpInsnNode)
				return (JumpInsnNode) ab;
		}
		return null;
	}

	public static boolean containsFieldNode(ClassNode classNode, String name) 
	{
		for(FieldNode f : classNode.fields)
			if(f.name.equals(name))
				return true;
		return false;
	}
	
	public static void addFieldNodeIf(ClassNode classNode, FieldNode feild) 
	{
		for(FieldNode f : classNode.fields)
			if(f.name.equals(feild.name))
				return;
		classNode.fields.add(feild);
	}
	
	public static boolean hasFieldNode(ClassNode classNode, String name)
	{
		for(FieldNode f : classNode.fields)
			if(f.name.equals(name))
				return true;
		return false;
	}
	
	public static void toFile(byte[] data, File f)
	{
		f.getParentFile().mkdirs();
		
    	InputStream in = null;
    	OutputStream out = null;
    	try
    	{
    		in = new ByteArrayInputStream(data);
    		out = new FileOutputStream(f);
    		JavaUtil.copy(in, out, false);
    	}
    	catch(Throwable e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		JavaUtil.closeQuietly(in);
    		JavaUtil.closeQuietly(out);
    	}
	}

	public static FieldInsnNode getFieldInsnNode(MethodNode node, int opcode, String owner, String name, String desc)
	{
		AbstractInsnNode[] arr = node.instructions.toArray();
		FieldInsnNode compare = new FieldInsnNode(opcode, owner, name, desc);
		for(AbstractInsnNode ab : arr)
		{
			if(ab instanceof FieldInsnNode && equals(compare, (FieldInsnNode)ab))
			{
				return (FieldInsnNode)ab;
			}
		}
		return null;
	}

	public static ClassWriter getClassWriter(ClassNode classNode, int flags) 
	{
        ClassWriter classWriter = new ClassWriter(flags);
        classNode.accept(classWriter);
        return classWriter;
	}
	
	public static byte[] toByteArray(ClassWriter classWriter, String transformedName) throws IOException 
	{
        byte[] bytes = classWriter.toByteArray();
        if(Boolean.parseBoolean(System.getProperty("asm.dump", "false")))
        	dumpFile(transformedName, bytes);
        
        return bytes;
	}
	
	public static LineNumberNode prevLabelNode(AbstractInsnNode spot) 
	{
		AbstractInsnNode n = spot;
		while(n != null)
		{
			n = n.getPrevious();
			if(n instanceof LineNumberNode)
				return (LineNumberNode) n;
		}
		return null;
	}
	
	public static void pubMinusFinal(ClassNode classNode, boolean all)
	{
		//AT the fields
		for(FieldNode f : classNode.fields)
		    f.access = pubMinusFinal(f.access);
		
		if(all)
		{
			//AT the methods
			for(MethodNode m : classNode.methods)
				m.access = pubMinusFinal(m.access);
			
			//AT the class
			classNode.access = pubMinusFinal(classNode.access);
			
			//AT the inner classes if non null
			if(classNode.innerClasses != null)
				for(InnerClassNode inner : classNode.innerClasses)
					inner.access = pubMinusFinal(inner.access);
		}
	}
	
	public static int pubMinusFinal(int access) 
	{
	    // Remove conflicting access modifiers
	    access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
	    
	    // Remove the final modifier
	    access &= ~Opcodes.ACC_FINAL;
	    
	    // Set the public modifier
	    access |= Opcodes.ACC_PUBLIC;
	    
	    return access;
	}
	
	/**
	 * Dummy Method to stop forge from breaking on macOS
	 */
	public static void setResizable(boolean resizeable) {}
	
	
	/**
	 * Export Methods from a Class to a File for ASM Later without Literally Copying the Entire Previous Class
	 */
    public static void exportMethods(ClassNode orgClassNode, String path, MethodNode... methodNode) throws IOException 
    {
        // Step 1: Create a new ClassNode with minimal structure
        ClassNode classNode = new ClassNode();
        classNode.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, orgClassNode.name, orgClassNode.signature, orgClassNode.superName, JavaUtil.toStaticStringArray(orgClassNode.interfaces));

        // Step 2: Add the method to the class node
        for(MethodNode m : methodNode)
        	classNode.methods.add(m);

        // Step 3: Use ClassWriter to write the class bytecode
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);

        ASMHelper.dumpFile(path + (FMLCorePlugin.isObf ? "_srg" : ""), classWriter.toByteArray());
    }
	
	public static MethodInsnNode nextMethodInsnNode(AbstractInsnNode pretarg, int opcode, String owner, String name, String desc, boolean itf) 
	{
		MethodInsnNode look = new MethodInsnNode(opcode, owner, name, desc, itf);
		AbstractInsnNode ab = pretarg;
		while(ab != null)
		{
			ab = ab.getNext();
			if(ab instanceof MethodInsnNode && equals(look, (MethodInsnNode) ab))
				return (MethodInsnNode) ab;
		}
		return null;
	}
	
	public static LabelNode nextLabelR(AbstractInsnNode spot) 
	{
		AbstractInsnNode n = spot;
		while(n != null)
		{
			n = n.getNext();
			if(n instanceof LabelNode)
				return (LabelNode) n;
		}
		return null;
	}
}
