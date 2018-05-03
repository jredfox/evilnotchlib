package com.EvilNotch.lib.minecraft.content.client.rp;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.main.MainJava;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureObj extends SimpleTexture{
	public static List<IResourcePack> packs = null;
	public static MetadataSerializer metaserial = null;
	
	public TextureObj(ResourceLocation textureResourceLocation) {
		super(textureResourceLocation);
	}
	@Override
    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        this.deleteGlTexture();
        IResource iresource = null;

        try
        {
            iresource = getResource(this.textureLocation);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            boolean flag = false;
            boolean flag1 = false;

            if (iresource.hasMetadata())
            {
                try
                {
                    TextureMetadataSection texturemetadatasection = (TextureMetadataSection)iresource.getMetadata("texture");

                    if (texturemetadatasection != null)
                    {
                        flag = texturemetadatasection.getTextureBlur();
                        flag1 = texturemetadatasection.getTextureClamp();
                    }
                }
                catch (RuntimeException runtimeexception)
                {
                    MainJava.logger.warn("Failed reading metadata of: {}", this.textureLocation, runtimeexception);
                }
            }

            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag1);
        }
        finally
        {
            IOUtils.closeQuietly((Closeable)iresource);
        }
    }
	public IResource getResource(ResourceLocation loc) throws IOException {
		if(packs == null || packs.size() == 0)
		{
			packs = new ArrayList();
			List<IResourcePack> dpacks = (List<IResourcePack>) ReflectionUtil.getObject(Minecraft.getMinecraft(), Minecraft.class, "defaultResourcePacks");
			for(IResourcePack pack : dpacks)
				packs.add(pack);
			List<ResourcePackRepository.Entry> entries = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries();
			for(ResourcePackRepository.Entry entry : entries)
				packs.add(entry.getResourcePack());
			metaserial = (MetadataSerializer) ReflectionUtil.getObject(Minecraft.getMinecraft(), Minecraft.class, "metadataSerializer_");
		}
		for(int i=packs.size()-1;i>=0;i++)
		{
			IResourcePack pack = packs.get(i);
			InputStream stream = pack.getInputStream(loc);
			InputStream streamMcmeta = pack.getInputStream(new ResourceLocation(loc.getResourceDomain(),loc.getResourcePath() + ".mcmeta"));
			if(stream != null)
				return new SimpleResource(pack.getPackName(), this.textureLocation, stream, streamMcmeta, metaserial);
		}
		return null;
	}

}
