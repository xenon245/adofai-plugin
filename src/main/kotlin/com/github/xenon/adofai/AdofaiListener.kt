package com.github.xenon.adofai

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class AdofaiListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        Adofai.registerPlayer(event.player)
    }
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.player.traceur.apply {
            player = null
        }
    }
}