package com.github.xenon.adofai.plugin

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class AdofaiListener(val process: AdofaiProcess) : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if(event.player.itemInHand.type == Material.BLAZE_ROD) {
            if(event.action == Action.RIGHT_CLICK_BLOCK) {
                event.isCancelled = true
                process.player(event.player)?.addBlock(event.clickedBlock!!)
                val loc = event.clickedBlock?.location!!
                event.player.sendMessage("${ChatColor.LIGHT_PURPLE}블럭을 추가하였습니다. (${loc.x}, ${loc.y}, ${loc.z})")
            } else if(event.action == Action.LEFT_CLICK_BLOCK) {
                event.isCancelled = true
                process.player(event.player)?.removeBlock()
                val loc = event.clickedBlock?.location!!
                event.player.sendMessage("${ChatColor.LIGHT_PURPLE}블럭을 제거하였습니다. (${loc.x}, ${loc.y}, ${loc.z})")
            }
        }
    }
}