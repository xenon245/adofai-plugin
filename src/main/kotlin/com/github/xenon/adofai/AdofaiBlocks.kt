package com.github.xenon.adofai

import com.google.common.collect.ImmutableList
import org.bukkit.Material
import org.bukkit.block.Block

object AdofaiBlocks {
    val START = StartBlock()

    val list = ImmutableList.of(
        START
    )

    fun getBlock(block: Block): AdofaiBlock? {
        val state = block.state
        val data = block.blockData
        val type = data.material

        if(type != Material.AIR) {
            if(type == Material.EMERALD_BLOCK) {
                return START
            } else {
                return null
            }
        } else {
            return null
        }
    }
}
abstract class AdofaiBlock {
    fun createBlockData(block: Block): AdofaiBlockData {
        return newBlockData(block).apply {
            this.block = block
            this.adofayBlock = this@AdofaiBlock
        }
    }
    protected abstract fun newBlockData(block: Block): AdofaiBlockData
}
abstract class AdofaiBlockData {
    lateinit var block: Block

    lateinit var adofayBlock: AdofaiBlock
        internal set
    open fun onInitialize(challenge: Challenge) {}

    open fun destroy() {}
}
class StartBlock: AdofaiBlock() {
    override fun newBlockData(block: Block): AdofaiBlockData {
        return StartData()
    }
    class StartData : AdofaiBlockData() {
        override fun onInitialize(challenge: Challenge) {

        }
    }
}