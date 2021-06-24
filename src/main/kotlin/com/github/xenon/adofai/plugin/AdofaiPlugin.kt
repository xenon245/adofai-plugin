package com.github.xenon.adofai.plugin

import com.github.monun.kommand.kommand
import com.github.xenon.adofai.Adofai
import com.github.xenon.adofai.AdofaiListener
import com.github.xenon.adofai.AdofaiPluginScheduler
import com.github.xenon.adofai.command.KommandAdofai
import org.bukkit.plugin.java.JavaPlugin

class AdofaiPlugin : JavaPlugin() {
    override fun onEnable() {
        Adofai.initialize(this)
        server.apply {
            pluginManager.registerEvents(AdofaiListener(), this@AdofaiPlugin)
            scheduler.runTaskTimer(this@AdofaiPlugin, AdofaiPluginScheduler(), 0L, 1L)
        }
        kommand {
            register("adofai") {
                KommandAdofai.register(this)
            }
        }
    }

    override fun onDisable() {
        Adofai.fakeEntityServer.clear()
        Adofai.levels.values.forEach {
            it.save()
            it.challenge?.run {
                it.stopChallenge()
            }
        }
    }
}