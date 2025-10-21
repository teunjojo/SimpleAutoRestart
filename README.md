# SimpleAutoRestart

[![GitHub Release](https://img.shields.io/github/v/release/teunjojo/SimpleAutoRestart?style=for-the-badge)](https://github.com/teunjojo/SimpleAutoRestart/releases)
[![Build SimpleAutoRestart](https://img.shields.io/github/actions/workflow/status/teunjojo/SimpleAutoRestart/maven.yml?label=dev%20build&style=for-the-badge)](https://github.com/teunjojo/SimpleAutoRestart/actions/workflows/maven.yml)

[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/teunjojo/SimpleAutoRestart)
[![spigot](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/spigot_vector.svg)](https://www.spigotmc.org/resources/simpleautorestart.107932/)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/plugin/simpleautorestart)
[![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/bukkit-plugins/simpleautorestart)

[![bukkit](https://badges.penpow.dev/badges/supported/bukkit/compact.svg)](https://bukkit.org/)
[![folia](https://badges.penpow.dev/badges/unsupported/folia/compact.svg)](https://papermc.io/software/folia)

I tried many of the auto restart plugins out there, but all I could find were either too complex or tick based. This means they were not accurate when TPS drops below 20.

So I decided to create my own.

[![bstats](https://bstats.org/signatures/bukkit/simpleautorestart.svg)](https://bstats.org/plugin/bukkit/SimpleAutoRestart/17760)

## Features

- Schedule automatic restarts at a specific times and days. (eg. `Wednesday;00:00`, `Sunday;3:00`)
- Customizable warning messages.
- Canceling/resuming restarts
- Realtime based and not tick based for more accurate.
- [MiniMessage](https://docs.advntr.dev/minimessage/) support
- Open Source!

## What it looks like in game (although you can customize this):

![Chat](https://i.imgur.com/ZjQq3sq.png)

## What it looks like in the server console (although you can customize this):

![Console](https://i.imgur.com/Kshy5U5.png)

## Installation

1. Set the startup script in [spigot.yml](https://docs.papermc.io/paper/reference/spigot-configuration/#settings_restart_script)
2. Download `SimpleAutoRestart-x.x.x-jar` from the releases page.
3. Place the JAR file in the plugins folder of your server.
4. Start or restart the server.
5. Configure the plugin by editing the config.yml file in the plugins/SimpleAutoRestart folder.

## Default Configuration

```yml
# The hour and minute when the server should restart (24 hour format)
# Format: Day;Hour;Minute
# Day can be: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday, or Daily
# Example: "Daily;03;00" for every day at 3 AM
restartTime:
  - "Daily;03:00"

# Add multiple times by adding a new line like: - '12:00'


#
# Messages
#

# Messages can either use legacy or MiniMessage (preferred) color codes.
# https://webui.advntr.dev/

# Configure message and the corresponding pre-warning time in seconds
messages:
  "0": "Restarting now"
  "1": "Restarting in 1 second"
  "2": "Restarting in 2 seconds"
  "3": "Restarting in 3 seconds"
  "60": "Restarting in 1 minute"
  "120": "Restarting in 2 minutes"
  "180": "Restarting in 3 minutes"
  "300": "Restarting in 5 minutes"

titles:
  "300": "<bold><dark_red>Warning"
  "3": "<bold><dark_red>Warning"

subtitles:
  "300": "<gold>Restarting in 5 minutes"
  "3": "<gold>Restarting now..."

# The commands that will be executed at restart time
commands:
  - "restart"
```

## Commands

- `/simpleautorestart` or `/sar` or `/autorestart` - Show the help menu.
- `/simpleautorestart cancel` - Cancel the next restart.
- `/simpleautorestart resume` - Resume the next restart.
- `/simpleautorestart status` - Show the status of the scheduled restarts.
- `/simpleautorestart set <hour> <minute> [day]` - Schedule a restart at a specific time.

## Troubleshooting

### It counts down, but doesn't restart

Make sure you have configured `restart-script` in [`spigot.yml`](https://docs.papermc.io/paper/reference/spigot-configuration/#settings_restart_script)

## Support

If you have any issues or questions. Feel free to [create an issue ticket](https://github.com/teunjojo/SimpleAutoRestart/issues/new). 
