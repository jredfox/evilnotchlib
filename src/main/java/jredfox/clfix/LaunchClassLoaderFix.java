package jredfox.clfix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fix LaunchClassLoader Memory Leaks Supports launchwrapper 1.3 - 1.12
 * V2.0.0 Is alot more robust then V1.0.0 In EvilNotchLib as it handles all possible ClassLoaders & Shadow Variables
 * @author jredfox
 */
public class LaunchClassLoaderFix {
	
	/**
	 * EvilNotchLib's Changes
	 *  - Changed {@link #patchCachedClasses} to true by default unless it gets overriden in the config
	 *  - Removed LaunchWrapperTransformer.class as it's not used for EvilNotchLib(still inside dpi-fix)
	 *  - Removed FindClass.class as it's not used for EvilnotchLib(still in dpi-fix)
	 *  
	 * ChangeLog 2.1.0:
	 * - Fixed {@link System#identityHashCode(Object)} collisions resulted in not setting class loader. object hashcode no longer represents the address and is no longer guaranteed since java 8 to be even unique per object instance
	 * - Fixed Technic's resources and pngMap memory leak in LaunchWrapperTransformer only.
	 * - Fixed Verify not working for non instances of LaunchClassLoader
	 * - Fixed Not Supporting Parent ClassLoaders on {@link #stopMemoryOverflow(ClassLoader)} and {@link #verify(ClassLoader)}
	 * - Fixed ClassLoader weirdness causing unintended behavior for {@link #stopMemoryOverflow(ClassLoader)} and {@link #verify(ClassLoader)}
	 * - Fixed Patching LaunchClassLoader#cachedClasses when it wasn't safe to do so MC < 1.12.2! This resulted in ClassNotFoundException caused by a duplicate class exception
	 * - Added Support for more Library ClassLoaders to stop the while loop from
	 * - Added -Dclfix.strictMode {@link #strictMode} when true we only apply the patches to LaunchClassLoader If your using it with DPI-FIX mod you can simply use the config
	 * - Added -Dclfix.patchCachedClasses when true patches LaunchClassLoader or a Custom Class Loader's Field of cachedClasses. It's disabled by default but will get turned on by DPI-Fix or EvilNotchLib in MC 1.12.2
	 */
	public static final String VERSION = "2.1.0";
	
	/**
	 * When true only allows patching of LaunchClassLoader instances regardless of memory leaks of the other custom class loaders that are not libraries nor blacklisted
	 */
	public static boolean strictMode = Boolean.parseBoolean(System.getProperty("clfix.strictMode", "false"));
	/**
	 * When true allows patching of cachedClasses for LaunchClassLoader or any class loader if strict mode isn't on
	 */
	public static boolean patchCachedClasses = Boolean.parseBoolean(System.getProperty("clfix.patchCachedClasses", "true"));
	
	private static String[] libLoaders = new String[]
	{
		"java.",
		"sun.",
		"com.sun.",
		"jdk.",
		"javax."
	};
	
	public static boolean isLibClassLoader(String[] libs, String name) 
	{
		for(String lib : libs)
			if(name.startsWith(lib))
				return true;
		return false;
	}
	
	/**
	 * can be called at any time
	 */
	public static void stopMemoryOverflow(ClassLoader clforge)
	{
		try
		{
			Class launch = forName("net.minecraft.launchwrapper.Launch");
			if(strictMode && launch == null)
			{
				System.err.println("LaunchWrapper is Missing!");
				return;
			}
			
			boolean cache = patchCachedClasses;
			Set<ClassLoader> allLoaders = getAllClassLoaders(launch, clforge);
			for(ClassLoader cl : allLoaders)
			{
				System.out.println("Fixing RAM Leak of:" + cl);
				
				//Support Shadow Variables for Dumb Mods Replacing Launch#classLoader
				Class actualClassLoader = cl.getClass();
				do
				{
					if(cache)
						setDummyMap(cl, actualClassLoader, "cachedClasses");
					setDummyMap(cl, actualClassLoader, "resourceCache");
					setDummyMap(cl, actualClassLoader, "packageManifests");
					setDummySet(cl, actualClassLoader, "negativeResourceCache");
					if(actualClassLoader.getName().equals("net.minecraft.launchwrapper.LaunchClassLoader"))
						break;//Regardless of what LaunchClassLoader extends break after as we are done
					actualClassLoader = actualClassLoader.getSuperclass();
				}
				while(actualClassLoader != null && !isLibClassLoader(libLoaders, actualClassLoader.getName()) );
			}
		}
		catch(Throwable t)
		{
			System.err.println("FATAL ERROR HAS OCCURED PATCHING THE LaunchClassLoader Memory Leaks!");
			t.printStackTrace();
		}
	}

