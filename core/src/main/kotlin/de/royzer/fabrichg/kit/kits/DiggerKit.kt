package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.kitProperty
import de.royzer.fabrichg.kit.property.property
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.task.mcCoroutineTask
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

data class DiggerHole(
    val owner: UUID,
    val size: Int,
    val pos: BlockPos
) {
    val aabb = AABB(
        pos.x - (size / 2).toDouble(),
        pos.y - size.toDouble(),
        pos.z - (size / 2).toDouble(),
        pos.x + (size / 2).toDouble(),
        pos.y.toDouble(),
        pos.z + (size / 2).toDouble()
    )
}

val diggerHoles = mutableListOf<DiggerHole>()

val diggerKit = kit("Digger") {
    val size by property(6, "size")

    kitSelectorItem = Items.DRAGON_EGG.defaultInstance
    cooldown = 7.0
    description = "Make a 5 by 5 hole"

    val blocksReplacedAchievement by achievement("blocks replaced") {
        level(500)
        level(4000)
        level(1000)
    }
    val playersKilledInDiggerAchievement by achievement("players killed in digger") {
        level(15)
        level(100)
        level(500)
    }

    kitItem {
        itemStack = kitSelectorItem.copy().apply { count = 16 }

        onPlace { hgPlayer, kit, stack, blockPos, world ->
            hgPlayer.activateCooldown(kit)
            val player = hgPlayer.serverPlayer ?: return@onPlace

            mcCoroutineTask(period = 0.milliseconds, delay = 750L.milliseconds) {
                diggerHoles.add(DiggerHole(player.uuid, size, blockPos))
                repeat(size) { _x ->
                    repeat(size) { _y ->
                        repeat(size) { _z ->
                            val x = _x - size / 2
                            val z = _z - size / 2
                            val pos = blockPos.subtract(Vec3i(x, _y, z))
                            if (world.getBlockState(pos).block != Blocks.BEDROCK && world.getBlockState(pos).block != Blocks.AIR) {
                                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                                blocksReplacedAchievement.award(player, 1)
                            }
                        }
                    }
                }
                world.playSound(null, blockPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 10F, 1F)
                stack.apply { count -= 1 }
            }
        }
    }

    kitEvents {
        onKillPlayer { hgPlayer, kit, killedPlayer ->
            val player = hgPlayer.serverPlayer ?: return@onKillPlayer

            diggerHoles.forEach { hole ->
                if (hole.owner != hgPlayer.uuid) return@forEach

                if (hole.aabb.contains(killedPlayer.pos)) {
                    playersKilledInDiggerAchievement.awardLater(player)
                }
            }
        }
    }
}