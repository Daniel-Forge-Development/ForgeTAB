package com.envyful.tab.forge.player;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.tab.api.TabGroup;
import com.envyful.tab.forge.ForgeTAB;
import com.envyful.tab.forge.api.ForgeTabGroupFactory;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.StringTextComponent;

import java.util.Objects;
import java.util.UUID;

public class TabAttribute extends AbstractForgeAttribute<ForgeTAB> {

    private ScorePlayerTeam team;
    private TabGroup tabGroup;

    public TabAttribute(ForgeTAB manager, EnvyPlayer<?> parent) {
        super(manager, (ForgeEnvyPlayer) parent);
    }

    public TabAttribute(UUID uuid) {
        super(uuid);
    }

    public void sendPackets(ServerPlayerEntity player) {
        if (this.tabGroup == null) {
            return;
        }

        this.team.setPlayerPrefix(new StringTextComponent(UtilChatColour.translateColourCodes(
                '&',
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getPrefix())
        )));
        this.team.setPlayerSuffix(new StringTextComponent(UtilChatColour.translateColourCodes(
                '&',
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getSuffix())
        )));

        Scoreboard scoreboard = new Scoreboard();

        player.connection.send(new STeamsPacket(this.team, 0));
        player.connection.send(new STeamsPacket(this.team, 2));
        player.connection.send(new STeamsPacket(this.team, Lists.newArrayList(this.parent.getName()), 3));
    }

    public void reCalculateGroup() {
        TabGroup newGroup = this.calculateTabGroup();

        if (!Objects.equals(this.tabGroup, newGroup)) {
            this.tabGroup = newGroup;
            this.setupTeam();
        }
    }

    @Override
    public void load() {
        this.tabGroup = this.calculateTabGroup();

        if (this.tabGroup == null) {
            return;
        }

        this.setupTeam();
    }

    private TabGroup calculateTabGroup() {
        for (TabGroup group : ForgeTabGroupFactory.getGroups()) {
            if (UtilPlayer.hasPermission(this.parent.getParent(), group.getPermission())) {
                return group;
            }
        }

        return null;
    }

    private void setupTeam() {
        Scoreboard scoreboard = new Scoreboard();
        this.team = new ScorePlayerTeam(scoreboard, this.parent.getUuid().toString().substring(0, 15));
        this.team.setAllowFriendlyFire(true);
        this.team.setCollisionRule(Team.CollisionRule.ALWAYS);
        this.team.setDeathMessageVisibility(Team.Visible.NEVER);
        this.team.setPlayerPrefix(new StringTextComponent(UtilChatColour.translateColourCodes('&',
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getPrefix()))));
        this.team.setPlayerSuffix(new StringTextComponent(UtilChatColour.translateColourCodes('&',
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getSuffix()))));
        this.team.setSeeFriendlyInvisibles(false);
    }

    @Override
    public void save() {}
}
