package me.restitutor.stafftogglebg

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import java.io.FileOutputStream

class StaffToggleBG : Plugin() {
    override fun onEnable() {
        ProxyServer.getInstance().pluginManager.registerCommand(this, ToggleCommand(initConfig()))
    }

    private fun initConfig() : File {
        if (!dataFolder.exists()) {
            logger.info("Created config folder: ${dataFolder.mkdir()}")
        }

        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            val outputStream = FileOutputStream(configFile)
            getResourceAsStream("config.yml").transferTo(outputStream)
        }
        return configFile
    }
}