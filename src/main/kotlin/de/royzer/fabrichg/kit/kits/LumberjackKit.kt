package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.core.BlockPos
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.silkmc.silk.core.entity.world


val lumberjackKit = kit("Lumberjack") {
    kitSelectorItem = Items.WOODEN_AXE.defaultInstance.also {
        it.enchant(Enchantments.UNBREAKING, 3)
    }
    description = "Chop down trees"

    cooldown = 8.0

    kitItem {
        itemStack = kitSelectorItem
        onDestroyBlock { hgPlayer, kit, blockPos ->
            val world = hgPlayer.serverPlayer?.world ?: return@onDestroyBlock
            destroyLogs(world, blockPos, 0)
            hgPlayer.checkUsesForCooldown(kit, 10)
        }
    }
}

// rekursionsbuster
fun destroyLogs(world: Level, blockPos: BlockPos, count: Int) {
    world.destroyBlock(blockPos, true)
    if (count > 64 * 3) return
    val neighbors = listOf<BlockPos>(
        blockPos.north(),
        blockPos.west(),
        blockPos.east(),
        blockPos.south(),
        blockPos.above(),
        blockPos.below(),
    )
    neighbors.filter {
        world.getBlockState(it).block.name.string.contains("log", true)
    }.forEach {
        destroyLogs(world, it, count + 1)
    }

}