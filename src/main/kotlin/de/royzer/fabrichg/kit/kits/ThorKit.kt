package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.util.toHighestPos
import de.royzer.fabrichg.util.toVec3
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LightningBolt
import net.minecraft.world.item.Items

val thorKit = kit("Thor") {
    kitSelectorItem = Items.WOODEN_AXE.defaultInstance

    cooldown = 12.0

    kitItem {
        itemStack = kitSelectorItem
        onUseOnBlock { hgPlayer, kit, blockPlaceContext ->
            val level = blockPlaceContext.level
            val lightning = LightningBolt(EntityType.LIGHTNING_BOLT, level)
            lightning.setPos(blockPlaceContext.clickedPos.toHighestPos().toVec3())
            level.addFreshEntity(lightning)

            hgPlayer.activateCooldown(kit)
        }
    }
}