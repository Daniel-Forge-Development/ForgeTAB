package com.envyful.tab.forge.task;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.tab.forge.ForgeTAB;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class PlayerUpdateTask implements Runnable {

    private static Field headerField;
    private static Field footerField;

    static {
        try {
            headerField = SPacketPlayerListHeaderFooter.class.getDeclaredField("field_179703_a");
            footerField = SPacketPlayerListHeaderFooter.class.getDeclaredField("field_179702_b");
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
        for (EntityPlayerMP player :
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
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
        }
    }

    private void sendHeaderAndFooter(EntityPlayerMP player, String header, String footer) {
        try {
            SPacketPlayerListHeaderFooter packet = new SPacketPlayerListHeaderFooter();
            headerField.set(packet, new TextComponentString(header));
            footerField.set(packet, new TextComponentString(footer));
            player.connection.sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
