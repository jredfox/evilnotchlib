package com.EvilNotch.lib.minecraft.content.client.rp;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.Level;

import com.EvilNotch.lib.Api.FieldAcessClient;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.MainJava;
import com.EvilNotch.lib.minecraft.content.ConfigLang;
import com.EvilNotch.lib.minecraft.content.client.models.TextureAtlasSpriteFixed;
import com.EvilNotch.lib.minecraft.proxy.ServerProxy;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.ILine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class CustomResourcePack implements IResourcePack,IResourceManagerReloadListener
{
	public File rootDir = null;
	public File packmcmeta = null;
	public File sounds = null;
	public File packImg = null;
	
	public static String currentLang = null;
	public static Map<String, String> langlistClient = null;

	public CustomResourcePack(File rootDir){
		this.rootDir = rootDir;
		this.packmcmeta = new File(rootDir,"pack.mcmeta");
		this.sounds = new File(this.rootDir,"assets/minecraft/sounds.json");
	}

	@Override
	public InputStream getInputStream(ResourceLocation rl) throws IOException
	{
		if (!resourceExists(rl))
			return null;
		else
		{
			File file = new File(new File(this.rootDir, "assets/" + rl.getResourceDomain()), rl.getResourcePath().replaceAll(":", "/"));

			String realFileName = file.getCanonicalFile().getName();
			if (!realFileName.equals(file.getName()))
				MainJava.logger.log(Level.WARN, "[LangPack] Resource Location " + rl.toString() + " only matches the file " + realFileName + " because RL is running in an environment that isn't case sensitive in regards to file names. This will not work properly on for example Linux.");

			return new FileInputStream(file);
		}
	}
	
	@Override
	public boolean resourceExists(ResourceLocation rl)
	{
		File fileRequested = new File(new File(this.rootDir,  "assets/" + rl.getResourceDomain()), rl.getResourcePath().replaceAll(":", "/"));

		if (!fileRequested.isFile())
		{
//			System.out.println("Asked for resource " + rl.toString() + " but can't find a file at " + fileRequested.getAbsolutePath());
//			MainJava.logger.log(Level.WARN, "[LangPack] Asked for resource " + rl.toString() + " but can't find a file at " + fileRequested.getAbsolutePath());
		}

		return fileRequested.isFile();
	}

	/**
	 * This is where it tries to load an array of resource packs
	 * Where your in the resourcepacks folder and it wants to know what to load
	 */
	@Override
	public Set<String> getResourceDomains()
	{
		File folder = new File(this.rootDir,"assets");
		if (!folder.exists())
			folder.mkdir();
		
		HashSet<String> folders = new HashSet<String>();

		MainJava.logger.log(Level.DEBUG, "[LangPack] Loading Domains: ");

		File[] resourceDomains = folder.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);

		for (File resourceFolder : resourceDomains)
		{
//			MainJava.logger.log(Level.DEBUG, "[LangPack]" + resourceFolder.getName() + " | " + resourceFolder.getAbsolutePath());
			folders.add(resourceFolder.getName());
		}

		return folders;
	}
	
	@Nullable
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
	{
	    try
	    {
	        InputStream inputstream = new FileInputStream(this.packmcmeta);
	        return (T)readMetadata(metadataSerializer, inputstream, metadataSectionName);
	    }
	    catch (RuntimeException var4)
	    {
	        return (T)null;
	    }
	    catch (FileNotFoundException var5)
	    {
	        return (T)null;
	    }
	}
    static <T extends IMetadataSection> T readMetadata(MetadataSerializer metadataSerializer, InputStream p_110596_1_, String sectionName)
    {
        JsonObject jsonobject = null;
        BufferedReader bufferedreader = null;

        try
        {
            bufferedreader = new BufferedReader(new InputStreamReader(p_110596_1_, StandardCharsets.UTF_8));
            jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
        }
        catch (RuntimeException runtimeexception)
        {
            throw new JsonParseException(runtimeexception);
        }
        finally
        {
            IOUtils.closeQuietly((Reader)bufferedreader);
        }

        return (T)metadataSerializer.parseMetadataSection(sectionName, jsonobject);
    }

	@Override
	public BufferedImage getPackImage() throws IOException
	{
		return null;
	}

	@Override
	public String getPackName()
	{
		return "LangPack";
	}

	/**
	 * Manually refresh resources do not call if already reloading resources vanilla style
	 */
	public void refresh() throws FileNotFoundException 
	{
		//lang_______________________
		if(langlistClient == null)
		{
			LanguageManager manager = Minecraft.getMinecraft().getLanguageManager();
			Locale l = (Locale)ReflectionUtil.getObject(manager, LanguageManager.class, FieldAcessClient.CURRENT_LOCALE);
			Map<String, String> map = (Map<String, String>) ReflectionUtil.getObject(l, Locale.class, FieldAcessClient.properties);
			langlistClient = map;
		}
		if(currentLang == null)
			currentLang = getCurrentLang();
		
		HashMap<File,ConfigLang> map = ServerProxy.langCfgs;
		System.out.print("[EvilNotchLib/Info] Injecting Integrated Server Lang:\n");
		
		ServerProxy.injectServerLang(ServerProxy.langDir, map);//integrated support
		ConfigLang cfg = map.get(new File(ServerProxy.langDir,currentLang + ".lang"));
		
		if(cfg != null)
		{
			injectClientLang(cfg);
		}
		if(!currentLang.equals("en_us"))
		{
			ConfigLang cfg2 = map.get(new File(ServerProxy.langDir,"en_us" + ".lang"));
			if(cfg2 != null)
				injectClientLang(cfg2);//fallback map to behave like vanilla does
		}
		//end lang___________________
		
		//actual refresh
		ArrayList<File> files = new ArrayList();
		JavaUtil.getAllFilesFromDir(this.rootDir, files);
		for(File f : files)
		{
			String name = f.getName();
			if(name.endsWith(".png"))
				injectTexture(f);
			else if(name.endsWith(".ogg"))
				injectSound(f,this.sounds);
			else if(name.endsWith(".json"))
				injectModel(f);
		}
	}
	/**
	 * Create fast utf-8 instanceof ConfigLang then do manual injections
	 */
	public void injectClientLang(File f) throws FileNotFoundException 
	{
		ConfigLang cfg = new ConfigLang(f);
		injectClientLang(cfg);
	}

	public void injectClientLang(ConfigLang cfg) 
	{
		for(ILine line : cfg.lines)
		{
			String key = line.getModPath();
			String value = (String) line.getHead();
			if(!langlistClient.containsKey(key))
				langlistClient.put(key,value);
		}
	}

	public static String getCurrentLang() 
	{
		 return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}

	/**
	 * Manually inject texture into memory
	 */
	public void injectTexture(File textureFile) 
	{
		// TODO Auto-generated method stub
		File folder = new File(this.rootDir,"assets");
		TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
		Map<String, TextureAtlasSprite> uploadSprite = (Map<String, TextureAtlasSprite>) ReflectionUtil.getObject(textureMap, TextureMap.class, MCPMappings.getField(TextureMap.class,"mapUploadedSprites"));
		Map<String, TextureAtlasSprite> mapRegisteredSprites = (Map<String, TextureAtlasSprite>) ReflectionUtil.getObject(textureMap, TextureMap.class, MCPMappings.getField(TextureMap.class,"mapRegisteredSprites"));
		String tst = "minecraft:blocks/stone";
		uploadSprite.put(tst, new TextureAtlasSpriteFixed(tst));
		mapRegisteredSprites.put(tst, new TextureAtlasSpriteFixed(tst));
	}

	public void injectSound(File f,File sounds$json) 
	{
		if(!sounds$json.exists())
		{
			MainJava.logger.log(Level.ERROR,"[IResourcePack] Unable to Bind sounds.json skipping:" + f);
			return;
		}
	}
	public void injectModel(File f) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) 
	{
		currentLang = getCurrentLang();
	}
	
}
