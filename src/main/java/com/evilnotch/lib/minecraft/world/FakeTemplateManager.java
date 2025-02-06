package com.evilnotch.lib.minecraft.world;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class FakeTemplateManager extends TemplateManager {

	public FakeTemplateManager() 
	{
		super("FakeWorld", null);
	}
	
	public static Template template = new Template();
	@Override
    public Template getTemplate(MinecraftServer server, ResourceLocation id)
    {
		return template;
    }
	
    @Override
	public Template get(@Nullable MinecraftServer server, ResourceLocation templatePath)
    {
    	return template;
    }
    
    @Override
	public boolean readTemplate(ResourceLocation server)
    {
    	return false;
    }
    
    @Override
	public boolean writeTemplate(@Nullable MinecraftServer server, ResourceLocation id)
    {
    	return false;
    }
    
    @Override
    public void remove(ResourceLocation templatePath)
    {
    	
    }

}
