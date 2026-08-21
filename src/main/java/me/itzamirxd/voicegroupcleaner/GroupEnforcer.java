package me.itzamirxd.voicegroupcleaner;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class GroupEnforcer {

    private GroupEnforcer() {
    }

    public static void kick(VoiceGroupCleaner plugin, ConfigManager config, Player player, String groupName) {
        if (player == null || !player.isOnline()) {
            return;
        }

        String displayName = player.getDisplayName();

        for (String rawCommand : config.getCreateGuardCommands()) {
            String command = applyPlaceholders(rawCommand, player, groupName, displayName);
            boolean ran = Bukkit.dispatchCommand(player, command);
            if (config.isDebug()) {
                plugin.getLogger().info((ran ? "Ran " : "Failed to run ") + "'" + command + "' for "
                        + player.getName() + " (group: " + groupName + ")");
            }
        }

        String actionbar = config.message("group-create-blocked",
                "%group%", groupName, "%player%", player.getName(), "%displayname%", displayName);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));

        if (config.isCreateGuardSoundEnabled()) {
            Sound sound = config.getCreateGuardSound();
            if (sound != null) {
                player.playSound(player.getLocation(), sound, config.getCreateGuardSoundVolume(), config.getCreateGuardSoundPitch());
            }
        }
    }

    private static String applyPlaceholders(String text, Player player, String groupName, String displayName) {
        return text.replace("%player%", player.getName())
                .replace("%displayname%", displayName)
                .replace("%group%", groupName);
    }
}
