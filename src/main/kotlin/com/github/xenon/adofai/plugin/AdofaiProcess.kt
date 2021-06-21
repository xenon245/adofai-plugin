package com.github.xenon.adofai.plugin

import com.github.monun.tap.util.isDamageable
import com.google.common.collect.ImmutableMap
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import java.util.*

class AdofaiProcess(private val plugin: AdofaiPlugin) {
    private var players: Map<UUID, AdofaiPlayer>

    var map = arrayListOf<AdofaiMap>()

    private var adofaiListener: AdofaiListener

    init {
        players = ImmutableMap.copyOf(
            Bukkit.getOnlinePlayers().asSequence().filter {
                it.gameMode.isDamageable
            }.associate { p ->
                p.uniqueId to AdofaiPlayer(p.uniqueId, p.name).apply {
                    player = p
                }
            }
        )
        plugin.server.apply {
            adofaiListener = AdofaiListener(this@AdofaiProcess).also {
                pluginManager.registerEvents(it, plugin)
            }
        }
    }

    fun player(uniqueId: UUID) = players[uniqueId]

    fun player(player: Player) = player(player.uniqueId)

    fun reload() {
        HandlerList.unregisterAll(plugin)
        players = ImmutableMap.copyOf(
            Bukkit.getOnlinePlayers().asSequence().filter {
                it.gameMode.isDamageable
            }.associate { p ->
                p.uniqueId to AdofaiPlayer(p.uniqueId, p.name).apply {
                    player = p
                }
            }
        )
        plugin.server.apply {
            adofaiListener = AdofaiListener(this@AdofaiProcess).also {
                pluginManager.registerEvents(it, plugin)
            }
        }
    }
}