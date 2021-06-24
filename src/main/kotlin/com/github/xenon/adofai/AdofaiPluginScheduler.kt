package com.github.xenon.adofai

class AdofaiPluginScheduler : Runnable {
    override fun run() {
        Adofai.fakeEntityServer.update()

        for(level in Adofai.levels.values) {
            level.challenge?.update()
        }
    }
}