package com.envyful.tab.forge.api;

import com.envyful.tab.api.TabGroup;
import org.spongepowered.configurate.ConfigurationNode;

/**
 *
 * Forge implementation of the {@link TabGroup} interface
 *
 */
public class ForgeTabGroup implements TabGroup {

    private final String identifier;
    private final int weight;
    private final String permission;
    private final String prefix;
    private final String suffix;

    public ForgeTabGroup(ConfigurationNode node) {
        this.identifier = node.getString();
        this.weight = node.node("weight").getInt();
        this.permission = node.node("permission").getString();
        this.prefix = node.node("prefix").getString();
        this.suffix = node.node("suffix").getString();
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }
}
