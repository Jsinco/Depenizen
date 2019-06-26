package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.depenizen.bukkit.events.sentinel.SentinelAttackScriptEvent;
import com.denizenscript.depenizen.bukkit.Bridge;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizencore.DenizenCore;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.dScript;
import net.aufdemrand.denizencore.scripts.ScriptBuilder;
import net.aufdemrand.denizencore.scripts.ScriptEntry;
import net.aufdemrand.denizencore.scripts.commands.core.DetermineCommand;
import net.aufdemrand.denizencore.scripts.containers.core.ProcedureScriptContainer;
import net.aufdemrand.denizencore.scripts.queues.ScriptQueue;
import net.aufdemrand.denizencore.scripts.queues.core.InstantQueue;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.entity.LivingEntity;
import org.mcmonkey.sentinel.SentinelIntegration;
import org.mcmonkey.sentinel.SentinelPlugin;
import org.mcmonkey.sentinel.SentinelUtilities;

import java.util.List;

public class SentinelBridge extends Bridge {

    @Override
    public void init() {
        ScriptEvent.registerScriptEvent(new SentinelAttackScriptEvent());
        SentinelPlugin.instance.registerIntegration(new DenizenSentinelTargets());
    }

    public class DenizenSentinelTargets extends SentinelIntegration {

        @Override
        public String getTargetHelp() {
            return "denizen_proc:PROCEDURE_SCRIPT_NAME, held_denizen_item:DENIZEN_ITEM_NAME";
        }

        @Override
        public String[] getTargetPrefixes() {
            return new String[] {"denizen_proc", "held_denizen_item"};
        }

        @Override
        public boolean isTarget(LivingEntity ent, String prefix, String value) {
            try {
                if (prefix.equals("held_denizen_item") && ent.getEquipment() != null) {
                    if (SentinelUtilities.regexFor(value).matcher(new dItem(SentinelUtilities.getHeldItem(ent)).identifySimple().replace("i@", "")).matches()) {
                        return true;
                    }
                }
                if (prefix.equals("denizen_proc") && ent.getEquipment() != null) {
                    dScript script = dScript.valueOf(value);
                    if (script == null) {
                        dB.echoError("Invalid procedure script name '" + value + "' (non-existent) in a Sentinel NPC target.");
                        return false;
                    }
                    if (!(script.getContainer() instanceof ProcedureScriptContainer)) {
                        dB.echoError("Invalid procedure script name '" + value + "' (not a procedure) in a Sentinel NPC target.");
                        return false;
                    }
                    List<ScriptEntry> entries = script.getContainer().getBaseEntries(DenizenCore.getImplementation().getEmptyScriptEntryData());
                    if (entries.isEmpty()) {
                        return false;
                    }
                    InstantQueue queue = InstantQueue.getQueue(ScriptQueue.getNextId(script.getContainer().getName()));
                    queue.addEntries(entries);
                    String def_name = "entity";
                    if (script.getContainer().getContents().contains("definitions")) {
                        List<String> definition_names = CoreUtilities.split(script.getContainer().getString("definitions"), '|');
                        if (definition_names.size() >= 1) {
                            def_name = definition_names.get(0);
                        }
                    }
                    queue.addDefinition(def_name, new dEntity(ent).getDenizenObject());
                    queue.start();
                    if (queue.determinations != null && queue.determinations.size() > 0) {
                        return CoreUtilities.toLowerCase(queue.determinations.get(0)).equals("true");
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}
