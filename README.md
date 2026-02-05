# MoggedKits

> The only kit plugin that guarantees +20% jawline definition and +10 confidence after every `/kit` usage.
> Powered by pure **anabolic Java**.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-orange.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Paper](https://img.shields.io/badge/Paper-1.21.5+-blue.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-1.2_Final-green.svg)](https://github.com/Verschuls/MoggedKits)

---

## Project Status: Community Maintained

**MoggedKits v1.2** marks the final release from the original author. The plugin is now feature-complete, stable, and **open to community maintenance**.

This project is now community-driven. Feel free to fork, contribute, and maintain.

For future products and projects, check out **[verschuls.xyz](https://verschuls.xyz)** — more stuff coming soon.

---

## What is this?

**MoggedKits** is a fast, flexible kit plugin that mogs the competition while you sleep.

No bloat, no 15 dependencies, no soy code — just **clean architecture and giga features**.

### Why MoggedKits?
- **Performance**: Built for speed, not bloat. Other kit plugins could never.
- **Scalability**: H2 for solo grinders, Redis for network chads
- **Simplicity**: Config so easy even your mewing streak won't break
- **Quality**: Meme branding, gigachad code underneath

---

## Features

- **Cooldown system** — fair timers that even natty players respect
- **Kit GUI** — browse your loadouts in style, left-click to claim, right-click to preview
- **Kit Preview** — see what you're getting before you commit (unlike your ex)
- **Permission nodes** — control who gets the gains and who stays beta
- **Easy-to-edit config** — YAML gang rise up
- **Multi-server ready** — Redis support for your sigma network empire
- **Auto-equip armor** — instant drip application, no fumbling required
- **Cross-server config sync** — edit once, mog everywhere via Redis PubSub
- **PlaceholderAPI support** — integrate kit data into your placeholders
- **MoggedAPI** — hook into kit events and data from your own plugins
- **Economy support** — Vault integration for buyable kits

<details>
<summary><b>Developer API Setup</b></summary>

### Installation

Add JitPack repository and dependency to your build file:

**Gradle (Kotlin DSL)**
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.verschuls.MoggedKits:api:VERSION")
}
```

**Gradle (Groovy)**
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.verschuls.MoggedKits:api:VERSION'
}
```

**Maven**
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.verschuls</groupId>
        <artifactId>MoggedKits</artifactId>
        <version>VERSION</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Replace `VERSION` with a release tag (e.g., `1.2`).

### Usage

**Access the API:**
```java
MKAPIProvider api = MoggedKitsAPI.getAPI();

// Get all kits
List<MKit> kits = api.getKits();

// Get specific kit
Optional<MKit> kit = api.getKit("starter");

// Cooldown management
long remaining = api.getCooldown(player, "starter");
api.setCooldown(player, "starter", 3600); // 1 hour
```

**Listen to events:**
```java
@EventHandler
public void onKitClaim(KitClaimEvent event) {
    Player player = event.getPlayer();
    MKit kit = event.getKit();

    // Cancel if needed
    event.setCancelled(true);
}

@EventHandler
public void onKitsLoaded(KitsLoadedEvent event) {
    List<MKit> kits = event.getKits();
    // Do something when kits are loaded
}
```

</details>

---

## Commands & Permissions

### Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/kits` | Open kit selection GUI | - |
| `/kit` | Open kit selection GUI | - |
| `/kit <name>` | Claim a specific kit | `moggedkits.kit.<name>` |
| `/buykit <name>` | Purchase a kit (requires Vault) | - |
| `/moggedkits` | Show admin subcommands | `moggedkits.admin` |
| `/moggedkits reload` | Reload all configs and kits | `moggedkits.admin` |
| `/moggedkits storage` | Show storage backend info | `moggedkits.admin` |
| `/moggedkits resetcooldown <kit> [player]` | Reset cooldown for kit (use `all` for all kits) | `moggedkits.admin` |
| `/moggedkits give <kit> [player]` | Give kit bypassing permissions/cooldowns | `moggedkits.admin` |
| `/moggedkits addaccess <kit> [player]` | Grant permanent kit access to player | `moggedkits.admin` |
| `/moggedkits removeaccess <kit> [player]` | Revoke kit access from player | `moggedkits.admin` |
| `/moggedkits migrate` | Migrate old YAML data to H2 | `moggedkits.admin` |

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
| **H2** | Local | Single-server, default storage |
| **Redis** | Network | Multi-server or "serious mogger" setups |

No MySQL, no Oracle, no enterprise bullshit. Just speed and testosterone.

### Why this choice?
- **H2**: Fast embedded database, zero config, perfect for most servers
- **Redis**: Lightning-fast, network-ready, handles thousands of players

### Migrating from YAML Storage

**YAML storage has been removed.** If you previously used YAML storage, you must migrate to H2:

1. Update to the latest version
2. Run `/moggedkits migrate` in-game
3. Confirm the migration when prompted
4. The `player_data` folder will be automatically deleted after successful migration

### Redis Features
- Cooldown sync across all servers
- Config/kit file sync via PubSub (edit once, syncs everywhere)
- LZ4 compression for efficient network transfer
- Instance identification for debugging

---

## Installation

### Requirements
- **Server**: Paper 1.21.5+ (we don't negotiate with outdated software)
- **Java**: 21+ (modern gains only)
- **Optional**: Redis server (for network domination)
- **Optional**: Vault + Economy plugin (for buyable kits)

### Steps
1. Acquire the latest `.jar`
2. Drop it into your `/plugins` folder
3. Restart your server (yeah, real men don't use `/reload`)
4. Configure in `plugins/MoggedKits/`
5. Type `/kit` and ascend

### Redis Setup (Optional)
1. Configure `redis.yml` with your Redis server details
2. Set host and port — plugin auto-detects and switches from H2
3. All servers connecting to same Redis will sync automatically

---

## Contributing

MoggedKits is now fully community maintained. Feel free to:

1. **Fork** the repository
2. **Clone** your fork locally
3. **Create a branch** for your changes
4. **Make your edits** (bug fixes, improvements, etc.)
5. **Submit a Pull Request** to the main branch

### Guidelines
- Keep changes focused and minimal
- Test your changes before submitting
- Follow existing code style
- Document any new features or changes

All contributions are welcome — from bug fixes to documentation improvements.

---

## FAQ

<details>
<summary><b>Is this plugin actually good or just memes?</b></summary>

The branding is memes, the code is quality. We wouldn't waste your time with garbage.
</details>

<details>
<summary><b>Why H2/Redis only? What about MySQL?</b></summary>

For kit cooldowns and player data, you don't need a full SQL database. H2 is fast and embedded, Redis is network-ready. We're keeping it lean.
</details>

<details>
<summary><b>Will there be a Spigot/Bukkit version?</b></summary>

No. Paper is the focus. Paper has better APIs and performance. Upgrade your server.
</details>

<details>
<summary><b>What about Folia?</b></summary>

No. Focus is on Paper.
</details>

<details>
<summary><b>Does Redis sync kits between servers?</b></summary>

Yes. Edit, save, do reload on given instance and watch as files synchronize across all connected servers.
</details>

<details>
<summary><b>Can I use this on production?</b></summary>

Yes. v1.2 is stable and production-ready.
</details>

<details>
<summary><b>I need help!</b></summary>

Check the [GitHub Issues](https://github.com/Verschuls/MoggedKits/issues) for existing solutions or open a new issue. Community contributions and support are welcome.
</details>

---

## License

**GPL v3 License** — open source, copyleft, and free like your testosterone levels.

See [LICENSE](LICENSE) file for details.

---

## Credits

**Originally created by [Verschuls](https://verschuls.xyz)**

Fuelled by memes, insomnia, and unreasonable amounts of taurine.

*Don't let the memes fool you — the code underneath is cleaner than your gym routine.*

---

<div align="center">

**MOG or BE MOGGED?**

*Drop a star if this plugin carried your server harder than your last ranked game*

</div>
