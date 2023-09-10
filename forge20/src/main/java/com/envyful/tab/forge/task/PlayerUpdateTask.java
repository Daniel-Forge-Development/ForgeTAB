package com.envyful.tab.forge.task;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.tab.forge.ForgeTAB;
import com.envyful.tab.forge.player.TabAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PlayerUpdateTask implements Runnable {

    private final ForgeTAB mod;

    public PlayerUpdateTask(ForgeTAB mod) {
        this.mod = mod;
    }

    @Override
    public void run() {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            Component header = this.mod.getConfig().getHeader().stream()
                    .map(s -> UtilChatColour.colour(
                            UtilPlaceholder.replaceIdentifiers(player, s)
                         )
                    ).reduce(Component.empty(), (textComponent, textComponent2) -> textComponent.copy().append(System.lineSeparator()).append(textComponent2));
            Component footer = this.mod.getConfig().getFooter().stream()
                    .map(s -> UtilChatColour.colour(
                            UtilPlaceholder.replaceIdentifiers(player, s)
                         )
                    ).reduce(Component.empty(), (textComponent, textComponent2) -> textComponent.copy().append(System.lineSeparator()).append(textComponent2));

            this.sendHeaderAndFooter(player, header, footer);
            this.sendUpdatePackets(player);
        }
    }

    private void sendHeaderAndFooter(ServerPlayer player, Component header, Component footer) {
        player.connection.send(new ClientboundTabListPacket(header, footer));
    }

    private void sendUpdatePackets(ServerPlayer player) {
        EnvyPlayer<ServerPlayer> envyPlayer = this.mod.getPlayerManager().getPlayer(player);
        TabAttribute attribute = envyPlayer.getAttribute(TabAttribute.class);

        if (attribute == null) {
            return;
        }

        attribute.reCalculateGroup();

        for (ForgeEnvyPlayer onlinePlayer : this.mod.getPlayerManager().getOnlinePlayers()) {
            attribute.sendPackets(onlinePlayer.getParent());
        }
    }
}
