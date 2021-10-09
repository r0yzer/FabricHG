package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.task.coroutineTask
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Vec3i

val diggerKit = kit("Digger") {
    val size = 6
    kitSelectorItem = Items.DRAGON_EGG.defaultStack
    cooldown = 7.0

    kitItem {
        itemStack = kitSelectorItem.copy().apply { count = 16 }

        onPlace { hgPlayer, kit, stack, blockPos, world ->
            hgPlayer.activateCooldown(kit)
            coroutineTask(delay = 750L) {
                repeat(size) { x ->
                    repeat(size) { y ->
                        repeat(size) { z ->
                            val x = x - 3
                            val z = z - 3
                            world.setBlockState(blockPos.subtract(Vec3i(x, y, z)), Blocks.AIR.defaultState)
                        }
                    }
                }
                world.playSound(null, blockPos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 10F, 1F)
                stack.apply { count -= 1 }
            }
        }
    }
}