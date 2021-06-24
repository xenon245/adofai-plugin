package com.github.xenon.adofai

import com.github.xenon.adofai.task.AdofaiScheduler
import com.github.xenon.adofai.task.AdofaiTask
import com.google.common.collect.ImmutableMap
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.regions.Region
import org.bukkit.block.Block

class Challenge(val level: Level) {
    lateinit var dataMap: Map<AdofaiBlock, Set<AdofaiBlockData>>

    lateinit var dataByBlock: Map<Block, AdofaiBlockData>

    private var _traceurs = HashSet<Traceur>()

    val traceurs: Set<Traceur>
        get() = _traceurs

    private val scheduler = AdofaiScheduler()

    private var valid = true

    internal fun parseBlocks() {
        checkState()

        val dataMap = HashMap<AdofaiBlock, HashSet<AdofaiBlockData>>()
        val dataByBlock = HashMap<Block, AdofaiBlockData>()

        level.region.forEachBlocks { block ->
            AdofaiBlocks.getBlock(block)?.let { adofaiBlock ->
                val data = adofaiBlock.createBlockData(block).apply {
                    onInitialize(this@Challenge)
                }
                dataMap.computeIfAbsent(adofaiBlock) { HashSet() } += data
                dataByBlock[block] = data
            }
        }
        this.dataMap = ImmutableMap.copyOf(dataMap)
        this.dataByBlock = ImmutableMap.copyOf(dataByBlock)
    }
    fun addTraceur(traceur: Traceur) {
        checkState()
        traceur.challenge?.let {
            if(this == this) return

            it.removeTraceur(traceur)
        }
        if(_traceurs.add(traceur)) {
            traceur.challenge = this
        }
    }
    fun removeTraceur(traceur: Traceur) {
        checkState()

        if(_traceurs.remove(traceur)) {
            traceur.challenge = null
        }
    }

    internal fun runTaskTimer(runnable: Runnable, delay: Long, period: Long): AdofaiTask {
        checkState()
        return scheduler.runTaskTimer(runnable, delay, period)
    }
    internal fun update() {
        scheduler.run()
    }
    internal fun destroy() {
        checkState()
        scheduler.cancelAll()

        valid = false

        _traceurs.apply {
            forEach { it.challenge = null }
            clear()
        }
        dataByBlock.values.forEach {
            it.destroy()
        }
    }
    private fun checkState() {
        check(this.valid) { "Invalid $this" }
    }
    private fun Region.forEachBlocks(action: (Block) -> Unit) {
        val world = BukkitAdapter.asBukkitWorld(world).world

        forEach {
            action.invoke(world.getBlockAt(it.x, it.y, it.z))
        }
    }
}