	/**
	 * Disables FoamFix's Flawed Fix trying to Fix LaunchClassLoader RAM Leak
	 */
	public static void stopMemoryOverflowFoamFix(ClassLoader clforge)
	{
		//Disable FoamFix lwWeakenResourceCache & lwRemovePackageManifestMap for 1.8x - 1.12.2
		Class foamShared = forName("pl.asie.foamfix.shared.FoamFixShared");
		Class foamBF = forName("pl.asie.foamfix.bugfixmod.coremod.BugfixModClassTransformer");
		if(foamShared != null)
		{
			try
			{
				System.out.println("Disabling FoamFix's \"Fix\" for LaunchClassLoader!");
				Object finstance = getPrivate(null, foamShared, "config");
				Class foamFixConfig = forName("pl.asie.foamfix.shared.FoamFixConfig");
				setPrivate(finstance, false, foamFixConfig, "lwWeakenResourceCache");
				setPrivate(finstance, false, foamFixConfig, "lwRemovePackageManifestMap");
				
				//Forces foamfix.cfg to be created
				Class foamCfgClazz = forName("pl.asie.foamfix.shared.FoamFixConfig");
				File foamCfgFile = new File("config", "foamfix.cfg");
				Object instance = foamCfgClazz.newInstance();
				Method init = foamCfgClazz.getDeclaredMethod("init", File.class, boolean.class);
				if(init == null)
					init = foamCfgClazz.getDeclaredMethod("init", File.class, Boolean.class);
				init.setAccessible(true);
				init.invoke(instance, foamCfgFile, true);
				
				//Disable their fix
				Object[] lines = getFileLines(foamCfgFile, true).toArray();
				for(int i=0;i<lines.length;i++)
					lines[i] = ((String) lines[i]).replace("removePackageManifestMap=true", "removePackageManifestMap=false").replace("weakenResourceCache=true", "weakenResourceCache=false");
				saveFileLines(lines, foamCfgFile);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		//Support 1.7.10 FoamFix BS
		else if(foamBF != null)
		{
			System.err.println("FoamFix for MC 1.7.10 has been found. FoamFix is not needed for 1.7.10 and actually causes graphical bugs. Please Use Optifine instead");
			try
			{
				Field fieldInstance = null;
				Field fieldSettings = null;
				for(Field f : foamBF.getDeclaredFields())
				{
					String name = f.getName();
					if(name.equalsIgnoreCase("instance"))
						fieldInstance = f;
					else if(name.equalsIgnoreCase("settings"))
						fieldSettings = f;
				}
				fixFields(fieldInstance, fieldSettings);
				
				Object instance = fieldInstance.get(null);
				Object settingsInstance = fieldSettings.get(instance);
				Class settingsClazz = settingsInstance.getClass();
				Field rc = settingsClazz.getDeclaredField("lwWeakenResourceCache");
				Field pkg = settingsClazz.getDeclaredField("lwRemovePackageManifestMap");
				fixFields(rc, pkg);
				rc.set(settingsInstance, false);
				pkg.set(settingsInstance, false);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		
		//StopMemoryOverflow just in case
		stopMemoryOverflow(clforge);
	}
	
	/**
	 * Verifies that LaunchClassLoader Map / Set are instances of the Dummy Version.
	 */
	public static void verify(ClassLoader clforge)
	{
		try
		{
			Class launch = forName("net.minecraft.launchwrapper.Launch");
			if(strictMode && launch == null)
				return;
			boolean cache = patchCachedClasses;
			Set<ClassLoader> allLoaders = getAllClassLoaders(launch, clforge);
			for(ClassLoader classLoader : allLoaders)
			{
				System.out.println("Verifying ClassLoader:" + classLoader);
				
				Class actualClazz = classLoader.getClass();
				String actualName = actualClazz.getName();
				
				do
				{
					Map cachedClasses = cache ? ((Map) getPrivate(classLoader, actualClazz, "cachedClasses")) : null;
					Map resourceCache = (Map) getPrivate(classLoader, actualClazz, "resourceCache");
					Map packageManifests = (Map) getPrivate(classLoader, actualClazz, "packageManifests");
					Set negativeResourceCache = (Set) getPrivate(classLoader, actualClazz, "negativeResourceCache");
					boolean flag = actualName.equals("net.minecraft.launchwrapper.LaunchClassLoader");
					
					if(cachedClasses != null && !(cachedClasses instanceof DummyMap))
						System.err.println((flag ? "LaunchClassLoader" : actualName) + "#cachedClasses is Unoptimized! size:" + cachedClasses.size() + " Class:" + cachedClasses.getClass());
					if(resourceCache != null && !(resourceCache instanceof DummyMap))
						System.err.println((flag ? "LaunchClassLoader" : actualName) + "#resourceCache is Unoptimized! size:" + resourceCache.size() + " Class:" + resourceCache.getClass());
					if(packageManifests != null && !(packageManifests instanceof DummyMap))
						System.err.println((flag ? "LaunchClassLoader" : actualName) + "#packageManifests is Unoptimized! size:" + packageManifests.size() + " Class:" + packageManifests.getClass());
					if(negativeResourceCache != null && !(negativeResourceCache instanceof DummySet))
						System.err.println((flag ? "LaunchClassLoader" : actualName) + "#negativeResourceCache is Unoptimized! size:" + negativeResourceCache.size() + " Class:" + negativeResourceCache.getClass());
					
					if(flag)
						break;
					actualClazz = actualClazz.getSuperclass();
					if(actualClazz != null)
						actualName = actualClazz.getName();
				}
				while(actualClazz != null && !isLibClassLoader(libLoaders, actualName));
			}
		}
		catch(Throwable t)
		{
			System.err.println("FATAL ERROR HAS OCCURED VERIFYING THE LaunchClassLoader Memory Leaks Was Fixed!");
			t.printStackTrace();
		}
	}

	public static Set<ClassLoader> getAllClassLoaders(Class launch, ClassLoader clforge) 
	{
		Set<ClassLoader> l = getClassLoaders(launch, clforge);
		Set<ClassLoader> allLoaders = Collections.newSetFromMap(new IdentityHashMap(16));
		for(ClassLoader root : l)
			allLoaders.addAll(getParents(root));
		return allLoaders;
	}

	/**
	 * Gets a 1D array of Parent ClassLoaders & Itself Excluding RelaunchClassLoader and Technic's MinecraftClassLoader or Libraries
	 * Unless {@link #strictMode} is true then it must be an instanceof LaunchClassLoader and checks it with string names to ensure it doesn't return false when it's true
	 */
	public static Set<ClassLoader> getParents(ClassLoader root) 
	{
		Set<ClassLoader> loaders = Collections.newSetFromMap(new IdentityHashMap(8));
		ClassLoader cl = root;
		String[] strict = new String[]{"net.minecraft.launchwrapper.LaunchClassLoader"};
		boolean strictMode = LaunchClassLoaderFix.strictMode;
		boolean first = true;
		do
		{
			Class clClazz = cl.getClass();
			if(strictMode && instanceOf(strict, clClazz) || !strictMode && (first || !isLibClassLoader(libLoaders, clClazz.getName())))
				loaders.add(cl);
			first = false;
			ClassLoader parent = (ClassLoader) getPrivate(cl, clClazz, "parent");
			cl = (parent != null && !loaders.contains(parent)) ? parent : cl.getParent();
		}
		while(cl != null && !loaders.contains(cl));
		
		return loaders;
	}
	
	/**
	 * @return true when the compared class is the base class or extends it
	 * @WARNING: doesn't support interfaces
	 */
	public static boolean instanceOf(String[] clazzes, Class c) 
	{
		while(c != null)
		{
			String name = c.getName();
			for(String base : clazzes)
				if(base.equals(name))
					return true;
			c = c.getSuperclass();
		}
		return false;
	}

	private static void setDummyMap(Object classLoader, Class clazzLoaderClazz, String mapName)
	{
		Map init = (Map) getPrivate(classLoader, clazzLoaderClazz, mapName);
		if(init == null) 
		{
			System.err.println(clazzLoaderClazz.getName() + "#" + mapName + " is missing!");
			return;
		}
		init.clear();
		setPrivate(classLoader, new DummyMap(), clazzLoaderClazz, mapName);
	}

	private static void setDummySet(Object classLoader, Class clazzLoaderClazz, String setName)
	{
		Set init = (Set) getPrivate(classLoader, clazzLoaderClazz, setName);
		if(init == null)
		{
			System.err.println(clazzLoaderClazz.getName() + "#" + setName + " is missing!");
			return;
		}
		init.clear();
		setPrivate(classLoader, new DummySet(), clazzLoaderClazz, setName);
	}
	
	public static Set<ClassLoader> getClassLoaders(Class launch, ClassLoader clforge) 
	{
		Set<ClassLoader> loaders = Collections.newSetFromMap(new IdentityHashMap(5));
		ClassLoader classLoader = launch != null ? ((ClassLoader) getPrivate(null, launch, "classLoader", false)) : null;
		ClassLoader currentLoader = LaunchClassLoaderFix.class.getClassLoader();
		ClassLoader contextLoader = getContextClassLoader();
		
		if(classLoader != null)
			loaders.add(classLoader);
		if(clforge != null)
			loaders.add(clforge);
		if(currentLoader != null)
			loaders.add(currentLoader);
		if(contextLoader != null)
			loaders.add(contextLoader);
		
		return loaders;
	}
	
	public static ClassLoader getContextClassLoader() 
	{
		try
		{
			return Thread.currentThread().getContextClassLoader();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		return null;
	}
	
	public static Field modifiersField;
	static
	{
		try
		{
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	private static void fixFields(Field... fields) throws IllegalArgumentException, IllegalAccessException
	{
		for(Field f : fields) 
		{
			f.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
		}
	}

	public static Object getPrivate(Class clazz, String field) {
		return getPrivate(null, clazz, field);
	}
	
	public static Object getPrivate(Object instance, Class<?> clazz, String name) {
		return getPrivate(instance, clazz, name, true);
	}
	
	public static Object getPrivate(Object instance, Class<?> clazz, String name, boolean print)
	{
		try
		{
	    	Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			return f.get(instance);
		}
		catch(NoSuchFieldException e)
		{
			
		}
		catch(Throwable t)
		{
			if(print)
				t.printStackTrace();
		}
        return null;
	}
	
	public static void setPrivate(Object instance, Object toset, Class clazz, String name)
	{
		try
		{
	    	Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			f.set(instance, toset);
		}
		catch(NoSuchFieldException e)
		{
			
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	
    public static <T> Class<T> forName(String className)
    {
    	try
    	{
    		return (Class<T>) Class.forName(className);
    	}
    	catch(ClassNotFoundException e)
    	{
    		
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
    
    public static boolean instanceOf(Class base, Class compare)
    {
    	return base.isAssignableFrom(compare);
    }
    
    public static boolean instanceOf(Class base, Object obj)
    {
    	return base.isInstance(obj);
    }
    
	public static void saveFileLines(Object[] list,File f)
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), Charset.forName("UTF-8") ) );
			for(Object s : list)
				writer.write(s + lineSeparator());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if(writer != null)
					writer.close();
			}
			catch (IOException e)
			{
				
			}
		}
	}
    
	/**
	 * Equivalent to Files.readAllLines() but, works way faster
	 */
	public static List<String> getFileLines(File f,boolean utf8)
	{
		BufferedReader reader = null;
		List<String> list = null;
		try
		{
			if(!utf8)
			{
				reader = new BufferedReader(new FileReader(f));//says it's utf-8 but, the jvm actually specifies it even though the lang settings in a game might be different
			}
			else
			{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), Charset.forName("UTF-8") ) );
			}
			
			list = new ArrayList();
			String s = reader.readLine();
			
			if(s != null)
			{
				list.add(s);
			}
			
			while(s != null)
			{
				s = reader.readLine();
				if(s != null)
				{
					list.add(s);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				try 
				{
					reader.close();
				} catch (IOException e) 
				{
					System.err.println("Unable to Close InputStream this is bad");
				}
			}
		}
		
		return list;
	}
	
	public static String lineSeparator()
	{
		return System.getProperty("java.version").replace("'", "").replace("\"", "").trim().startsWith("1.6.") ? System.getProperty("line.separator") : System.lineSeparator();
	}

}
