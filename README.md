# VoiceGroupCleaner

A Paper/Spigot/Purpur plugin that keeps [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) groups
under control. Players can only be in the voice groups you allow — anything else gets cleaned up automatically,
and anyone who tries to make their own group gets bounced straight back out.

## Features

- **Allow-list based cleanup** — any Simple Voice Chat group not on your list is removed on a timer.
- **Instant group-create guard** — the moment a player creates a disallowed group, they're kicked out of it,
  see an actionbar warning, hear a sound, and any extra commands you've configured run for them.
- **Fully configurable reaction** — no command is hardcoded. Everything that happens when a player gets kicked
  (which commands run, what sound plays, what text they see) is set in `config.yml`.
- **`/vgclean` admin command** — reload, list, manually clean, and manage the allow-list in-game.
- **Placeholders** — `%player%`, `%displayname%`, `%group%`, `%count%`, `%flags%` across commands and messages.

## Installation

1. Install [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) on your server first.
2. Drop `VoiceGroupCleaner-<version>.jar` into your `plugins/` folder.
3. Restart the server, then edit `plugins/VoiceGroupCleaner/config.yml` to your liking.
4. `/vgclean reload` to apply changes without a restart.

## Configuration & commands

Full breakdown of every config option and the `/vgclean` command is in [`docs/commands.md`](docs/commands.md),
also linked directly from the top of `config.yml`.

## Compatibility

Compiled against the Paper API, which stays backwards compatible within a Minecraft version line, so one jar
covers **Paper, Purpur, Spigot, and Bukkit-based servers** from 1.20 through the latest release. There is no
Quilt/Fabric build — Quilt is a mod loader, not a plugin loader, and this plugin can't run on it without a
Bukkit-compatibility mod like Cardboard, which isn't officially supported here.

## Building from source

```bash
mvn clean package
```

The built jar lands in `target/VoiceGroupCleaner-<version>.jar`.

## Author

Built and maintained by **1amir2026**.
Telegram: [@ItzAmiRxD](https://t.me/ItzAmiRxD)

## License

See [LICENSE](LICENSE).
