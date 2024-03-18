package de.royzer.fabrichg.feast

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.scoreboard.formattedTime
import de.royzer.fabrichg.sendPlayerStatus
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.getRandomHighestPos
import kotlinx.coroutines.Job
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.math.geometry.produceFilledCirclePositions
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

// TODO
object Feast {
    var started = false
    var timeLeft = 300

    var feastJob: Job? = null
    var feastCenter: BlockPos = BlockPos.ZERO
    var feastTimestamp: Instant? = null

    private var radius = 25

    fun start() {
        server.playerList.players.forEach { it.sendPlayerStatus() }
        started = true
        feastCenter = getRandomHighestPos(150)
        feastTimestamp = Instant.now().plusSeconds(timeLeft.toLong())

        PlayerList.players.forEach { e ->
            e.value.kits.forEach {
                if (it.name == "Anchor") {
                    e.value.serverPlayer?.inventory?.armor?.set(2, Items.AIR.defaultInstance)
                    e.value.serverPlayer?.inventory?.armor?.set(3, Items.AIR.defaultInstance)

                }
            }
        }

        repeat(10) { i ->
            feastCenter.produceFilledCirclePositions(radius) {
                server.overworld().setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.AIR.defaultBlockState())
            }
        }

        feastCenter.produceFilledCirclePositions(radius) {
            server.overworld().setBlockAndUpdate(it, Blocks.GRASS_BLOCK.defaultBlockState())
        }

        feastJob =
            mcCoroutineTask(howOften = timeLeft.toLong() + 1, period = 1000.milliseconds, delay = 0.milliseconds) {
                when (timeLeft) {
                    300, 180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcastComponent(literalText {
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
                        spawn()
                    }
                }
                timeLeft -= 1
            }
    }

    fun spawn() {

        server.playerList.players.forEach {
            it.playSound(SoundEvents.RAID_HORN.value(), 1.0f, 1.0f)
        }

        server.overworld().setBlock(
            feastCenter.subtract(Vec3i(0, -1, 0)),
            Blocks.ENCHANTING_TABLE.defaultBlockState(),
            3
        )
        val chestLocations: Array<BlockPos?> = arrayOfNulls(12)

        chestLocations[0] = feastCenter.subtract(Vec3i(1, -1, 1))
        chestLocations[1] = feastCenter.subtract(Vec3i(-1, -1, 1))
        chestLocations[2] = feastCenter.subtract(Vec3i(-1, -1, -1))
        chestLocations[3] = feastCenter.subtract(Vec3i(1, -1, -1))
        chestLocations[4] = feastCenter.subtract(Vec3i(2, -1, 2))
        chestLocations[5] = feastCenter.subtract(Vec3i(0, -1, 2))
        chestLocations[6] = feastCenter.subtract(Vec3i(-2, -1, 2))
        chestLocations[7] = feastCenter.subtract(Vec3i(2, -1, 0))
        chestLocations[8] = feastCenter.subtract(Vec3i(-2, -1, 0))
        chestLocations[9] = feastCenter.subtract(Vec3i(2, -1, -2))
        chestLocations[10] = feastCenter.subtract(Vec3i(0, -1, -2))
        chestLocations[11] = feastCenter.subtract(Vec3i(-2, -1, -2))

        Arrays.stream(chestLocations).forEach { chestLocation ->
            server.overworld().setBlockAndUpdate(chestLocation!!, Blocks.CHEST.defaultBlockState())
        }

    }
}