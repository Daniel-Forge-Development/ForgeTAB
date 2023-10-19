package com.envyful.tab.forge;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.forge.concurrency.ForgeTaskBuilder;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.tab.forge.api.ForgeTabGroupFactory;
import com.envyful.tab.forge.command.ReloadCommand;
import com.envyful.tab.forge.config.TABConfig;
import com.envyful.tab.forge.player.TabAttribute;
import com.envyful.tab.forge.task.PlayerUpdateTask;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod(ForgeTAB.MOD_ID)
@Mod.EventBusSubscriber
public class ForgeTAB {

    public static final String MOD_ID = "forgetab";

    private static ForgeTAB instance;

    private final ForgePlayerManager playerManager = new ForgePlayerManager();
    private final ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, playerManager);

    private TABConfig config;

    public ForgeTAB() {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        instance = this;
        this.loadConfig();

        ForgeTabGroupFactory.init(this.config);

        playerManager.registerAttribute(TabAttribute.class);
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

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new ReloadCommand()));
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
