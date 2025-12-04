# MoggedKits

> The only kit plugin that guarantees +20% jawline definition and +10 confidence after every `/kit` usage.
> Powered by pure **anabolic Java**.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Paper](https://img.shields.io/badge/Paper-1.19+-blue.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-1.0-green.svg)](https://github.com/Verschuls/MoggedKits)

---

## What is this?

**MoggedKits** is a fast, flexible kit plugin that mogs the competition while you sleep.

No bloat, no 15 dependencies, no soy code — just **clean architecture and giga features**.

### Why MoggedKits?
- **Performance**: Built for speed, not bloat. Other kit plugins could never.
- **Scalability**: YAML for solo grinders, Redis for network chads
- **Simplicity**: Config so easy even your mewing streak won't break
- **Quality**: Meme branding, gigachad code underneath

---

## Features

### Core (v1.0) — The Full Mogging Package
- `/kit` command with permission-based access
- **Cooldown system** — fair timers that even natty players respect
- **Kit GUI** — browse your loadouts in style, left-click to claim, right-click to preview
- **Kit Preview** — see what you're getting before you commit (unlike your ex)
- **Permission nodes** — control who gets the gains and who stays beta
- **Easy-to-edit config** — YAML gang rise up
- **Multi-server ready** — Redis support for your sigma network empire
- **Auto-equip armor** — instant drip application, no fumbling required
- **Cross-server config sync** — edit once, mog everywhere via Redis PubSub

---

## Commands & Permissions

### Commands

| Command | Description | Permission | Cooldown |
|---------|-------------|------------|----------|
| `/kits` or `/kit` | Open kit selection GUI | - | No |
| `/kit <name>` | Claim a specific kit | `moggedkits.kit.<name>` | Yes (configurable) |
| `/moggedkits` | Admin command | `moggedkits.admin` | No |
| `/moggedkits reload` | Reload all configs and kits | `moggedkits.admin` | No |
| `/moggedkits storage` | Show storage backend info | `moggedkits.admin` | No |

### Permission Nodes
```yaml
moggedkits.*              # Full access - the whole buffet
moggedkits.admin          # Admin perms (reload, storage, all kits)
moggedkits.kit.*          # Access to all kits
moggedkits.kit.<kit-name> # Access to specific kit (auto-generated per kit)
```

**Note**: The default kit (configured in `config.yml`) requires no permission — even betas deserve something.

---

## Storage Modes

| Mode | Type | Use case |
|------|------|----------|
| **YAML** | Local | Single-server, casual mogging |
| **Redis** | Network | Multi-server or "serious mogger" setups |

No MySQL, no Oracle, no enterprise bullshit. Just speed and testosterone.

### Why this choice?
- **YAML**: Simple, no external dependencies, perfect for small servers
- **Redis**: Lightning-fast, network-ready, handles thousands of players

### Redis Features
- Cooldown sync across all servers
- Config/kit file sync via PubSub (edit once, syncs everywhere)
- LZ4 compression for efficient network transfer
- Instance identification for debugging

---

## Support the Grind

This plugin is **free and open source** — always has been, always will be. But if MoggedKits saved you hours of config pain, or you just want to support a solo dev grinding at 4am fueled by taurine and questionable life choices, consider grabbing it on Polymart.
### [Get MoggedKits on Polymart](https://polymart.org)

**What you get for supporting:**
- Early access to experimental builds (hit GitHub ~1 week later)
- Direct support via Discord — actual help, not "read the docs"
- Priority feature requests
- The warm fuzzy feeling of funding more 4am coding sessions

Every purchase helps keep the lights on and the code flowing. No pressure though — the GitHub version will always be free. But if you're feeling generous, your support means more than you know.

*Real talk: indie dev life isn't easy. Your support lets me keep making stuff instead of getting a "real j\*b."*

---

## Installation

### Requirements
- **Server**: Paper 1.19+ (we don't negotiate with outdated software)
- **Java**: 21+ (modern gains only)
- **Optional**: Redis server (for network domination)

### Steps
1. Acquire or build the latest `.jar`
2. Drop it into your `/plugins` folder
3. Restart your server (yeah, real men don't use `/reload`)
4. Configure in `plugins/MoggedKits/`
5. Type `/kit` and ascend

### Redis Setup (Optional)
1. Configure `redis.yml` with your Redis server details
2. Set host and port — plugin auto-detects and switches from YAML
3. All servers connecting to same Redis will sync automatically

---

## Configuration

<details>
<summary><b>Main Config (config.yml)</b></summary>

```yaml
# Config version - touch this and your config gets mogged on next reload
version: 1.0

# The unkillable kit. Delete it? It respawns. Like a cockroach but useful.
# This kit requires no permission - even the most beta players deserve something.
defaultKit: "chad"

# When your inventory is already stuffed like a Thanksgiving turkey:
# true = items get yeeted on the ground (sigma move)
# false = blocked until you clean up your mess like your mom told you to
dropWhenFull: false

# GUI Configuration
main_menu:
  title: "&8» &c&lMOG &8or &c&lBE MOGGED &8«"
  rows: 3
  filler: "GRAY_STAINED_GLASS_PANE"
  kit_slots: [13]

kit_preview:
  filler: "GRAY_STAINED_GLASS_PANE"
  # ... preview settings
```
</details>

<details>
<summary><b>Kit Configuration (kits/kitname.yml)</b></summary>

```yaml
# Cooldown between kit claims (in seconds)
delay: 5

# GUI weight (position in menu, 0 = first)
weight: 0

# GUI title when previewing
guiTitle: '&c&lCHAD GRINDSET &8| &7Choose Your Destiny'

# How your kit appears in the menu
display:
  access:
    name: '&a&lCHAD KIT &7(Available)'
    lore:
      - '&7The ultimate sigma male loadout'
      - ''
      - '&a&lLEFT-CLICK &8» &7Claim your destiny'
      - '&e&lRIGHT-CLICK &8» &7Preview the gains'
    material: NETHERITE_CHESTPLATE
    flags: ['HIDE_ATTRIBUTES']

  denied:
    name: '&c&lCHAD KIT &7(Beta Detected)'
    lore:
      - '&cYou lack the testosterone for this kit'
    material: NETHERITE_CHESTPLATE

  cooldown:
    name: '&e&lCHAD KIT &7(Recharging)'
    lore:
      - '&7Your sigma energy is regenerating'
      - '&e- &7Time remaining: &f%kit_chad_cooldown%'
    material: NETHERITE_CHESTPLATE

# Armor configuration
armor:
  autoEquip: true
  helmet:
    name: '&c&lCHAD CROWN'
    material: DIAMOND_HELMET
    enchants: ['protection:4', 'unbreaking:3']
  # ... more armor pieces

# Items configuration
items:
  DIAMOND_SWORD:
    name: '&c&lEXCALIBRUH'
    lore:
      - '&7The legendary blade of chads'
    enchants: ['sharpness:5', 'fire_aspect:2']
  # ... more items
```
</details>

---

## Roadmap

### v1.0 (Current) — Foundation Arc
- Core kit system with GUI
- Kit preview system
- YAML/Redis storage
- Cooldown & permission system
- Auto-equip armor with fallback to inventory
- Cross-server config sync

### v1.1 (Next) — Bulk Season
- More `/moggedkits` subcommands (give, reset cooldown)
- Performance optimizations
- More customization options

### v1.2+ (Future) — Ascension Arc
- Kit categories/folders
- PlaceholderAPI integration
- Economy support (paid kits for premium moggers)
- One-time kits
- Kit bundles
- Dev API

**Want to suggest a feature?** Join the [Discord](https://dsc.verschuls.xyz)

---

## Contributing

Soon...™

---

## FAQ

<details>
<summary><b>Is this plugin actually good or just memes?</b></summary>

The branding is memes, the code is quality. We wouldn't waste your time with garbage.
</details>

<details>
<summary><b>Why YAML/Redis only? What about MySQL?</b></summary>

For kit cooldowns and player data, you don't need a full SQL database. YAML is simple, Redis is fast. We're keeping it lean.
</details>

<details>
<summary><b>Will there be a Spigot/Bukkit version?</b></summary>

Maybe eventually, but Paper is the focus. Paper has better APIs and performance. Upgrade your server.
</details>

<details>
<summary><b>What about Folia?</b></summary>

No. Focus is on Paper. Maybe one day, but don't hold your breath.
</details>

<details>
<summary><b>Does Redis sync kits between servers?</b></summary>

Yes. Edit, save, do reload on given instance and watch as files synchronize across all connected servers.
</details>

<details>
<summary><b>Can I use this on production?</b></summary>

Mostly yes. There could be a few bugs but most issues are fixed. Use at your own discretion.
</details>

<details>
<summary><b>I need help!</b></summary>

Free support isn't offered due to time constraints. If you want dedicated support, consider purchasing on [Polymart](https://polymart.org). Buyers receive:
- Direct support via Discord
- Early access to experimental builds (reach GitHub a week later)
- Private features bound to you before public release
</details>

---

## Bug Reports

Found a bug? Report it here:
- **Issues**: [GitHub Issues](https://github.com/Verschuls/MoggedKits/issues)
- **Website**: [Discord](https://dsc.verschuls.xyz)

When reporting bugs, include:
- Server version & platform
- Plugin version
- Steps to reproduce
- Error logs (if any)
- Storage mode (YAML/Redis)

---

## License

**GPL v3 License** — open source, copyleft, and free like your testosterone levels.

See [LICENSE](LICENSE) file for details.

---

## Credits

**Created by [Verschuls](https://verschuls.xyz)**

Fuelled by memes, insomnia, and unreasonable amounts of taurine.

*Don't let the memes fool you — the code underneath is cleaner than your gym routine.*

---

<div align="center">

**MOG or BE MOGGED**

*Drop a star if this plugin carried your server harder than your last ranked game*

</div>
