package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.kitProperty
import de.royzer.fabrichg.kit.property.property
import net.minecraft.core.Vec3i
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.milliseconds

val diggerKit = kit("Digger") {
    val size by property(6, "size")

    kitSelectorItem = Items.DRAGON_EGG.defaultInstance
    cooldown = 7.0
    description = "Make a 5 by 5 hole"

    kitItem {
        itemStack = kitSelectorItem.copy().apply { count = 16 }

        onPlace { hgPlayer, kit, stack, blockPos, world ->
            hgPlayer.activateCooldown(kit)
            mcCoroutineTask(period = 0.milliseconds, delay = 750L.milliseconds) {
                repeat(size) { _x ->
                    repeat(size) { _y ->
                        repeat(size) { _z ->
                            val x = _x - size / 2
                            val z = _z - size / 2
                            val pos = blockPos.subtract(Vec3i(x, _y, z))
                            if (world.getBlockState(pos).block != Blocks.BEDROCK)
                                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                        }
                    }
                }
                world.playSound(null, blockPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 10F, 1F)
                stack.apply { count -= 1 }
            }
        }
    }
}