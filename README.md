# MoggedKits

> The only kit plugin that guarantees +20% jawline definition and +10 confidence after every `/kit` usage.
> Powered by pure **anabolic Java**.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-orange.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Paper](https://img.shields.io/badge/Paper-1.21.5+-blue.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-1.1.1-green.svg)](https://github.com/Verschuls/MoggedKits)

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
    compileOnly("com.github.verschuls.MoggedKits:VERSION")
}
```

**Gradle (Groovy)**
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.verschuls.MoggedKits:VERSION'
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

Replace `VERSION` with a release tag.

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

| Command                     | Description                                      | Permission | Cooldown |
|-----------------------------|--------------------------------------------------|------------|----------|
| `/kits` or `/kit`           | Open kit selection GUI                           | - | No |
| `/kit <name>`               | Claim a specific kit                             | `moggedkits.kit.<name>` | Yes (configurable) |
| `/moggedkits`               | Admin command                                    | `moggedkits.admin` | No |
| `/moggedkits reload`        | Reload all configs and kits                      | `moggedkits.admin` | No |
| `/moggedkits storage`       | Show storage backend info                        | `moggedkits.admin` | No |
| `/moggedkits resetcooldown` | Resets cooldown for kit or all kits for a player | `moggedkits.admin` | No |
| `/moggedkits give`          | Give player kit without permissions              | `moggedkits.admin` | No |

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
### [Get MoggedKits on Polymart](https://polymart.org/product/8941/moggedkits)

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
- **Server**: Paper 1.21.5+ (we don't negotiate with outdated software)
- **Java**: 21+ (modern gains only)
- **Optional**: Redis server (for network domination)

### Steps
1. Acquire the latest `.jar`
2. Drop it into your `/plugins` folder
3. Restart your server (yeah, real men don't use `/reload`)
4. Configure in `plugins/MoggedKits/`
5. Type `/kit` and ascend

### Redis Setup (Optional)
1. Configure `redis.yml` with your Redis server details
2. Set host and port — plugin auto-detects and switches from YAML
3. All servers connecting to same Redis will sync automatically

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
- PlaceholderAPI integration
- Economy support (paid kits for premium moggers)
- One-time kits
- Dev API
- H2 in favor of YamlStorage
- MySQL/MariaDB if plugin gets bigger

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

Free support isn't offered due to time constraints. If you want dedicated support, consider purchasing on [Polymart](https://polymart.org/product/8941/moggedkits). Buyers receive:
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

**MOG or BE MOGGED?**

*Drop a star if this plugin carried your server harder than your last ranked game*

</div>
