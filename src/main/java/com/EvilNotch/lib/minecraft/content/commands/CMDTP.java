package com.EvilNotch.lib.minecraft.content.commands;

import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.minecraft.EntityUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandTP;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CMDTP extends CommandTP {
	
    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.tp.usage", new Object[0]);
        }
        else
        {
            int i = 0;
            Entity entity;

            if (args.length != 2 && args.length != 4 && args.length != 6)
            {
                entity = getCommandSenderAsPlayer(sender);
            }
            else
            {
                entity = getEntity(server, sender, args[0]);
                i = 1;
            }

            if (args.length != 1 && args.length != 2)
            {
                if (args.length < i + 3)
                {
                    throw new WrongUsageException("commands.tp.usage", new Object[0]);
                }
                else if (entity.world != null)
                {
                    int j = 4096;
                    int k = i + 1;
                    CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(entity.posX, args[i], true);
                    CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(entity.posY, args[k++], -4096, 4096, false);
                    CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(entity.posZ, args[k++], true);
                    CommandBase.CoordinateArg commandbase$coordinatearg3 = parseCoordinate((double)entity.rotationYaw, args.length > k ? args[k++] : "~", false);
                    CommandBase.CoordinateArg commandbase$coordinatearg4 = parseCoordinate((double)entity.rotationPitch, args.length > k ? args[k] : "~", false);
                    teleportEntity(entity, server, commandbase$coordinatearg, commandbase$coordinatearg1, commandbase$coordinatearg2, commandbase$coordinatearg3, commandbase$coordinatearg4);
                    notifyCommandListener(sender, this, "commands.tp.success.coordinates", new Object[] {entity.getName(), commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult()});
                }
            }
            else
            {
                Entity entity1 = getEntity(server, sender, args[args.length - 1]);
                entity.dismountRidingEntity();
                if (entity1.world != entity.world && !Config.tpAllowCrossDim)
                {
                    throw new CommandException("commands.tp.notSameDimension", new Object[0]);
                }
                    
                EntityUtil.telePortEntity(entity, server, entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch, entity1.dimension);

                notifyCommandListener(sender, this, "commands.tp.success", new Object[] {entity.getName(), entity1.getName()});
            }
        }
    }
    public void teleportEntity(Entity e,MinecraftServer server, CommandBase.CoordinateArg argX, CommandBase.CoordinateArg argY, CommandBase.CoordinateArg argZ, CommandBase.CoordinateArg argYaw, CommandBase.CoordinateArg argPitch) throws WrongUsageException
    {
    	EntityUtil.telePortEntity(e, server, argX.getResult(), argY.getResult(), argZ.getResult(), (float)argYaw.getResult(), (float)argPitch.getResult(), e.dimension);
    }

}
