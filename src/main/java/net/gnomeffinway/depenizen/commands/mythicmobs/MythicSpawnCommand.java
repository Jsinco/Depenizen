package net.gnomeffinway.depenizen.commands.mythicmobs;

import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizencore.exceptions.CommandExecutionException;
import net.aufdemrand.denizencore.exceptions.InvalidArgumentsException;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.aH;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.AbstractCommand;
import net.elseland.xikage.MythicMobs.API.IMobsAPI;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;
import net.elseland.xikage.MythicMobs.MythicMobs;
import net.gnomeffinway.depenizen.objects.mythicmobs.MythicMobsMob;
import org.bukkit.entity.Entity;

public class MythicSpawnCommand extends AbstractCommand {

    // <--[command]
    // @Name MythicSpawn
    // @Syntax mythicspawn [<name>] [<location>] (level:<#>)
    // @Group Depenizen
    // @Plugin Depenizen, MythicMobs
    // @Required 2
    // @Stable untested
    // @Short Spawns a MythicMob at a location.
    // @Author Morphan1

    // @Description
    // This allows you to spawn a MythicMob at a location using the mob's internal name.

    // @Tags
    // <entry[saveName].spawned_mythicmob> returns the spawned MythicMobsMob.

    // @Usage
    // Use to spawn a BarbarianMinion at a player's location.
    // - mythicspawn BarbarianMinion <player.location>

    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (aH.Argument arg : aH.interpret(scriptEntry.getArguments())) {

            if (!scriptEntry.hasObject("location")
                    && arg.matchesArgumentType(dLocation.class)) {
                scriptEntry.addObject("location", arg.asType(dLocation.class));
            }

            else if (!scriptEntry.hasObject("level")
                    && arg.matchesPrefix("level", "l")
                    && arg.matchesPrimitive(aH.PrimitiveType.Integer)) {
                scriptEntry.addObject("level", arg.asElement());
            }

            else if (!scriptEntry.hasObject("name")) {
                scriptEntry.addObject("name", arg.asElement());
            }

            else {
                arg.reportUnhandled();
            }

        }

        if (!scriptEntry.hasObject("location") || !scriptEntry.hasObject("name")) {
            throw new InvalidArgumentsException("Must specify a name and location.");
        }

        scriptEntry.defaultObject("level", new Element(1));
    }

    @Override
    public void execute(ScriptEntry scriptEntry) throws CommandExecutionException {

        Element name = scriptEntry.getElement("name");
        dLocation location = scriptEntry.getdObject("location");
        Element level = scriptEntry.getElement("level");

        IMobsAPI api = MythicMobs.inst().getAPI().getMobAPI();

        try {
            MythicMob mob = api.getMythicMob(name.asString());
            Entity entity = api.spawnMythicMob(mob, location, level.asInt());
            scriptEntry.addObject("spawned_mythicmob", new MythicMobsMob(api.getMythicMobInstance(entity)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
