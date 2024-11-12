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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.evilnotch.lib.util.JavaUtil;

/**
 * Fix LaunchClassLoader Memory Leaks Supports launchwrapper 1.3 - 1.12
 * V2.0.0 Is alot more robust then V1.0.0 In EvilNotchLib as it handles all possible ClassLoaders & Shadow Variables
 * @author jredfox
 */
public class LaunchClassLoaderFix {
	
	public static final String VERSION = "2.0.0";
	
	/**
	 * can be called at any time
	 */
	public static void stopMemoryOverflow(ClassLoader clforge)
	{
		try
		{
			Class launch = forName("net.minecraft.launchwrapper.Launch");
			if(launch == null)
			{
				System.err.println("LaunchWrapper is Missing!");
				return;
			}
			
			String clazzLoaderName = "net.minecraft.launchwrapper.LaunchClassLoader";
			Class clazzLoaderClazz = forName(clazzLoaderName);
			ClassLoader classLoader = (ClassLoader) getPrivate(null, launch, "classLoader", false);
			ClassLoader currentLoader = LaunchClassLoaderFix.class.getClassLoader();
			ClassLoader contextLoader = getContextClassLoader();
			
			Map<String, ClassLoader> loaders = new HashMap(5);
			loaders.put(toNString(classLoader), classLoader);
			loaders.put(toNString(clforge), clforge);
			loaders.put(toNString(currentLoader), currentLoader);
			loaders.put(toNString(contextLoader), contextLoader);
			for(ClassLoader cl : loaders.values())
			{
				if(cl == null)
					continue;
				System.out.println("Fixing RAM Leak of:" + cl);
				//Support Shadow Variables for Dumb Mods Replacing Launch#classLoader
				Class actualClassLoader = cl.getClass();
				boolean flag = instanceOf(clazzLoaderClazz, actualClassLoader);
				do
				{
					setDummyMap(cl, actualClassLoader, "cachedClasses");
					setDummyMap(cl, actualClassLoader, "resourceCache");
					setDummyMap(cl, actualClassLoader, "packageManifests");
					setDummySet(cl, actualClassLoader, "negativeResourceCache");
					if(flag && actualClassLoader.getName().equals(clazzLoaderName))
						break;//Regardless of what LaunchClassLoader extends break after as we are done
					actualClassLoader = actualClassLoader.getSuperclass();
				}
				while(flag ? true : !actualClassLoader.getName().startsWith("java.") );
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
	 * Verifies that LaunchClassLoader Map / Set are instances of the Dummy Version. Only Checks LaunchClassLoader.class
	 */
	public static void verify()
	{
		Class launch = forName("net.minecraft.launchwrapper.Launch");
		if(launch == null)
			return;
		Class clazzLoaderClazz = forName("net.minecraft.launchwrapper.LaunchClassLoader");
		ClassLoader classLoader = (ClassLoader) getPrivate(null, launch, "classLoader", false);
		
		Map cachedClasses = (Map) getPrivate(classLoader, clazzLoaderClazz, "cachedClasses");
		Map resourceCache = (Map) getPrivate(classLoader, clazzLoaderClazz, "resourceCache");
		Map packageManifests = (Map) getPrivate(classLoader, clazzLoaderClazz, "packageManifests");
		Set negativeResourceCache = (Set) getPrivate(classLoader, clazzLoaderClazz, "negativeResourceCache");
		
		if(cachedClasses != null && !(cachedClasses instanceof DummyMap))
		{
			System.err.println("LaunchClassLoader#cachedClasses is Unoptimized! size:" + cachedClasses.size() + " Class:" + cachedClasses.getClass());
		}
		if(resourceCache != null && !(resourceCache instanceof DummyMap))
		{
			System.err.println("LaunchClassLoader#resourceCache is Unoptimized! size:" + resourceCache.size() + " Class:" + resourceCache.getClass());
		}
		if(packageManifests != null && !(packageManifests instanceof DummyMap))
		{
			System.err.println("LaunchClassLoader#packageManifests is Unoptimized! size:" + packageManifests.size() + " Class:" + packageManifests.getClass());
		}
		if(negativeResourceCache != null && !(negativeResourceCache instanceof DummySet))
		{
			System.err.println("LaunchClassLoader#negativeResourceCache is Unoptimized! size:" + negativeResourceCache.size() + " Class:" + negativeResourceCache.getClass());
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

	private static ClassLoader getContextClassLoader() 
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
	
    public static String toNString(Object o) {
        return o == null ? "0" : (o.getClass().getName() + "@" + System.identityHashCode(o));
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
					System.out.println("Unable to Close InputStream this is bad");
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
