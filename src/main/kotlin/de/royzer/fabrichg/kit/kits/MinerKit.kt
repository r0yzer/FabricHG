package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.Silk
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack

val minerPickaxe
    get() = itemStack(Items.STONE_PICKAXE) {
        val binding: Holder<Enchantment>? =
            Silk.server?.registryAccess()?.registry(Registries.ENCHANTMENT)?.get()?.get(Enchantments.EFFICIENCY)?.let {
                Holder.direct(
                    it
                )
            }
        if (binding != null) {
            enchant(binding, 3)
        }
    }

val minerKit = kit("Miner") {
    kitSelectorItem = minerPickaxe
    description = "Mine ores faster"

    kitItem {
        itemStack = kitSelectorItem.copy()
        onDestroyBlock { hgPlayer, kit, blockPos ->
            val world = hgPlayer.serverPlayer?.world ?: return@onDestroyBlock
            destroyOres(world, blockPos, 0, hgPlayer.serverPlayer!!)
        }
    }
}

fun destroyOres(world: Level, blockPos: BlockPos, count: Int, player: ServerPlayer) {
    if (count > 64 * 3) return
    val blockState = world.getBlockState(blockPos)

    if (!oreBlocks.contains(blockState.block)) return

    world.destroyBlock(blockPos, true)

    val neighbors = listOf(
        blockPos.north(),
        blockPos.south(),
        blockPos.east(),
        blockPos.west(),
        blockPos.above(),
        blockPos.below()
    )
    neighbors.forEach { neighbor ->
        destroyOres(world, neighbor, count + 1, player)
    }
}

val oreBlocks = listOf(
    Blocks.IRON_ORE,
    Blocks.DEEPSLATE_IRON_ORE,
    Blocks.COPPER_ORE,
    Blocks.DEEPSLATE_COPPER_ORE,
    Blocks.DIAMOND_ORE,
    Blocks.DEEPSLATE_DIAMOND_ORE,
    Blocks.LAPIS_ORE,
    Blocks.DEEPSLATE_LAPIS_ORE,
    Blocks.EMERALD_ORE,
    Blocks.DEEPSLATE_EMERALD_ORE,
    Blocks.REDSTONE_ORE,
    Blocks.DEEPSLATE_REDSTONE_ORE,
    Blocks.COAL_ORE,
    Blocks.DEEPSLATE_COAL_ORE
)