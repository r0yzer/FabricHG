package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.checkUsesForCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.silkmc.silk.core.Silk
import net.silkmc.silk.core.entity.world


val lumberjackKit = kit("Lumberjack") {
    kitSelectorItem = Items.WOODEN_AXE.defaultInstance.also {
//        val unbreaking: Holder<Enchantment> = Holder.direct(
//            Silk.server!!.overworld().registryAccess().registry(Registries.ENCHANTMENT).get().get(Enchantments.UNBREAKING)!!
//        )
//        it.enchant(unbreaking, 3)
    }
    description = "Chop down trees"

    cooldown = 8.0

    val maxUses by property(10, "max uses")

    kitItem {
        itemStack = kitSelectorItem
        onDestroyBlock { hgPlayer, kit, blockPos ->
            val world = hgPlayer.serverPlayer?.world ?: return@onDestroyBlock
            destroyLogs(world, blockPos, 0)
            hgPlayer.checkUsesForCooldown(kit, maxUses)
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