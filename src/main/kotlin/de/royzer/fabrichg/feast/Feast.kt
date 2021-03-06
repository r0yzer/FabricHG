package de.royzer.fabrichg.feast

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.scoreboard.formattedTime
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.getRandomHighestPos
import kotlinx.coroutines.Job
import net.axay.fabrik.core.math.geometry.produceFilledCirclePositions
import net.axay.fabrik.core.task.coroutineTask
import net.axay.fabrik.core.text.literalText
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos

// TODO
object Feast {
    var started = false
    var timeLeft = 300

    var feastJob: Job? = null
    var feastCenter: BlockPos = BlockPos.ORIGIN

    private var radius = 25

    fun start() {
        started = true
        feastCenter = getRandomHighestPos(150)

        repeat(10) { i ->
            feastCenter.produceFilledCirclePositions(radius) {
                server.overworld.setBlockState(BlockPos(it.x, it.y + i, it.z), Blocks.AIR.defaultState)
            }
        }

        feastCenter.produceFilledCirclePositions(radius) {
            server.overworld.setBlockState(it, Blocks.GRASS_BLOCK.defaultState)
        }

        feastJob = coroutineTask(howOften = timeLeft.toLong() + 1, period = 1000) {
            when (timeLeft) {
                300, 180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcast(literalText {
                    text("Das Feast startet in ")
                    text(timeLeft.formattedTime) { color = TEXT_BLUE }
                    text(" Minuten bei ")
                    text("${feastCenter.x} ${feastCenter.y} ${feastCenter.z}") {
                        color = TEXT_BLUE
                        bold = true
                    }
                    color = TEXT_GRAY
                })
                0 -> {
                    server.overworld.setBlockState(feastCenter.add(0, 1, 0), Blocks.ENCHANTING_TABLE.defaultState)
                }
            }
            timeLeft -= 1
        }

    }
}