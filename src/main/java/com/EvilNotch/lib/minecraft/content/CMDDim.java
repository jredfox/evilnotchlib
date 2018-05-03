package com.EvilNotch.lib.minecraft.content;

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.util.Line.LineBase;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CMDDim extends CommandTeleport{
	
	 /**
     * Gets the name of the command
     */
	@Override
    public String getName()
    {
        return "tpdim";
    }
    /**
    * Gets the usage string for the command.
    */
    @Override
    public String getUsage(ICommandSender sender)
    {
       return "commands.evilnotchlib.tp.usage";
    }
    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 5)
        {
            throw new WrongUsageException("commands.teleport2.usage", new Object[0]);
        }
        else
        {
            int index = 0;
            Entity entity = getEntity(server, sender, args[index++]);

            if (entity.world != null)
            {
                int i = 4096;
                Vec3d vec3d = sender.getPositionVector();
                
                CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(vec3d.x, args[index++], true);
                CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(vec3d.y, args[index++], -4096, 4096, false);
                CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(vec3d.z, args[index++], true);
                double x = commandbase$coordinatearg.getResult();
                double y = commandbase$coordinatearg1.getResult();
                double z = commandbase$coordinatearg2.getResult();
                
                String strdim = args[index++];
                if(!LineBase.isStringNum(strdim))
                	 throw new WrongUsageException("commands.teleport3.usage", new Object[0]);
                
                int traveldim = Integer.parseInt(strdim);
                int prevDim = entity.dimension;
            	EntityPlayerMP p = (EntityPlayerMP)entity;
                if(traveldim != prevDim)
                {
                	p.setLocationAndAngles(commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult(), entity.rotationYaw, entity.rotationPitch);
                	World oldWorld = p.getEntityWorld();
                	p.getServer().getPlayerList().transferPlayerToDimension(p,traveldim, new EntityTeleporter(p.getServer().getWorld(traveldim)));
                	World newWorld = p.world;
                	if(!newWorld.loadedEntityList.contains(p) || !newWorld.playerEntities.contains(p))
                	{
                		System.out.println("ERR Player Not Added To World:" + p.getName());
                		newWorld.spawnEntity(p);
                	}
                	if(prevDim == 1)
                		removeDragonBars(p,oldWorld);//vanilla bug fix 1.9+
                }
                if(p.posX != x || p.posY != y || p.posZ != z)
                {
                	this.doTeleport(entity, x, y, z);
                	if(prevDim != traveldim)
                		System.out.println("World Provider Trying to Overriding TP Command Please Report To Mod Author:\n" + x + "," + y + "," + z + " " + p.world.getClass().getName() );
                }
                System.out.println("Ent List:" + p.world.playerEntities);
                System.out.println("EntHasPlayer:" + p.world.loadedEntityList.contains(p));
                
               	FMLCommonHandler.instance().firePlayerRespawnEvent((EntityPlayer)entity, false);
                
                notifyCommandListener(sender, this, "commands.teleport.success.coordinates", new Object[] {entity.getName(), commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult()});
            }
        }
    }
    
    /**
     * Must be only called if player is at end dim this is because vanilla is hard coded and no longer displays bars outside of end dim
     */
    public void removeDragonBars(EntityPlayerMP p,World end) 
    {
    	try
    	{
    		DragonFightManager fightManager = ((WorldProviderEnd)end.provider).getDragonFightManager();
    		if(fightManager != null)
    		{
    			FieldAcess.method_dragonManager.invoke(fightManager);
    		}
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
	}
	/**
     * Perform the actual teleport
     */
    protected void doTeleport(Entity e, double x, double y, double z)
    {
        if (e instanceof EntityPlayerMP)
        {
            Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);
            e.dismountRidingEntity();
            if(e.posX != x || e.posY != y || e.posZ != z)
            {
            	e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
            	((EntityPlayerMP)e).connection.setPlayerLocation(x, y, z, e.rotationYaw, e.rotationPitch, set);
            }
        }
        else
        {
            float f2 = (float)MathHelper.wrapDegrees(e.rotationYaw);
            float f3 = (float)MathHelper.wrapDegrees(e.rotationPitch);
            f3 = MathHelper.clamp(f3, -90.0F, 90.0F);
            e.setLocationAndAngles(x, y, z, f2, f3);
        }

        if (!(e instanceof EntityLivingBase) || !((EntityLivingBase)e).isElytraFlying())
        {
            e.motionY = 0.0D;
            e.onGround = true;
        }
    }

}
