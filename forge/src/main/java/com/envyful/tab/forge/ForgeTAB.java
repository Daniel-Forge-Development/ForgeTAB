package com.envyful.tab.forge;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.concurrency.ForgeUpdateBuilder;
import com.envyful.tab.forge.config.TABConfig;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;
import org.bstats.forge.Metrics;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Paths;

@Mod(
        modid = "forgetab",
        name = "Forge TAB",
        version = ForgeTAB.VERSION,
        acceptableRemoteVersions = "*"
)
public class ForgeTAB {

    public static final String VERSION = "0.0.1";

    private TABConfig config;

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        this.loadConfig();

        Metrics metrics = new Metrics(
                Loader.instance().activeModContainer(),
                event.getModLog(),
                Paths.get("config/"),
                12198 //TODO
        );

        ForgeUpdateBuilder.instance()
                .name("ForgeTAB")
                .version(VERSION)
                .requiredPermission("tab.update.notify")
                .owner("Pixelmon-Development")
                .repo("ForgeTAB")
                .start();
    }

    private void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(TABConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    public TABConfig getConfig() {
        return this.config;
    }
}
