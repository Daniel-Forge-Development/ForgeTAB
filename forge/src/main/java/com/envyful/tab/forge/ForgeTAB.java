package com.envyful.tab.forge;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.concurrency.ForgeTaskBuilder;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.tab.forge.api.ForgeTabGroupFactory;
import com.envyful.tab.forge.command.ReloadCommand;
import com.envyful.tab.forge.config.TABConfig;
import com.envyful.tab.forge.player.TabAttribute;
import com.envyful.tab.forge.task.PlayerUpdateTask;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;

@Mod(
        modid = "forgetab",
        name = "Forge TAB",
        version = ForgeTAB.VERSION,
        acceptableRemoteVersions = "*"
)
public class ForgeTAB {

    public static final String VERSION = "1.2.0";

    private static ForgeTAB instance;

    private final ForgePlayerManager playerManager = new ForgePlayerManager();
    private final ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private TABConfig config;

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        instance = this;
        this.loadConfig();

        ForgeTabGroupFactory.init(this.config);

        playerManager.registerAttribute(this, TabAttribute.class);

        new ForgeTaskBuilder()
                .async(true)
                .delay(10L)
                .interval(10L)
                .task(new PlayerUpdateTask(this))
                .start();
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(TABConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.commandFactory.registerCommand(event.getServer(), new ReloadCommand());
    }

    public TABConfig getConfig() {
        return this.config;
    }

    public ForgePlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public static ForgeTAB getInstance() {
        return instance;
    }
}
