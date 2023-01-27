# AutoRestart
I created this plugin as a solution to my own problem where I could not find a reliable and simple plugin to restart my Spigot server. After trying various options and finding them to be either too complex or tick based so they where not accurate when TPS was below 20 and therefore not compatible with Hibernate plugins.
So I decided to build my own.

## Features
- Schedule automatic restarts at a specific time.
- Warning messages broadcasted to players before restart.
- Time based and not tick based for more accurate.
- Compatible with plugins like [Hibernate](https://www.spigotmc.org/resources/hibernate.4441/) and [Server Naptime](https://github.com/gvk/MinecraftPluginServerHibernate)

## Installation
1. Download `AutoRestart-x.x.x-jar` from the releases page.
2. Place the JAR file in the plugins folder of your server.
3. Start or restart the server.
4. Configure the plugin by editing the config.yml file in the plugins/AutoRestart folder.

## Support
If you have any issues or questions. Feel free to create either open issue ticket or mail me on mail@teunjojo.com.