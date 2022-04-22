package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.task.coroutineTask
import net.minecraft.core.Vec3i
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks

val diggerKit = kit("Digger") {
    val size = 6
    kitSelectorItem = Items.DRAGON_EGG.defaultInstance
    cooldown = 7.0

    kitItem {
        itemStack = kitSelectorItem.copy().apply { count = 16 }

        onPlace { hgPlayer, kit, stack, blockPos, world ->
            hgPlayer.activateCooldown(kit)
            coroutineTask(delay = 750L) {
                repeat(size) { x ->
                    repeat(size) { y ->
                        repeat(size) { z ->
                            val x = x - size / 2
                            val z = z - size / 2
                            val pos = blockPos.subtract(Vec3i(x, y, z))
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