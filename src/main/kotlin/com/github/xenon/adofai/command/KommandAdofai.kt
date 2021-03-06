package com.github.xenon.adofai.command

import com.github.monun.kommand.KommandBuilder
import com.github.monun.kommand.KommandContext
import com.github.monun.kommand.argument.KommandArgument
import com.github.monun.kommand.argument.playerTarget
import com.github.monun.kommand.argument.string
import com.github.monun.kommand.sendFeedback
import com.github.xenon.adofai.Adofai
import com.github.xenon.adofai.Level
import com.github.xenon.adofai.traceur
import com.github.xenon.adofai.util.selection
import com.sk89q.worldedit.regions.CuboidRegion
import net.kyori.adventure.text.Component.text
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object KommandAdofai {
    fun register(builder: KommandBuilder) {
        builder.apply {
            then("create") {
                then("name" to string()) {
                    require { this is Player }
                    executes {
                        create(it.sender as Player, it.parseArgument("name"))
                    }
                }
            }
            then("remove") {
                then("level" to LevelArgument) {
                    executes {
                        remove(it.sender, it.parseArgument("level"))
                    }
                }
            }
            then("start") {
                then("level" to LevelArgument) {
                    then("player" to playerTarget()) {
                        executes {
                            start(it.sender, it.parseArgument("level"), it.parseArgument("player"))
                        }
                    }
                    require {
                        this is Player
                    }
                    executes {
                        start(it.sender, it.parseArgument("level"), it.sender as Player)
                    }
                }
            }
            then("quit") {
                require { this is Player }
                executes {
                    quit(it.sender, this as Player)
                }
                then("player" to playerTarget()) {
                    executes {
                        quit(it.sender, it.parseArgument("player"))
                    }
                }
            }
            then("stop") {
                then("level" to LevelArgument) {
                    executes {
                        stop(it.sender, it.parseArgument("level"))
                    }
                }
            }
        }
    }
    private fun create(sender: Player, name: String) {
        sender.selection?.let { region ->
            if(region !is CuboidRegion) {
                sender.sendFeedback { text("????????? ????????? ???????????? ?????? ???????????????. $region") }
            } else {
                Adofai.runCatching {
                    createLevel(name, region)
                }.onSuccess {
                    sender.sendFeedback { text("${it.name} ????????? ????????? ??????????????????.") }
                }.onFailure {
                    sender.sendFeedback { text("$name ????????? ?????? ????????? ??????????????????. ${it.message}") }
                }
            }
        } ?: sender.sendFeedback { text("????????? ????????? ????????? ????????? WorldEdit??? Wand??? ??????????????????.") }
    }
    private fun remove(sender: CommandSender, level: Level) {
        level.remove()
        sender.sendFeedback { text("${level.name} ????????? ????????? ??????????????????.") }
    }
    private fun start(sender: CommandSender, level: Level, player: Player) {
        val challenge = level.startChallenge()
        player.run {
            gameMode = GameMode.SPECTATOR
        }
    }
    private fun quit(sender: CommandSender, player: Player) {
        player.traceur.apply {
            challenge?.let {
                it.removeTraceur(this)
                sender.sendFeedback { text("${player.name}(???)??? ${it.level.name} ?????? ????????? ??????????????????.") }
            } ?: sender.sendFeedback { text("${player.name}(???)??? ?????? ?????? ????????? ????????????.") }
        }
    }
    private fun stop(sender: CommandSender, level: Level) {
        if(level.challenge == null) {
            sender.sendFeedback { text("?????? ???????????? ????????????.") }
        } else {
            level.stopChallenge()
            sender.sendFeedback { text("${level.name} ????????? ??????????????????.") }
        }
    }
}

object LevelArgument : KommandArgument<Level> {
    override fun parse(context: KommandContext, param: String): Level? {
        return Adofai.levels[param]
    }

    override fun suggest(context: KommandContext, target: String): Collection<String> {
        return Adofai.levels.keys.filter { it.startsWith(target, true) }
    }
}