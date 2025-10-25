# SimpleAutoRestart

![Banner](banner.svg)

<div align="center">

[GitHub](https://github.com/teunjojo/SimpleAutoRestart) | [Modrinth](https://modrinth.com/plugin/simpleautorestart) | [Spigot](https://www.spigotmc.org/resources/simpleautorestart.107932/) | [CurseForge](https://www.curseforge.com/minecraft/bukkit-plugins/simpleautorestart) | [Wiki](https://github.com/teunjojo/SimpleAutoRestart/wiki)

</div>

[![GitHub Release](https://img.shields.io/github/v/release/teunjojo/SimpleAutoRestart?style=for-the-badge)](https://github.com/teunjojo/SimpleAutoRestart/releases)
[![Build SimpleAutoRestart](https://img.shields.io/github/actions/workflow/status/teunjojo/SimpleAutoRestart/maven.yml?label=dev%20build&style=for-the-badge)](https://github.com/teunjojo/SimpleAutoRestart/actions/workflows/maven.yml)

![License](https://img.shields.io/github/license/teunjojo/simpleautorestart?style=for-the-badge)

[![bukkit](https://badges.penpow.dev/badges/supported/bukkit/compact-minimal.svg)](https://bukkit.org/)
[![spigot](https://badges.penpow.dev/badges/supported/spigot/compact-minimal.svg)](https://spigot.org/)
[![papermc](https://badges.penpow.dev/badges/supported/paper/compact-minimal.svg)](https://bukkit.org/)

A Simple plugin to automatically restart your server at scheduled times with customizable warnings.

## Features

- Schedule automatic restarts at a specific times and days. (eg. `Wednesday;00:00`, `Sunday;3:00`)
- Customizable warning messages and titles (supports [MiniMessage](https://docs.advntr.dev/minimessage/) and legacy formatting).
- Configurable restart command(s) to fit your setup.
- Real-time based (not tick-based) scheduling for better accuracy.
- Supports all versions of from 1.8.8+
- Open Source!

## Installation

1. Configure your server restart script in `spigot.yml` (See [PaperMC Docs](https://docs.papermc.io/paper/reference/spigot-configuration/#settings_restart_script))
2. Download the latest `SimpleAutoRestart-<version>.jar`.
3. Place the JAR file into your server's `plugins/` folder.
4. Start or restart the server.
5. Configure `plugins/SimpleAutoRestart/config.yml` (See the [wiki](https://github.com/teunjojo/SimpleAutoRestart/wiki) for more information)

## User Interface

### Default Warning Messages

![User](https://i.imgur.com/7yjcaTg.jpeg)

### Admin Commands

![Status](https://i.imgur.com/4Kl6Rgy.jpeg)

## Troubleshooting

### It counts down, but doesn't restart

Make sure you have configured restart-script in your `spigot.yml`.

## Support

If you have any issues, bugs or questions. Feel free to [create an issue ticket](https://github.com/teunjojo/SimpleAutoRestart/issues/new) or message me on [discord](https://discord.com/users/306533562444939265).
