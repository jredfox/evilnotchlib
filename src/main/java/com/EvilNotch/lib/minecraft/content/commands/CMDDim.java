package com.EvilNotch.lib.minecraft.content.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.Line.LineBase;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

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
        int index = 0;
        if (args.length == 0 || args.length > 8)
        {
            throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
        }
        else if (args.length >= 1 && args.length < 3)
        {
    		if(!(sender instanceof Entity) && args.length == 1)
    			throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
        	String arg = args[args.length-1];
        	boolean flag = LineBase.isStringNum(arg);
       		Entity fromPlayer = args.length == 1 ? (Entity)sender : getEntity(server, sender, args[index++]);
       		Entity toPlayer = args.length <= 2 && flag ? fromPlayer : getEntity(server, sender, args[index++]);
       		
       		int dim = flag ? Integer.parseInt(arg) : toPlayer.dimension;
       		if(toPlayer instanceof EntityLivingBase && fromPlayer instanceof EntityLivingBase)
       		{
       			((EntityLivingBase)fromPlayer).rotationYawHead = ((EntityLivingBase)toPlayer).rotationYawHead;
       		}
       		teleportEnt(fromPlayer, server, toPlayer.posX, toPlayer.posY, toPlayer.posZ,toPlayer.rotationYaw,toPlayer.rotationPitch, dim);        	
        	
       		notifyCommandListener(sender, this, "commands.teleport.success.coordinates", new Object[] {fromPlayer.getName(),  toPlayer.posX, toPlayer.posY, toPlayer.posZ,"Dim:" + toPlayer.dimension});
        }
        else if (args.length == 3)
        {
        	//tpdim @p int senderBoolean
    		if(args.length == 3)
    		{
    			String last = args[args.length-1];
    			if(last.startsWith("~") || LineBase.isStringNum(last))
    				throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
    		}
    		Entity e = getEntity(server, sender, args[index++]);
    		
    		String dim = args[index++];
    		String bool = args[index++];
    		if(!LineBase.isStringNum(dim) || !JavaUtil.isStringBoolean(bool))
    			throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
    		
    		int dimension = Integer.parseInt(dim);
    		boolean senderCoords = Boolean.parseBoolean(bool);
    		double x = e.posX;
    		double y = e.posY;
    		double z = e.posZ;
    		
    		if(senderCoords)
    		{
    			Vec3d vec3d = sender.getPositionVector();
                x = vec3d.x;
                y = vec3d.y;
                z = vec3d.z;
                if(sender instanceof Entity)
                {
                	Entity e2 = (Entity)sender;
                	e.rotationYaw = e2.rotationYaw;
                	e.rotationPitch = e2.rotationPitch;
                	if(sender instanceof EntityLivingBase && e instanceof EntityLivingBase)
                	{
                		((EntityLivingBase)e).rotationYawHead = ((EntityLivingBase)e2).rotationYawHead;
                	}
                }
    		}
    		teleportEnt(e, server, x, y, z, e.rotationYaw, e.rotationPitch, dimension);
        }
        else if (args.length >= 4)
        {
        	String ent = args[index];
        	Entity entity = null;
        	
        	if(ent.startsWith("~") || LineBase.isStringNum(ent))
        	{
        		if(!(sender instanceof Entity))
        			throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
        		entity = (Entity)sender;
        	}
        	else
        	{
        		entity = getEntity(server, sender, args[index++]);
        	}

            if (entity.world != null)
            {
            	boolean vecFlag = JavaUtil.getBoolean(args[args.length-1]);
            	
                Vec3d vec3d =  vecFlag ? sender.getPositionVector() : entity.getLowestRidingEntity().getPositionVector();
                
                CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(vec3d.x, args[index++], true);
                CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(vec3d.y, args[index++], false);
                CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(vec3d.z, args[index++], true);
                double x = commandbase$coordinatearg.getResult();
                double y = commandbase$coordinatearg1.getResult();
                double z = commandbase$coordinatearg2.getResult();

                if(index == args.length)
                	throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
                
                String strdim = args[index++];
                if(!LineBase.isStringNum(strdim))
               	 	throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
                int traveldim = Integer.parseInt(strdim);
                
                float yaw = entity.rotationYaw;
                float pitch = entity.rotationPitch;
                boolean hasFlags = false;
                if(index+2 <= args.length)
                {
                	CommandBase.CoordinateArg yawcoord = parseCoordinate(vec3d.x, args[index++], true);
                    CommandBase.CoordinateArg pitchcoord = parseCoordinate(vec3d.y, args[index++], false);
                    yaw = (float)yawcoord.getResult();
                    pitch = (float)pitchcoord.getResult();
                    hasFlags = true;
                }
                
                if(this.dismountPassengers())
                	entity.removePassengers();
                
                teleportEnt(entity, server, x, y, z,yaw,pitch, traveldim);
                
                notifyCommandListener(sender, this, "commands.teleport.success.coordinates", new Object[] {entity.getName(), commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult(),"Dim:" + traveldim});
            }
        }
    }
    /**
     * public method to determine how to teleport the entity since this command is extendible
     */
    public void teleportEnt(Entity entity,MinecraftServer server, double x, double y, double z, float yaw, float pitch, int traveldim)
    {
    	if(this.wholeStack())
    	{
    		EntityUtil.teleportStack(entity, server, x, y, z,yaw,pitch, traveldim);
    	}
    	else if(!this.dismountPassengers() )
    	{
    		EntityUtil.teleportStackAtIndex(entity, server, x, y, z,yaw,pitch, traveldim);
    	}
    	else
    	{
    		entity.removePassengers();
    		EntityUtil.telePortEntity(entity, server, x, y, z,yaw,pitch, traveldim);
    	}
    }
	public boolean wholeStack()
    {
    	return false;
    }
    public boolean dismountPassengers()
    {
    	return false;
    }

	/**
     * Get a list of options for when the user presses the TAB key
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length != 1 && args.length != 2 ? Collections.emptyList() : getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }

}
