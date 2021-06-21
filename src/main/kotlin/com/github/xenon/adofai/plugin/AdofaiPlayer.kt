package com.github.xenon.adofai.plugin

import com.github.monun.tap.ref.weaky
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*

class AdofaiPlayer(val uniqueId: UUID, name: String) {
    var player: Player? by weaky(null) { Bukkit.getPlayer(uniqueId) }

    var count: Int = 1

    var blocks = HashMap<Int, Block>()

    fun addBlock(block: Block) {
        blocks[count] = block
        count++
    }
    fun removeBlock() {
        count--
        blocks.remove(count)
    }
}