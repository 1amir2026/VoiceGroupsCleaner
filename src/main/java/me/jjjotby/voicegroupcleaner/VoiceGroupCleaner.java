package me.jjjotby.voicegroupcleaner;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.CreateGroupEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoiceGroupCleaner extends JavaPlugin implements VoicechatPlugin {

    private static final String SVC_PLUGIN_ID = "voicegroupcleaner";

    private ConfigManager configManager;
    private GroupCleanupTask cleanupTask;
    private VoicechatServerApi voicechatApi;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);

        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service == null) {
            getLogger().severe("Simple Voice Chat's BukkitVoicechatService is not available. " +
                    "Is the Simple Voice Chat plugin/mod installed and enabled? Disabling VoiceGroupCleaner.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        service.registerPlugin(this);

        ReloadCommand commandExecutor = new ReloadCommand(this);
        getCommand("vgclean").setExecutor(commandExecutor);
        getCommand("vgclean").setTabCompleter(commandExecutor);

        getLogger().info("VoiceGroupCleaner enabled, waiting for Simple Voice Chat to finish initializing...");
    }

    @Override
    public void onDisable() {
        stopCleanupTask();
    }

    @Override
    public String getPluginId() {
        return SVC_PLUGIN_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        if (!(api instanceof VoicechatServerApi)) {
            getLogger().severe("Received a non-server VoicechatApi instance. VoiceGroupCleaner will not run.");
            return;
        }
        this.voicechatApi = (VoicechatServerApi) api;
        getLogger().info("Simple Voice Chat API acquired. Starting cleanup task.");
        startCleanupTask();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(CreateGroupEvent.class, new GroupCreateGuard(this, configManager));
    }

    public void startCleanupTask() {
        stopCleanupTask();
        if (voicechatApi == null) {
            getLogger().warning("Cannot start cleanup task yet: Simple Voice Chat API isn't ready.");
            return;
        }
        cleanupTask = new GroupCleanupTask(this, voicechatApi, configManager);
        cleanupTask.runTaskTimer(this, 40L, configManager.getCheckInterval());
    }

    public void stopCleanupTask() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
    }

    public void reload() {
        configManager.reload();
        startCleanupTask();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public VoicechatServerApi getVoicechatApi() {
        return voicechatApi;
    }
}
