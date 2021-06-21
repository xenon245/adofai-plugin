package com.github.xenon.adofai.plugin

import com.github.monun.kommand.kommand
import com.google.common.collect.ImmutableMap
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.plugin.java.JavaPlugin

class AdofaiPlugin : JavaPlugin() {
    private var process: AdofaiProcess? = null
    override fun onEnable() {
        process = AdofaiProcess(this)
        kommand {
            register("adofai") {
                AdofaiKommand.register(this, process!!)
            }
        }
    }

    override fun onDisable() {
        process = null
    }
}