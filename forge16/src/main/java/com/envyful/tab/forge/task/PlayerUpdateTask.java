package com.envyful.tab.forge.task;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.tab.forge.ForgeTAB;
import com.envyful.tab.forge.player.TabAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class PlayerUpdateTask implements Runnable {

    private static Field headerField;
    private static Field footerField;

    static {
        try {
            headerField = SPlayerListHeaderFooterPacket.class.getDeclaredField("field_179703_a");
            footerField = SPlayerListHeaderFooterPacket.class.getDeclaredField("field_179702_b");
            headerField.setAccessible(true);
            footerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private final ForgeTAB mod;

    public PlayerUpdateTask(ForgeTAB mod) {
        this.mod = mod;
    }

    @Override
    public void run() {
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            String header = this.mod.getConfig().getHeader().stream()
                    .map(s -> UtilChatColour.translateColourCodes(
                            '&',
                            UtilPlaceholder.replaceIdentifiers(player, s)
                         )
                    ).collect(Collectors.joining("\n"));
            String footer = this.mod.getConfig().getFooter().stream()
                    .map(s -> UtilChatColour.translateColourCodes(
                            '&',
                            UtilPlaceholder.replaceIdentifiers(player, s)
                         )
                    ).collect(Collectors.joining("\n"));

            this.sendHeaderAndFooter(player, header, footer);
            this.sendUpdatePackets(player);
        }
    }

    private void sendHeaderAndFooter(ServerPlayerEntity player, String header, String footer) {
        try {
            SPlayerListHeaderFooterPacket packet = new SPlayerListHeaderFooterPacket();
            headerField.set(packet, new StringTextComponent(header));
            footerField.set(packet, new StringTextComponent(footer));
            player.connection.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendUpdatePackets(ServerPlayerEntity player) {
        EnvyPlayer<ServerPlayerEntity> envyPlayer = this.mod.getPlayerManager().getPlayer(player);
        TabAttribute attribute = envyPlayer.getAttribute(ForgeTAB.class);

        if (attribute == null) {
            return;
        }

        attribute.reCalculateGroup();

        for (ForgeEnvyPlayer onlinePlayer : this.mod.getPlayerManager().getOnlinePlayers()) {
            attribute.sendPackets(onlinePlayer.getParent());
        }
    }
}
