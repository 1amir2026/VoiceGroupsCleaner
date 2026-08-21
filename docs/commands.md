# <img width="28" height="28" alt="image" src="https://github.com/user-attachments/assets/c35e9b16-60e7-45eb-8914-a590d08d378d" />    VoiceGroupCleaner : Commands & Configuration Reference

## `/vgclean`

Permission: `vgclean.reload` (default: op)

| Command | Description |
|---|---|
| `/vgclean reload` | Reloads `config.yml` and restarts the cleanup timer. |
| `/vgclean list` | Lists every Simple Voice Chat group and whether it's kept, removable, protected, or auto-managed. |
| `/vgclean clean` | Runs a cleanup pass immediately instead of waiting for the timer. |
| `/vgclean keep <name>` | Adds a group name to `allowed-groups` and saves it. |
| `/vgclean remove <name>` | Removes a group name from `allowed-groups` and saves it. |

> [!warning] 
> The /vgclean clean command can sometimes be configured to run automatically.
>
> This is useful because automatically removing groups may occasionally cause issues when you want make groups without SimpleVoiceChat plugin and With Another plugins/scripts. ( e.g: VoiceServer plugin ) 
> 
> However, groups with SimpleVoiceChat Plugin that need to be removed—especially groups created by non-OP players will still be removed within seconds, so unnecessary groups won't remain for long.

## `config.yml`

| Key | What it does |
|---|---|
| `check-interval` | How often (in ticks, 20 = 1 second) the cleanup sweep runs. |
| `debug` | Logs every action the plugin takes when `true`. |
| `ignore-password-protected` | Password-protected groups are never auto-removed when `true`. |
| `allowed-groups` | The only group names players are allowed to be in. Everything else is fair game for cleanup. |
| `group-create-guard.enabled` | Turns the instant kick-on-create behavior on/off. |
| `group-create-guard.delay-ticks` | Delay before acting on a disallowed group. |
| `group-create-guard.commands` | List of commands run **as the player** when they get kicked. Add as many as you like, one per line : see below. |
| `group-create-guard.sound` / `sound-volume` / `sound-pitch` | The sound played on the player when they're kicked. Any [Bukkit `Sound`](https://jd.papermc.io/paper/1.21/org/bukkit/Sound.html) name works. |
| `messages.*` | Every player-facing string, `&`-color-coded. |

### `group-create-guard.commands`

Runs in order, as the player themselves (not console), every time someone gets kicked out of a disallowed group.
The default just makes them leave voice chat:

```yaml
group-create-guard:
  commands:
    - "voicechat leave"
```

You can add more. For example, to also give a warning in chat and play a sound through a command instead of
(or alongside) the built-in `sound` setting:

```yaml
group-create-guard:
  commands:
    - "voicechat leave"
    - "playsound minecraft:block.anvil.land player %player% ~ ~ ~ 1 1"
    - "tell %player% &7Custom voice groups aren't allowed here."
```

Available placeholders in these commands:

- `%player%` : the player's username.
- `%displayname%` : the player's display name (nickname, if they have one).

### Placeholders in `messages`

| Placeholder | Available in |
|---|---|
| `%group%` | `group-create-blocked`, `keep-*`, `remove-*` |
| `%player%` | `group-create-blocked` |
| `%displayname%` | `group-create-blocked` |
| `%count%` | `list-header` |
| `%flags%` | `list-entry-flags` |

### Actionbar text

`messages.group-create-blocked` is shown in the actionbar (not chat) the instant a player is kicked out of a
group they just created. It's plain colored text with no chat prefix in front of it : it's meant to read as a
standalone actionbar warning, not a chat message:

```yaml
messages:
  group-create-blocked: "&cYou can't create custom voice chat groups! (%group%)"
```

> [!TIP]
> The default `config.yml` is **safe to use** and usually requires little to no modification.
>
> However, it also provides **a wide range of options** for customization. You can change many settings to better suit your needs and configure the plugin exactly the way you want.

