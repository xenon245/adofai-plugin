package com.github.xenon.adofai.task

import com.github.xenon.adofai.util.Tick
import java.lang.Long.max
import java.util.*

class AdofaiTask internal constructor(
    private val scheduler: AdofaiScheduler, val runnable: Runnable, delay: Long
) : Comparable<AdofaiTask> {
    companion object {
        internal const val ERROR = 0L
        internal const val NO_REPEATING = -1L
        internal const val CANCEL = -2L
        internal const val DONE = -3L
    }

    internal var nextRun: Long = Tick.currentTicks + max(0L, delay)

    internal var period: Long = 0L

    val isScheduled: Boolean
        get() = period.let { it != ERROR && it > CANCEL}

    val isCancelled
        get() = period == CANCEL
    val isDone
        get() = period == DONE
    internal fun execute() {
        runnable.runCatching { run() }
    }
    fun cancel() {
        if(!isScheduled) return

        period = CANCEL

        val remainTicks = nextRun - Tick.currentTicks

        if(remainTicks > 0xFF) scheduler.remove(this)
    }

    override fun compareTo(other: AdofaiTask): Int {
        return nextRun.compareTo(other.nextRun)
    }
}
class AdofaiScheduler : Runnable {
    private val queue = PriorityQueue<AdofaiTask>()

    fun runTask(runnable: Runnable, delay: Long): AdofaiTask {
        AdofaiTask(this, runnable, delay).apply {
            this.period = AdofaiTask.NO_REPEATING
            queue.offer(this)
            return this
        }
    }

    fun runTaskTimer(runnable: Runnable, delay: Long, period: Long): AdofaiTask {
        AdofaiTask(this, runnable, delay).apply {
            this.period = max(1L, period)
            queue.offer(this)
            return this
        }
    }

    override fun run() {
        val current = Tick.currentTicks

        while(queue.isNotEmpty()) {
            val task = queue.peek()

            if(task.nextRun > current) {
                break
            }
            queue.remove()

            if(task.isScheduled) {
                task.run {
                    execute()
                    if(period > 0) {
                        nextRun = current + period
                        queue.offer(task)
                    } else {
                        period = AdofaiTask.DONE
                    }
                }
            }
        }
    }
    internal fun cancelAll() {
        val queue = this.queue
        queue.forEach { it.period = AdofaiTask.CANCEL }
        queue.clear()
    }
    fun remove(task: AdofaiTask) {
        queue.remove(task)
    }
}