package com.denizenscript.depenizen.bukkit.events.askyblock;

import com.wasteofplastic.askyblock.events.IslandExitEvent;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.dLocation;
import com.denizenscript.denizen.objects.dPlayer;
import com.denizenscript.denizencore.objects.dObject;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerExitsSkyBlockScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player exits skyblock
    //
    // @Regex ^on player exits skyblock$
    //
    // @Cancellable false
    //
    // @Triggers when a player leaves a skyblock island.
    //
    // @Context
    // <context.owner> Returns the owner of the island.
    // <context.island_location> Returns the location of the island.
    // <context.location> Returns the location the player exited at.
    //
    // @Plugin Depenizen, A SkyBlock
    //
    // -->

    public static PlayerExitsSkyBlockScriptEvent instance;
    public IslandExitEvent event;
    public dLocation location;
    public dLocation island_location;
    public dPlayer owner;

    public PlayerExitsSkyBlockScriptEvent() {
        instance = this;
    }

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).startsWith("player exits skyblock");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return true;
    }

    @Override
    public String getName() {
        return "PlayerEntersSkyBlock";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(new dPlayer(event.getPlayer()), null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("owner")) {
            return owner;
        }
        else if (name.equals("island_location")) {
            return island_location;
        }
        else if (name.equals("location")) {
            return location;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onPlayerExitsSkyblock(IslandExitEvent event) {
        island_location = new dLocation(event.getIslandLocation());
        location = new dLocation(event.getLocation());
        owner = new dPlayer(event.getIslandOwner());
        this.event = event;
        fire(event);
    }
}
