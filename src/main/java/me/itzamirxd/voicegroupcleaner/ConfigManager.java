package me.itzamirxd.voicegroupcleaner;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigManager {

    private final VoiceGroupCleaner plugin;

    private long checkInterval;
    private boolean debug;
    private boolean ignorePasswordProtected;
    private final Set<String> allowedGroups = new HashSet<>();
    private final Map<String, String> messages = new HashMap<>();

    private boolean createGuardEnabled;
    private long createGuardDelayTicks;
    private final List<String> createGuardCommands = new ArrayList<>();
    private boolean createGuardSoundEnabled;
    private Sound createGuardSound;
    private float createGuardSoundVolume;
    private float createGuardSoundPitch;

    public ConfigManager(VoiceGroupCleaner plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        this.checkInterval = cfg.getLong("check-interval", 100);
        this.debug = cfg.getBoolean("debug", false);
        this.ignorePasswordProtected = cfg.getBoolean("ignore-password-protected", true);

        List<String> groups = cfg.getStringList("allowed-groups");
        allowedGroups.clear();
        allowedGroups.addAll(groups);

        messages.clear();
        ConfigurationSection section = cfg.getConfigurationSection("messages");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                messages.put(key, section.getString(key, ""));
            }
        }

        if (checkInterval < 1) {
            plugin.getLogger().warning("check-interval must be >= 1, defaulting to 100.");
            checkInterval = 100;
        }

        ConfigurationSection guardSection = cfg.getConfigurationSection("group-create-guard");
        this.createGuardEnabled = guardSection == null || guardSection.getBoolean("enabled", true);
        this.createGuardDelayTicks = guardSection == null ? 20L : guardSection.getLong("delay-ticks", 20L);

        createGuardCommands.clear();
        if (guardSection != null && guardSection.isList("commands")) {
            createGuardCommands.addAll(guardSection.getStringList("commands"));
        } else {
            createGuardCommands.add("voicechat leave");
        }

        ConfigurationSection soundSection = guardSection == null ? null : guardSection.getConfigurationSection("sound");
        this.createGuardSoundEnabled = soundSection == null || soundSection.getBoolean("enabled", true);
        String soundName = soundSection == null ? "BLOCK_ANVIL_LAND" : soundSection.getString("name", "BLOCK_ANVIL_LAND");
        this.createGuardSoundVolume = (float) (soundSection == null ? 1.0 : soundSection.getDouble("volume", 1.0));
        this.createGuardSoundPitch = (float) (soundSection == null ? 1.0 : soundSection.getDouble("pitch", 1.0));

        if (createGuardDelayTicks < 0) {
            plugin.getLogger().warning("group-create-guard.delay-ticks must be >= 0, defaulting to 20.");
            createGuardDelayTicks = 20L;
        }

        try {
            this.createGuardSound = Sound.valueOf(soundName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("group-create-guard.sound.name '" + soundName + "' isn't a valid Sound name, " +
                    "defaulting to BLOCK_ANVIL_LAND.");
            this.createGuardSound = Sound.BLOCK_ANVIL_LAND;
        }
    }

    public long getCheckInterval() {
        return checkInterval;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isIgnorePasswordProtected() {
        return ignorePasswordProtected;
    }

    public Set<String> getAllowedGroups() {
        return allowedGroups;
    }

    public boolean isCreateGuardEnabled() {
        return createGuardEnabled;
    }

    public long getCreateGuardDelayTicks() {
        return createGuardDelayTicks;
    }

    public List<String> getCreateGuardCommands() {
        return createGuardCommands;
    }

    public boolean isCreateGuardSoundEnabled() {
        return createGuardSoundEnabled;
    }

    public Sound getCreateGuardSound() {
        return createGuardSound;
    }

    public float getCreateGuardSoundVolume() {
        return createGuardSoundVolume;
    }

    public float getCreateGuardSoundPitch() {
        return createGuardSoundPitch;
    }

    public boolean isAllowed(String groupName) {
        return allowedGroups.contains(groupName);
    }

    public String message(String key, String... placeholders) {
        String raw = messages.getOrDefault(key, key);
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            raw = raw.replace(placeholders[i], placeholders[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public void saveAllowedGroups() {
        plugin.getConfig().set("allowed-groups", new ArrayList<>(allowedGroups));
        plugin.saveConfig();
    }
}
