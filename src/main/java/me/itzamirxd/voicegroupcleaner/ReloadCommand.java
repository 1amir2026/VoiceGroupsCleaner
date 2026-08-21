package me.itzamirxd.voicegroupcleaner;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("reload", "list", "clean", "keep", "remove");

    private final VoiceGroupCleaner plugin;

    public ReloadCommand(VoiceGroupCleaner plugin) {
        this.plugin = plugin;
    }

    private ConfigManager cfg() {
        return plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("vgclean.reload")) {
            sender.sendMessage(cfg().message("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                sender.sendMessage(cfg().message("reloaded"));
                return true;

            case "list":
                return handleList(sender);

            case "clean":
                return handleClean(sender);

            case "keep":
                return handleKeep(sender, args);

            case "remove":
                return handleRemove(sender, args);

            default:
                sendUsage(sender);
                return true;
        }
    }

    private boolean handleList(CommandSender sender) {
        VoicechatServerApi api = plugin.getVoicechatApi();
        if (api == null) {
            sender.sendMessage(cfg().message("svc-not-ready"));
            return true;
        }

        Collection<Group> groups = api.getGroups();
        if (groups.isEmpty()) {
            sender.sendMessage(cfg().message("list-empty"));
            return true;
        }

        sender.sendMessage(cfg().message("list-header", "%count%", String.valueOf(groups.size())));
        for (Group group : groups) {
            String tag = statusTag(group);
            String flags = (group.isPersistent() ? "persistent" : "temporary")
                    + (group.hasPassword() ? ", password" : "");
            String flagsText = cfg().message("list-entry-flags", "%flags%", flags);
            sender.sendMessage(tag + " " + group.getName() + " " + flagsText);
        }
        return true;
    }

    private String statusTag(Group group) {
        if (!group.isPersistent()) {
            return cfg().message("list-entry-auto");
        }
        if (cfg().isAllowed(group.getName())) {
            return cfg().message("list-entry-kept");
        }
        if (cfg().isIgnorePasswordProtected() && group.hasPassword()) {
            return cfg().message("list-entry-protected");
        }
        return cfg().message("list-entry-removable");
    }

    private boolean handleClean(CommandSender sender) {
        if (plugin.getVoicechatApi() == null) {
            sender.sendMessage(cfg().message("svc-not-ready"));
            return true;
        }
        new GroupCleanupTask(plugin, plugin.getVoicechatApi(), cfg()).runTask(plugin);
        sender.sendMessage(cfg().message("clean-done"));
        return true;
    }

    private boolean handleKeep(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(cfg().message("keep-usage"));
            return true;
        }
        String groupName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        boolean added = cfg().getAllowedGroups().add(groupName);
        if (added) {
            cfg().saveAllowedGroups();
            sender.sendMessage(cfg().message("keep-added", "%group%", groupName));
        } else {
            sender.sendMessage(cfg().message("keep-exists", "%group%", groupName));
        }
        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(cfg().message("remove-usage"));
            return true;
        }
        String groupName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        boolean removed = cfg().getAllowedGroups().remove(groupName);
        if (removed) {
            cfg().saveAllowedGroups();
            sender.sendMessage(cfg().message("remove-removed", "%group%", groupName));
        } else {
            sender.sendMessage(cfg().message("remove-not-found", "%group%", groupName));
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(cfg().message("usage-header"));
        sender.sendMessage(cfg().message("usage-reload"));
        sender.sendMessage(cfg().message("usage-list"));
        sender.sendMessage(cfg().message("usage-clean"));
        sender.sendMessage(cfg().message("usage-keep"));
        sender.sendMessage(cfg().message("usage-remove"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(partial))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("keep") || args[0].equalsIgnoreCase("remove"))) {
            VoicechatServerApi api = plugin.getVoicechatApi();
            if (api == null) {
                return new ArrayList<>();
            }
            String partial = args[1].toLowerCase();
            return api.getGroups().stream()
                    .map(Group::getName)
                    .filter(n -> n.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
