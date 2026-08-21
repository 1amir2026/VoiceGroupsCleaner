package me.itzamirxd.voicegroupcleaner;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.events.CreateGroupEvent;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.function.Consumer;

public class GroupCreateGuard implements Consumer<CreateGroupEvent> {

    private final VoiceGroupCleaner plugin;
    private final ConfigManager config;

    public GroupCreateGuard(VoiceGroupCleaner plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void accept(CreateGroupEvent event) {
        if (!config.isCreateGuardEnabled()) {
            return;
        }

        Group group = event.getGroup();
        if (group == null || config.isAllowed(group.getName())) {
            return;
        }

        VoicechatConnection connection = event.getConnection();
        if (connection == null || connection.getPlayer() == null) {
            return;
        }

        UUID playerId = connection.getPlayer().getUuid();
        String groupName = group.getName();

        Bukkit.getScheduler().runTaskLater(plugin,
                () -> GroupEnforcer.kick(plugin, config, Bukkit.getPlayer(playerId), groupName),
                config.getCreateGuardDelayTicks());
    }
}
