package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.util.toHighestPos
import de.royzer.fabrichg.util.toVec3
import net.minecraft.core.Vec3i
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LightningBolt
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks

val thorKit = kit("Thor") {
    kitSelectorItem = Items.WOODEN_AXE.defaultInstance

    cooldown = 12.0

    description = "Summon lightning bolts"

    val heightForNetherrack by property(100, "height for netherrack")
    val netherrackExplosionRadius by property(5.0, "netherrack explosion radius")

    kitItem {
        itemStack = kitSelectorItem
        onUseOnBlock { hgPlayer, kit, blockPlaceContext ->
            val level = blockPlaceContext.level
            val lightningPos = blockPlaceContext.clickedPos.toHighestPos()
            if (lightningPos.y > heightForNetherrack) {
                if (level.getBlockState(lightningPos.subtract(Vec3i(0, 1,0))).block == Blocks.NETHERRACK) {
                    // soll das auch unter y 100 explodieren?
                    level.explode(
                        hgPlayer.serverPlayer,
                        lightningPos.x.toDouble(),
                        lightningPos.y.toDouble(),
                        lightningPos.z.toDouble(),
                        netherrackExplosionRadius.toFloat(),
                        true,
                        Level.ExplosionInteraction.TNT
                    )
                } else {
                    level.setBlockAndUpdate(lightningPos, Blocks.NETHERRACK.defaultBlockState())
                }
            }
            val lightning = LightningBolt(EntityType.LIGHTNING_BOLT, level)
            lightning.setPos(lightningPos.toVec3())
            level.addFreshEntity(lightning)

            hgPlayer.activateCooldown(kit)
        }
    }
}