# SimpleAutoRestart

![Banner](banner.svg)

[![GitHub Release](https://img.shields.io/github/v/release/teunjojo/SimpleAutoRestart?style=for-the-badge)](https://github.com/teunjojo/SimpleAutoRestart/releases)
[![Build SimpleAutoRestart](https://img.shields.io/github/actions/workflow/status/teunjojo/SimpleAutoRestart/maven.yml?label=dev%20build&style=for-the-badge)](https://github.com/teunjojo/SimpleAutoRestart/actions/workflows/maven.yml)

![License](https://img.shields.io/github/license/teunjojo/simpleautorestart?style=for-the-badge)

[![bukkit](https://badges.penpow.dev/badges/supported/bukkit/compact-minimal.svg)](https://bukkit.org/)
[![spigot](https://badges.penpow.dev/badges/supported/spigot/compact-minimal.svg)](https://spigot.org/)
[![papermc](https://badges.penpow.dev/badges/supported/paper/compact-minimal.svg)](https://bukkit.org/)

## Features

- Schedule automatic restarts at a specific times and days. (eg. `Wednesday;00:00`, `Sunday;3:00`)
- Customizable warning messages/titles.
- Canceling/resuming restarts
- [MiniMessage](https://docs.advntr.dev/minimessage/) support
- Customizable restart commands
- Realtime based and not tick based for more accuracy.
- Open Source!

## What it looks like in-game (although you can customize this):

![Chat](https://i.imgur.com/ZjQq3sq.png)

## What it looks like in the server console (although you can customize this):

![Console](https://i.imgur.com/Kshy5U5.png)

## Installation

1. Set the startup script in [spigot.yml](https://docs.papermc.io/paper/reference/spigot-configuration/#settings_restart_script)
2. Download `SimpleAutoRestart-x.x.x-jar` from the releases page.
3. Place the JAR file in the plugins folder of your server.
4. Start or restart the server.
5. Configure the plugin by editing the config.yml file in the plugins/SimpleAutoRestart folder.

[![bstats](https://bstats.org/signatures/bukkit/simpleautorestart.svg)](https://bstats.org/plugin/bukkit/SimpleAutoRestart/17760)

## Troubleshooting

### It counts down, but doesn't restart

Make sure you have configured `restart-script` in [`spigot.yml`](https://docs.papermc.io/paper/reference/spigot-configuration/#settings_restart_script)

## Support

If you have any issues or questions. Feel free to [create an issue ticket](https://github.com/teunjojo/SimpleAutoRestart/issues/new). 
