package com.envyful.tab.forge.player;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.forge.player.attribute.AbstractForgeAttribute;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.envyful.tab.api.TabGroup;
import com.envyful.tab.forge.ForgeTAB;
import com.envyful.tab.forge.api.ForgeTabGroupFactory;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

import java.util.Objects;

public class TabAttribute extends AbstractForgeAttribute<ForgeTAB> {

    private PlayerTeam team;
    private TabGroup tabGroup;

    public TabAttribute(ForgeTAB manager, ForgePlayerManager playerManager) {
        super(manager, playerManager);
    }

    public void sendPackets(ServerPlayer player) {
        if (this.tabGroup == null) {
            return;
        }

        this.team.setPlayerPrefix(UtilChatColour.colour(
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getPrefix())
        ));
        this.team.setPlayerSuffix(UtilChatColour.colour(
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getSuffix())
        ));

        player.connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(this.team, true));
        player.connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(this.team, false));
        player.connection.send(ClientboundSetPlayerTeamPacket.createPlayerPacket(this.team, this.parent.getName(), ClientboundSetPlayerTeamPacket.Action.ADD));
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
        this.team = new PlayerTeam(scoreboard, this.parent.getUuid().toString().substring(0, 15));
        this.team.setAllowFriendlyFire(true);
        this.team.setCollisionRule(Team.CollisionRule.ALWAYS);
        this.team.setDeathMessageVisibility(Team.Visibility.NEVER);
        this.team.setPlayerPrefix(UtilChatColour.colour(
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getPrefix())));
        this.team.setPlayerSuffix(UtilChatColour.colour(
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getSuffix())));
        this.team.setSeeFriendlyInvisibles(false);
    }

    @Override
    public void save() {}
}
