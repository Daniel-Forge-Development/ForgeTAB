package com.envyful.tab.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigPath("config/TAB/config.yml")
@ConfigSerializable
public class TABConfig extends AbstractYamlConfig {

    private List<String> header = Lists.newArrayList(
            " ",
            "&bExample Server Name",
            " ",
            "&7(&a%forge_online% / &c%forge_max_online%)",
            " "
    );

    private List<String> footer = Lists.newArrayList(
            " ",
            "Discord: discord.gg/pixelmon",
            " "
    );

    public TABConfig() {}

    public List<String> getHeader() {
        return this.header;
    }

    public List<String> getFooter() {
        return this.footer;
    }
}
