package com.envyful.tab.forge.api;

import com.envyful.tab.api.TabGroup;
import com.envyful.tab.forge.config.TABConfig;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * Static factory managing all {@link TabGroup} on the server
 *
 */
public class ForgeTabGroupFactory {

    private static final List<TabGroup> GROUPS = new CopyOnWriteArrayList<>();

    public static void init(TABConfig config) {
        for (ConfigurationNode groups : config.getNode().node("groups").childrenMap().values()) {
            GROUPS.add(new ForgeTabGroup(groups));
        }

        GROUPS.sort(Comparator.comparing(TabGroup::getWeight));
    }

    public static void reload(TABConfig config) {
        GROUPS.clear();
        init(config);
    }

    public static List<TabGroup> getGroups() {
        return GROUPS;
    }
}
