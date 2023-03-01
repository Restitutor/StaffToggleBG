package me.restitutor.stafftogglebg

import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.types.InheritanceNode
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.time.Duration


class ToggleCommand(private val configFile: File) : Command("staff") {
    override fun execute(player: CommandSender, args: Array<String>) {
        val config = getConfig()
        if (player !is ProxiedPlayer) {
            player.sendMessage(config.getString("messages.notPlayer"))
            return
        }

        if (!player.hasPermission("stafftoggle.toggle")) {
            player.sendMessage(config.getString("messages.noPerm"))
            return
        }

        if (!args.isNotEmpty()) {
            player.sendMessage(config.getStringList("messages.help").joinToString("\n"))
            return
        }

        val group = config.getString("settings.adminGroup")

        if (args[0].equals("off", true) && player.hasPermission("luckperms.user.parent.removetemp")) {
            LuckPermsProvider.get().userManager.modifyUser(player.uniqueId) { user ->
                // 10L is given arbitrarily to mark the node as temporary
                user.data().remove(InheritanceNode.builder(group).expiry(10L).build())
            }
            player.sendMessage("Removed parent group $group")
            return
        }
        if (args[0].equals("on", true) && player.hasPermission("luckperms.user.parent.addtemp")) {
            var time: Int = config.getInt("settings.defaultMins")
            if (args.size >= 2) {
                try {
                    time = args.last().toInt()
                } catch (nfe: NumberFormatException) {
                    player.sendMessage("Invalid argument.")
                }
            }

            player.sendMessage("Add parent group $group for $time mins")
            LuckPermsProvider.get().userManager.modifyUser(player.uniqueId) { user ->
                user.data().add(InheritanceNode.builder(group).expiry(Duration.ofMinutes(time.toLong())).build())
            }
            return
        }
        // Player typed some unknown command
        player.sendMessage(config.getStringList("messages.help").joinToString("\n"))
    }

    fun getConfig(): Configuration {
        return ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(configFile)
    }
}
