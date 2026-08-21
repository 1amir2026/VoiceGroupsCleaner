package me.itzamirxd.voicegroupcleaner;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GroupCleanupTask extends BukkitRunnable {

    private final VoiceGroupCleaner plugin;
    private final VoicechatServerApi api;
    private final ConfigManager config;

    public GroupCleanupTask(VoiceGroupCleaner plugin, VoicechatServerApi api, ConfigManager config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
    }

    @Override
    public void run() {
        List<Group> groups = new ArrayList<>(api.getGroups());
        if (groups.isEmpty()) {
            return;
        }

        for (Group group : groups) {
            if (config.isAllowed(group.getName())) {
                continue;
            }

            if (config.isIgnorePasswordProtected() && group.hasPassword()) {
                if (config.isDebug()) {
                    plugin.getLogger().info("Skipped '" + group.getName() + "' (password-protected).");
                }
                continue;
            }

            List<Player> occupants = getOccupants(group);
            if (!occupants.isEmpty()) {
                handleOccupied(group, occupants);
                continue;
            }

            if (!group.isPersistent()) {
                continue;
            }
            removeIfStillEmpty(group);
        }
    }

    private void handleOccupied(Group group, List<Player> occupants) {
        UUID groupId = group.getId();
        String groupName = group.getName();

        for (Player player : occupants) {
            GroupEnforcer.kick(plugin, config, player, groupName);
        }

        if (!group.isPersistent()) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Group current = api.getGroup(groupId);
            if (current == null) {
                return;
            }
            removeIfStillEmpty(current);
        }, config.getCreateGuardDelayTicks());
    }

    private void removeIfStillEmpty(Group group) {
        if (!getOccupants(group).isEmpty()) {
            if (config.isDebug()) {
                plugin.getLogger().info("Skipped removing '" + group.getName() + "' (still has players).");
            }
            return;
        }
        boolean removed = api.removeGroup(group.getId());
        if (config.isDebug()) {
            if (removed) {
                plugin.getLogger().info("Removed group: " + group.getName());
            } else {
                plugin.getLogger().info("Failed to remove group: " + group.getName()
                        + " (likely became occupied, or is not persistent)");
            }
        }
    }

    private List<Player> getOccupants(Group group) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> {
                    VoicechatConnection connection = api.getConnectionOf(player.getUniqueId());
                    if (connection == null) {
                        return false;
                    }
                    Group playerGroup = connection.getGroup();
                    return playerGroup != null && playerGroup.getId().equals(group.getId());
                })
                .collect(Collectors.toList());
    }
}
