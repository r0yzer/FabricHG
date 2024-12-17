package de.royzer.fabrichg.game

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.server
import de.royzer.fabrichg.settings.ConfigManager
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.Silk
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.math.geometry.produceCirclePositions
import net.silkmc.silk.core.math.geometry.produceFilledCirclePositions
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText
import kotlin.random.Random

object Pit {

    var started = false

    val pitCenter = BlockPos(0, 0, 0)

    fun start() {
        if (started) return
        if (!ConfigManager.gameSettings.pitEnabled) {
            return
        }
        started = true
        broadcastComponent(literalText {
            text("Das Pit startet ")
            text("jetzt") { color = TEXT_BLUE }
            color = TEXT_GRAY
        })
        server.overworld().allEntities.forEach { entity ->
            if (entity == null) return@forEach
            if (entity !is ServerPlayer && entity !is Wolf && entity !is HGBot) {
                entity.kill()
            }
        }
        mcCoroutineTask(sync = true) {
            pitCenter.subtract(Vec3i(0, 4, 0)).produceFilledCirclePositions(55) {
                Silk.server?.overworld()
                    ?.setBlockAndUpdate(BlockPos(it.x, it.y, it.z), Blocks.BEDROCK.defaultBlockState())
            }
            pitCenter.subtract(Vec3i(0, 3, 0)).produceFilledCirclePositions(55) {
                repeat(4) { i ->
                    Silk.server?.overworld()
                        ?.setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.STONE.defaultBlockState())
                }

            }

//            pitCenter.subtract(Vec3i(0, 3, 0)).produceFilledCirclePositions(50) {
//                repeat(153) { i ->
//                    Silk.server?.overworld()
//                        ?.setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.BEDROCK.defaultBlockState())
//                }
//            }
            pitCenter.produceFilledCirclePositions(50) {
                repeat(150) { i ->
                    Silk.server?.overworld()
                        ?.setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.AIR.defaultBlockState())
                }
            }
            pitCenter.produceCirclePositions(50) {
                repeat(150) { i ->
                    Silk.server?.overworld()
                        ?.setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.STONE.defaultBlockState())
                }
            }
            PlayerList.alivePlayers.forEach { player ->
                val overworld = Silk.server?.overworld() ?: return@forEach
                player.serverPlayer?.teleportTo(overworld, 0.0, 1.0, 0.0, Random.nextFloat() * 180, 0.0f)
            }
        }
    }
}