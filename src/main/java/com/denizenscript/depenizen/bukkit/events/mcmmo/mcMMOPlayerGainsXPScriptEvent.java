package com.denizenscript.depenizen.bukkit.events.mcmmo;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.dEntity;
import com.denizenscript.denizen.objects.dPlayer;
import com.denizenscript.denizencore.objects.Element;
import com.denizenscript.denizencore.objects.aH;
import com.denizenscript.denizencore.objects.dObject;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class mcMMOPlayerGainsXPScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // mcmmo player gains xp for skill (in <area>)
    // mcmmo player gains xp for <skill> (in <area>)
    //
    // @Regex ^on mcmmo player gains xp for [^\s]+( in ((notable (cuboid|ellipsoid))|([^\s]+)))?$
    //
    // @Cancellable true
    //
    // @Triggers when a player gains mcMMO xp.
    //
    // @Context
    // <context.skill> returns the name of the skill that the player gained xp for.
    // (Based on the mcMMO language file).
    // <context.xp> returns the amount of xp gained.
    // <context.cause> returns the cause of the xp gain.
    // Will be one of: 'PVP', 'PVE', 'VAMPIRISM', 'SHARED_PVP', 'SHARED_PVE', 'COMMAND', 'UNKNOWN'.
    //
    // @Plugin Depenizen, mcMMO
    //
    // -->

    public mcMMOPlayerGainsXPScriptEvent() {
        instance = this;
    }

    public static mcMMOPlayerGainsXPScriptEvent instance;
    public McMMOPlayerXpGainEvent event;
    public dPlayer player;
    public Element skill;
    public Element xp;
    public Element cause;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return lower.startsWith("mcmmo player gains xp for");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String eSkill = path.eventArgLowerAt(5);
        if (!eSkill.equals("skill") && !eSkill.equals(CoreUtilities.toLowerCase(skill.asString()))) {
            return false;
        }

        if (!runInCheck(path, player.getLocation())) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "mcMMOPlayerXPGain";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        String lower = CoreUtilities.toLowerCase(determination);

        if (aH.Argument.valueOf(lower).matchesPrimitive(aH.PrimitiveType.Float)) {
            xp = new Element(lower);
            return true;
        }
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(player, null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("skill")) {
            return skill;
        }
        else if (name.equals("xp")) {
            return xp;
        }
        else if (name.equals("cause")) {
            return cause;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onmcMMOPlayerLevelChanges(McMMOPlayerXpGainEvent event) {
        if (dEntity.isNPC(event.getPlayer())) {
            return;
        }
        player = dPlayer.mirrorBukkitPlayer(event.getPlayer());
        cause = new Element(event.getXpGainReason().toString());
        skill = new Element(event.getSkill().getName());
        xp = new Element(event.getRawXpGained());
        this.event = event;
        fire(event);
        event.setRawXpGained(xp.asFloat());
    }
}
