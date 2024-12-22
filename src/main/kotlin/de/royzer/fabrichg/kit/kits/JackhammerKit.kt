package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.entity.world

val jackhammerKit = kit("Jackhammer") {
    kitSelectorItem = Items.STONE_AXE.defaultInstance

    description = "Create deep holes"

    cooldown = 16.0

    val destroyBlocksAchievement by achievement("destroy blocks") {
        level(1000)
        level(10000)
        level(35000)
    }

    kitItem {
        itemStack = kitSelectorItem
        onDestroyBlock { hgPlayer, kit, blockPos ->
            val player = hgPlayer.serverPlayer ?: return@onDestroyBlock

            repeat(384) {
                val world = hgPlayer.serverPlayer?.world ?: return@repeat
                val unten = BlockPos(blockPos.x, -63, blockPos.z)
                val pos = unten.subtract(Vec3i(0, -it, 0))
                if (world.getBlockState(pos).block != Blocks.BEDROCK && world.getBlockState(pos).block != Blocks.AIR) {
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                    destroyBlocksAchievement.awardLater(player)
                }
            }

            hgPlayer.checkUsesForCooldown(kit, 6)
        }
    }
}