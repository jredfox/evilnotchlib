package jredfox.clfix;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.evilnotch.lib.asm.util.ASMHelper;


public class LaunchWrapperTransformer implements ClassFileTransformer {
	
	public static File lcl = new File(System.getProperty("user.dir"), "asm/cache/dpi-fix/net/minecraft/launchwrapper/LaunchClassLoader.class").getAbsoluteFile();
	public static File dm = new File(System.getProperty("user.dir"), "asm/cache/dpi-fix/jredfox/clfix/DummyMap.class").getAbsoluteFile();
	public static File ds = new File(System.getProperty("user.dir"), "asm/cache/dpi-fix/jredfox/clfix/DummySet.class").getAbsoluteFile();
	public static File mcl = new File(System.getProperty("user.dir"), "asm/cache/dpi-fix/net/technicpack/legacywrapper/MinecraftClassLoader.class").getAbsoluteFile();
	public static boolean pcc; //= Boolean.parseBoolean(System.getProperty("clfixtransformer.cc", "false"));
	
	public static void init(Instrumentation inst)
	{
		lcl.delete();
		dm.delete();
		ds.delete();
		mcl.delete();
		pcc = LaunchClassLoaderFix.patchCachedClasses;
		inst.addTransformer(new LaunchWrapperTransformer());
		LaunchClassLoaderFix.forName("net.minecraft.launchwrapper.LaunchClassLoader");//Force Load LaunchClassLoader Class
		LaunchClassLoaderFix.forName("jredfox.clfix.DummyMap");//Force Load DummyMap
		LaunchClassLoaderFix.forName("jredfox.clfix.DummySet");//Force Load DummySet
		LaunchClassLoaderFix.forName("net.technicpack.legacywrapper.MinecraftClassLoader");//Force Load Technic's MinecraftClassLoader Class
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] bytes)
	{
		//Cache DummyMap / DummySet to prevent ClassNotFoundException If the Java Agent Gets Removed from the Class Path
		if(className.equals("jredfox/clfix/DummyMap"))
		{
			if(bytes == null && dm.exists())
				return toByteArray(dm);
			else if(bytes != null)
				ASMHelper.toFile(bytes, dm);
		}
		else if(className.equals("jredfox/clfix/DummySet"))
		{
			if(bytes == null && ds.exists())
				return toByteArray(ds);
			else if(bytes != null)
				ASMHelper.toFile(bytes, ds);
		}
		
		if(bytes == null)
			return null;
		
		if(className.equals("net/minecraft/launchwrapper/LaunchClassLoader"))
		{
			try
			{
				System.out.println("Transforming " + className.replace("/", ".") + " to fix memory leak");
				
				//Return the cached file if it exists
				if(lcl.exists())
					return toByteArray(lcl);
				
				ClassNode classNode = ASMHelper.getClassNode(bytes);
				ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "dm", "Ljava/util/Map;", null, null));
				ASMHelper.addFieldNodeIf(classNode, new FieldNode(Opcodes.ACC_PUBLIC, "ds", "Ljava/util/Set;", null, null));
				boolean pcc = LaunchWrapperTransformer.pcc;
				
				MethodNode m = ASMHelper.getMethodNode(classNode, "<init>", "([Ljava/net/URL;)V");
				
				//public Map dm = new DummyMap();
			    //public Set ds = new DummySet();
				InsnList l = new InsnList();
				l.add(new VarInsnNode(Opcodes.ALOAD, 0));
				l.add(new TypeInsnNode(Opcodes.NEW, "jredfox/clfix/DummyMap"));
				l.add(new InsnNode(Opcodes.DUP));
				l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "jredfox/clfix/DummyMap", "<init>", "()V", false));
				l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "dm", "Ljava/util/Map;"));
				l.add(new LabelNode());
				l.add(new VarInsnNode(Opcodes.ALOAD, 0));
				l.add(new TypeInsnNode(Opcodes.NEW, "jredfox/clfix/DummySet"));
				l.add(new InsnNode(Opcodes.DUP));
				l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "jredfox/clfix/DummySet", "<init>", "()V", false));
				l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "ds", "Ljava/util/Set;"));
				
		        //cachedClasses = new DummyMap();
		        //resourceCache = new DummyMap();
		        //packageManifests = new DummyMap();
		        //negativeResourceCache = new DummySet();
				if(pcc && ASMHelper.hasFieldNode(classNode, "cachedClasses"))
				{
					l.add(new LabelNode());
					l.add(new VarInsnNode(Opcodes.ALOAD, 0));
					l.add(new TypeInsnNode(Opcodes.NEW, "jredfox/clfix/DummyMap"));
					l.add(new InsnNode(Opcodes.DUP));
					l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "jredfox/clfix/DummyMap", "<init>", "()V", false));
					l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "cachedClasses", "Ljava/util/Map;"));
				}
				if(ASMHelper.hasFieldNode(classNode, "resourceCache"))
				{
					l.add(new LabelNode());
					l.add(new VarInsnNode(Opcodes.ALOAD, 0));
					l.add(new TypeInsnNode(Opcodes.NEW, "jredfox/clfix/DummyMap"));
					l.add(new InsnNode(Opcodes.DUP));
					l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "jredfox/clfix/DummyMap", "<init>", "()V", false));
					l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "resourceCache", "Ljava/util/Map;"));
				}
				if(ASMHelper.hasFieldNode(classNode, "packageManifests"))
				{
					l.add(new LabelNode());
					l.add(new VarInsnNode(Opcodes.ALOAD, 0));
					l.add(new TypeInsnNode(Opcodes.NEW, "jredfox/clfix/DummyMap"));
					l.add(new InsnNode(Opcodes.DUP));
					l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "jredfox/clfix/DummyMap", "<init>", "()V", false));
					l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "packageManifests", "Ljava/util/Map;"));
				}
				if(ASMHelper.hasFieldNode(classNode, "negativeResourceCache"))
				{
					l.add(new LabelNode());
					l.add(new VarInsnNode(Opcodes.ALOAD, 0));
					l.add(new TypeInsnNode(Opcodes.NEW, "jredfox/clfix/DummySet"));
					l.add(new InsnNode(Opcodes.DUP));
					l.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "jredfox/clfix/DummySet", "<init>", "()V", false));
					l.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "negativeResourceCache", "Ljava/util/Set;"));
				}
				l.add(new LabelNode());
				
				//Inject Before Thread#currentThread#setContextClassLoader and if not found launchwrapper 1.12 for example then inject before this#addClassLoaderExclusion
				MethodInsnNode targ = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "setContextClassLoader", "(Ljava/lang/ClassLoader;)V", false);
				if(targ == null)
					targ = ASMHelper.getMethodInsnNode(m, Opcodes.INVOKEVIRTUAL, "net/minecraft/launchwrapper/LaunchClassLoader", "addClassLoaderExclusion", "(Ljava/lang/String;)V", false);
				m.instructions.insert(ASMHelper.prevLabelNode(targ), l);
				
				//Transform Entire Class to call dm (dummy map) ds (dummy set) so no matter what foamfix or another mod does with reflection it should retain no memory leak as they won't get used
				FieldInsnNode cachedClassesInsn = new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "cachedClasses", "Ljava/util/Map;");
				FieldInsnNode packageManifestsInsn = new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "packageManifests", "Ljava/util/Map;");
				FieldInsnNode resourceCache = new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "resourceCache", "Ljava/util/Map;");
				FieldInsnNode negativeResourceCache = new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "negativeResourceCache", "Ljava/util/Set;");
				
				for(MethodNode method : classNode.methods)
				{
					if(!method.name.equals("<init>"))
					{
						AbstractInsnNode ab = method.instructions.getFirst();
						while(ab != null)
						{
							if(!(ab instanceof FieldInsnNode))
							{
								ab = ab.getNext();
								continue;
							}
							
							FieldInsnNode insn = (FieldInsnNode) ab;
							if(pcc && ASMHelper.equals(cachedClassesInsn, insn) || ASMHelper.equals(packageManifestsInsn, insn) || ASMHelper.equals(resourceCache, insn))
								insn.name = "dm";
							else if(ASMHelper.equals(negativeResourceCache, insn))
								insn.name = "ds";
							
							ab = ab.getNext();
						}
					}
					else
					{
						//Sets 1,000 to 0 initial capacity of resourceCache
						FieldInsnNode put = ASMHelper.getFieldInsnNode(method, Opcodes.PUTFIELD, "net/minecraft/launchwrapper/LaunchClassLoader", "resourceCache", "Ljava/util/Map;");
						if(put != null && put.getPrevious() != null && put.getPrevious().getPrevious() instanceof IntInsnNode)
						{
							IntInsnNode insn = (IntInsnNode) put.getPrevious().getPrevious();
							insn.operand = 0;
						}
					}
				}
				
				byte[] clazzBytes = ASMHelper.toByteArray(ASMHelper.getClassWriter(classNode, ClassWriter.COMPUTE_MAXS), className);
				ASMHelper.toFile(clazzBytes, lcl);
				return clazzBytes;
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		else if(className.equals("net/technicpack/legacywrapper/MinecraftClassLoader"))
		{
			try
			{
				System.out.println("Transforming " + className.replace("/", ".") + " to fix resources memory leak");
				
				if(mcl.exists())
					return toByteArray(mcl);
				
				ClassNode classNode = ASMHelper.getClassNode(bytes);
				for(MethodNode m : classNode.methods)
				{
					if(m.name.equals("<init>"))
					{
						//get the last put field before the first return
						AbstractInsnNode spot = null;
						for(AbstractInsnNode a : m.instructions.toArray())
						{
							if(a == null)
								continue;
							
							int op = a.getOpcode();
							if(op == Opcodes.PUTFIELD && a instanceof FieldInsnNode)
								spot = a;
							else if(ASMHelper.isReturnOpcode(op))
								break;
						}
						
						InsnList list = new InsnList();
						//resources = DummyMap.get();
						FieldNode resources = ASMHelper.getFieldnode(classNode, "resources");
						if(resources != null)
						{
							list.add(new VarInsnNode(Opcodes.ALOAD, 0));
							list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/clfix/DummyMap", "get", "()Ljredfox/clfix/DummyMap;", false));
							list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/technicpack/legacywrapper/MinecraftClassLoader", "resources", resources.desc));
						}
						//pngResource = DummyMap.get();
						FieldNode pngResource = ASMHelper.getFieldnode(classNode, "pngResource");
						if(pngResource != null)
						{
							list.add(new VarInsnNode(Opcodes.ALOAD, 0));
							list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jredfox/clfix/DummyMap", "get", "()Ljredfox/clfix/DummyMap;", false));
							list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/technicpack/legacywrapper/MinecraftClassLoader", "pngResource", pngResource.desc));
						}
						if(list.getFirst() != null)
							m.instructions.insert(spot, list);
					}
				}
				
				byte[] clazzBytes = ASMHelper.toByteArray(ASMHelper.getClassWriter(classNode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES), className);
				ASMHelper.toFile(clazzBytes, mcl);
				return clazzBytes;
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		
		return bytes;
	}
	
    public static byte[] toByteArray(File file)
    {
    	InputStream input = null;
        ByteArrayOutputStream output = null;
        try
        {
        	input = new FileInputStream(file);
        	output = new ByteArrayOutputStream();
        	copy(input, output);
        }
        catch(Throwable e)
        {
        	e.printStackTrace();
        }
        finally
        {
            closeQuietly(input);
            closeQuietly(output);
        }
        return output.toByteArray();
    }
    
    public static void copy(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1048576/2];
		int length;
   	 	while ((length = in.read(buffer)) >= 0)
		{
			out.write(buffer, 0, length);
		}
	}
	
    public static void closeQuietly(Closeable clos)
	{
		try 
		{
			if(clos != null)
				clos.close();
		}
		catch (IOException e)
		{
			
		}
	}

}
