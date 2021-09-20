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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

import java.util.Objects;

public class TabAttribute extends AbstractForgeAttribute<ForgeTAB> {

    private ScorePlayerTeam team;
    private TabGroup tabGroup;

    public TabAttribute(ForgeTAB manager, EnvyPlayer<?> parent) {
        super(manager, (ForgeEnvyPlayer) parent);
    }

    public void sendPackets(EntityPlayerMP player) {
        if (this.tabGroup == null) {
            return;
        }

        this.team.setPrefix(UtilChatColour.translateColourCodes('&',
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getPrefix())));
        this.team.setSuffix(UtilChatColour.translateColourCodes('&',
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getSuffix())));

        Scoreboard scoreboard = new Scoreboard();

        player.connection.sendPacket(new SPacketTeams(this.team, 0));
        player.connection.sendPacket(new SPacketTeams(this.team, 2));
        player.connection.sendPacket(new SPacketTeams(this.team, Lists.newArrayList(this.parent.getName()), 3));
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
        this.team.setDeathMessageVisibility(Team.EnumVisible.NEVER);
        this.team.setPrefix(UtilChatColour.translateColourCodes('&',
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getPrefix())));
        this.team.setSuffix(UtilChatColour.translateColourCodes('&',
                UtilPlaceholder.replaceIdentifiers(this.parent.getParent(), this.tabGroup.getSuffix())));
        this.team.setSeeFriendlyInvisiblesEnabled(false);
    }

    @Override
    public void save() {}
}
