package com.github.xenon.adofai.plugin

import com.github.monun.kommand.KommandBuilder
import com.github.monun.kommand.KommandContext
import com.github.monun.kommand.argument.KommandArgument
import com.github.monun.kommand.argument.string
import com.github.monun.kommand.sendFeedback
import net.kyori.adventure.text.Component.text
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object AdofaiKommand {
    fun register(builder: KommandBuilder, process: AdofaiProcess) {
        builder.apply {
            then("start") {

            }
            then("create") {
                then("name" to string()) {
                    executes {
                        val maps = process.map[it.parseArgument("name")]
                        if(maps == null) {
                            val map = AdofaiMap(it.parseArgument("name")).apply {
                                val player = process.player(it.sender as Player)
                                blocks = player?.blocks!!
                            }
                            process.map.add(map)
                            it.sender.sendFeedback {
                                text("${ChatColor.LIGHT_PURPLE}맵을 만들었습니다. ${map.name}")
                            }
                        } else {
                            it.sender.sendFeedback {
                                text("${ChatColor.LIGHT_PURPLE}맵이 이미 존재합니다.")
                            }
                        }
                    }
                }
            }
            then("remove") {
                then("map" to MapArgument(process)) {
                    executes {
                        val map = it.parseArgument<AdofaiMap>("map")
                        process.map.remove(map)
                    }
                }
            }
            then("stop") {

            }
            then("reload") {
                executes {
                    process.reload()
                }
            }
        }
    }
}

class MapArgument(val process: AdofaiProcess) : KommandArgument<AdofaiMap> {
    override fun parse(context: KommandContext, param: String): AdofaiMap? {
        return process.map.filter { it.name == param }.first()
    }

    override fun suggest(context: KommandContext, target: String): Collection<String> {
        val list = arrayListOf<String>()
        process.map.forEach { list += it.name }
        return list
    }
}