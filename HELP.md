# Help

**Nothing happens when a group is created.**
Check `group-create-guard.enabled: true` in `config.yml`, and make sure Simple Voice Chat is fully loaded
(look for "Simple Voice Chat API acquired" in the console) before testing.

**The kick command doesn't run.**
Commands in `group-create-guard.commands` run *as the player*. If a command needs a permission the player
doesn't have, it will silently fail from their perspective : check console with `debug: true` for the
per-command Ran/Failed log line.

**Sound doesn't play.**
`sound` must be a valid name from the [Bukkit `Sound` enum](https://jd.papermc.io/paper/1.21/org/bukkit/Sound.html),
not a resource-pack sound key. If you want a resource-pack sound instead, use a `/playsound` command inside
`group-create-guard.commands` instead of the built-in `sound` field.

**I want a group name change to apply without a restart.**
`/vgclean reload` re-reads `config.yml` and restarts the cleanup timer : no restart needed.
or use `/vgclean keep <group-name>` no reload needed.

**Where's the full command/config list?**
[`docs/commands.md`](docs/commands.md).

**Contact**
Telegram: [@ItzAmiRxD](https://t.me/ItzAmiRxD)
