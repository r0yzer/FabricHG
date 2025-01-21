package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.kit.kit
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.silkmc.silk.core.Silk
import net.silkmc.silk.core.item.itemStack
import de.royzer.fabrichg.kit.kit
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.world.level.Level
import net.silkmc.silk.core.entity.world
import org.locationtech.jts.shape.fractal.HilbertCode.level


val minerKit = kit("Miner") {
    kitSelectorItem = ItemStack(Items.IRON_PICKAXE)
    description = "You start with an iron pickaxe and u can mine all ores in a cluster instantly!"

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

    kitItem {
        itemStack = kitSelectorItem
        onDestroyBlock { hgPlayer, kit, blockPos ->
            val world = hgPlayer.serverPlayer?.world ?: return@onDestroyBlock
            destroyOres(world, blockPos, 0, hgPlayer.serverPlayer!!)
            hgPlayer.checkUsesForCooldown(kit, maxUses!!)
        }
    }
}

// Zerstöre alle verbundenen Blöcke
fun destroyOres(world: Level, blockPos: BlockPos, count: Int, player: ServerPlayer) {
    if (count > 64 * 3) return // Limit für Anzahl der zerstörten Blöcke
    val blockState = world.getBlockState(blockPos)


    if (!oreBlocks.contains(blockState.block)) return


    world.destroyBlock(blockPos, true)

    // Blöcke drumherum abchecken
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