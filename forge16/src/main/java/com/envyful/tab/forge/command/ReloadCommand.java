package com.envyful.tab.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.tab.forge.ForgeTAB;
import com.envyful.tab.forge.api.ForgeTabGroupFactory;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

@Command(
        value = "forgetab",
        description = "forge tab reload",
        aliases = {
                "ftabreload"
        }
)
@Permissible("forge.tab.command.reload")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        ForgeTAB.getInstance().loadConfig();
        ForgeTabGroupFactory.reload(ForgeTAB.getInstance().getConfig());
        sender.sendMessage(new StringTextComponent("Reloaded"), Util.NIL_UUID);
    }
}
