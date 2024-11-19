package net.minecraftforge.fml.crashy;

import java.awt.GraphicsEnvironment;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.crashy.Crashy.ModEntry.MODSIDE;

/**
 * Copy this package into your project so you can have a Crash Report with a GUI while not keeping the original Process Alive
 * Doesn't Depend Upon Much Minecraft Specific Code So it should work for any Java Application.
 * To Get it to work with any Java Application Change {@link ModEntry#CURRENT_SIDE} Unless you Don't Plan to Use the ModEntry System
 * Refactor to your liking and also change "net.minecraftforge.fml.crashy.Crash" String to the new Refactored Package
 * @author jredfox
 */
public class Crashy {
	
    public static void crash(String msg, Throwable t, boolean gui)
    {
    	if(msg == null)
    		msg = "";
    	int retVal = -1;
        File crashDir = new File("crash-reports").getAbsoluteFile();
        File toFile = new File(crashDir, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
      
        if (toFile.getParentFile() != null)
            toFile.getParentFile().mkdirs();
        
        String reportMsg = getCompleteReport(msg, t);
        Writer writer = null;
        try
        {
            writer = new OutputStreamWriter(new FileOutputStream(toFile), StandardCharsets.UTF_8);
            writer.write(reportMsg);
        }
        catch(Throwable p)
        {
        	p.printStackTrace();
        	retVal = -2;
        }
        finally
        {
        	closeQuietly(writer);
        }
        System.err.print(reportMsg);
        
        if(gui)
        {
        	try
        	{
        		displayCrash(msg + nl(msg), t);
        	}
        	catch(Throwable t2)
        	{
        		System.err.println("Unable to Crash Report GUI");
        		t2.printStackTrace();
        	}
        }
        
        exit(retVal);
    }
    
    public static String getCompleteReport(String msg, Throwable t) 
    {
    	StringBuilder sb = new StringBuilder();
    	sb.append("---- Crash Report ----\n");
    	sb.append("// ");
    	sb.append(getWittyComment());
    	sb.append("\n\n");
    	sb.append("Time: ");
    	sb.append((new SimpleDateFormat()).format(new Date()));
    	sb.append("\n");
    	sb.append("Description: " + msg + "\n\n");
    	sb.append(getStackTrace(t));
    	return sb.toString();
	}

	/**
     * Gets a random witty comment for inclusion in this CrashReport
     */
    private static String getWittyComment()
    {
        String[] astring = new String[] {"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

        try
        {
            return astring[(int)(System.nanoTime() % (long)astring.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }
    
    public static String getStackTrace(Throwable t)
    {
    	if(t == null)
    		return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stacktrace = sw.toString();
        return stacktrace;
    }
    
    public static void displayCrash(String msg, Throwable t)
    {
    	if(msg == null)
    		msg = "";
        displayCrash(msg + (t == null ? "" : (nl(msg) + getStackTrace(t)) ), false);
    }
    
	public static void displayCrash(String msg, boolean exit)
	{
		//don't display GUI on web servers
		if(IS_HEADLESS || !GUI)
		{
			if(exit)
				exit(-1);
			return;
		}
		
    	if(msg == null)
    		msg = "";
		
		try
		{
			String java = System.getProperty("java.home") + "/bin/java".replace("/", File.separator);
			File jarFile = getFileFromClass(Crashy.class);
			//If Deobf or Jar is a Derp Jar get the Jar as a Folder
			if(jarFile.getPath().endsWith(".class"))
				jarFile = getDerpJar(jarFile, Crashy.class);
			ProcessBuilder pb = new ProcessBuilder(new String[]{java, "-Dcrashy.darkui=" + System.getProperty("crashy.darkui", "false"), "-cp", jarFile.getPath(), "net.minecraftforge.fml.crashy.Crash", msg});
			pb.start();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
		if(exit)
			exit(-1);
	}
	
    public static void displayMissingMods(ModEntry... mods)
    {
    	String s = "Missing Mods For " + ModEntry.CURRENT_SIDE;
    	boolean missing = false;
    	for(ModEntry mod : mods)
    	{
    		if(mod.side == MODSIDE.ANY || mod.side == ModEntry.CURRENT_SIDE)
    		{
    			try
    			{
					Class.forName(mod.clazz);
				} 
    			catch (Throwable e)
    			{
    				s += "\n" + mod.name + " for Side:" + mod.side;
    				missing = true;
				}
    		}
    	}
    	if(missing)
    	{
        	try 
        	{
    			displayCrash(s, true);
    		} 
        	catch (Throwable e)
        	{
    			e.printStackTrace();
    		}
    	}
    }
	
    /**
     * ModEntry are Minecraft Specific Code Change CURRENT_SIDE boolean based on what your Application Is.
     * @author jredfox
     */
	public static class ModEntry
	{
		public static MODSIDE CURRENT_SIDE = Crashy.class.getClassLoader().getSystemClassLoader().getResource("net/minecraft/client/main/Main.class") != null ? MODSIDE.CLIENT : MODSIDE.SERVER;
		
		public static enum MODSIDE
		{
			CLIENT(),
			SERVER(),
			ANY()
		}
		
		public String clazz;
		public String name;
		public MODSIDE side;
		
		public ModEntry(String clazz, String name, MODSIDE side)
		{
			this.clazz = clazz;
			this.name = name;
			this.side = side;
		}
	}
	
	//_______________________________________________START UTIL METHODS REQUIRED______________________________\\
	public static final boolean IS_HEADLESS = GraphicsEnvironment.isHeadless();
	/**
	 * Gets set by ConfigCore#load
	 */
	public static boolean GUI = true;
	
	public static void exit(int i) 
	{
		FMLCommonHandler fml = FMLCommonHandler.instance();
		if(fml != null)
		{
			fml.handleExit(i);
		}
		else
		{
			System.exit(i);
		}
	}
    
    public static void closeQuietly(final Closeable closeable) 
    {
        try 
        {
            if (closeable != null)
                closeable.close();
        } catch (final IOException ioe) {}
    }
    
    /**
     * @return a new line feed if the msg isn't empty
     */
    public static String nl(String msg)
    {
		return !msg.isEmpty() ? "\n" : "";
    }
	
	/**
	 * get a file from a class Does not support Eclipse's Jar In Jar Loader but does support javaw java and URLClassLoaders
	 * This assumes the file is contained in an archive file such as a zip or a jar. If it's a folder derp jar then it will return the physical path of the .class file
	 */
	public static File getFileFromClass(Class clazz)
	{
		URL jarURL = clazz.getProtectionDomain().getCodeSource().getLocation();//get the path of the currently running jar
		return getFileFromURL(jarURL);
	}
	
	private static File getFileFromURL(URL jarURL) 
	{
		String j = jarURL.toExternalForm().replace("jar:/", "").replace("jar:", "");
		if(j.contains("!"))
			j = j.substring(0, j.indexOf('!'));
		return getFileFromURL(j);
	}

	public static File getFileFromURL(String url)
	{
		try 
		{
			return new File(new URL(url).toURI()).getAbsoluteFile();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return The Root Directory of the Jar when the Jar is a Folder
	 */
	public static File getDerpJar(File clazzFile, Class clazz)
	{
		String pjar = clazzFile.getPath().replace("\\", "/");
		return new File(pjar.substring(0, pjar.lastIndexOf(clazz.getName().replace(".", "/") + ".class")));
	}

}