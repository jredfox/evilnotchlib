package com.EvilNotch.lib.minecraft.content.commands;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.main.Config;
import com.EvilNotch.lib.minecraft.EntityUtil;
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
import net.minecraft.util.math.BlockPos;
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
        int index = 0;
        if (args.length < 5 && args.length != 1 && args.length != 2)
        {
            throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
        }
        else if (args.length == 1 || args.length == 2)
        {
        	Entity e = args.length == 1 ? getCommandSenderAsPlayer(sender) : getEntity(server, sender, args[index++]);
        	Entity e2 = getEntity(server, sender, args[index++]);
        	
        	if(e == null || !(e2 instanceof EntityPlayer) && args.length == 2|| e.world == null || e2.world == null)
        		throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
        	
          	Entity fromPlayer = e;
        	EntityPlayerMP toPlayer = (EntityPlayerMP)e2;
        	fromPlayer.dismountRidingEntity();
        	
        	if(fromPlayer instanceof EntityLivingBase)
        	{
        		((EntityLivingBase)fromPlayer).rotationYawHead = toPlayer.rotationYawHead;
        	}
        	EntityUtil.telePortEntity(fromPlayer, server, toPlayer.posX, toPlayer.posY, toPlayer.posZ,toPlayer.rotationYaw,toPlayer.rotationPitch, toPlayer.dimension);
        	
        	notifyCommandListener(sender, this, "commands.teleport.success.coordinates", new Object[] {e.getName(),  toPlayer.posX, toPlayer.posY, toPlayer.posZ,"Dim:" + toPlayer.dimension});
        }
        else if (args.length == 5)
        {
            Entity entity = getEntity(server, sender, args[index++]);

            if (entity.world != null)
            {
                Vec3d vec3d = sender.getPositionVector();
                entity.dismountRidingEntity();
                
                CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(vec3d.x, args[index++], true);
                CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(vec3d.y, args[index++], -4096, 4096, false);
                CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(vec3d.z, args[index++], true);
                double x = commandbase$coordinatearg.getResult();
                double y = commandbase$coordinatearg1.getResult();
                double z = commandbase$coordinatearg2.getResult();
                
                String strdim = args[index++];
                if(!LineBase.isStringNum(strdim))
                	 throw new WrongUsageException("commands.evilnotchlib.tp.usage", new Object[0]);
                
                int traveldim = Integer.parseInt(strdim);
                EntityUtil.telePortEntity(entity, server, x, y, z,entity.rotationYaw,entity.rotationPitch, traveldim);
                
                notifyCommandListener(sender, this, "commands.teleport.success.coordinates", new Object[] {entity.getName(), commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult(),"Dim:" + traveldim});
            }
        }
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
