package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.silkmc.silk.core.entity.pos


val tankKit = kit("Tank") {
    kitSelectorItem = Items.GRAVEL.defaultInstance

    val recraftExplosionSize by property(1.5f, "recraft explosion size")
    val entityExplosionSize by property(3.5f, "entity explosion size")
    val playerExplosionSize by property(5f, "player explosion size")

    kitEvents {
        onKillEntity(true) { hgPlayer, kit, killed ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onKillEntity

            val explosionSize = if (killed.hgPlayer == null) entityExplosionSize else playerExplosionSize

            if (killed.hgPlayer?.isNeo == true) {
                blockKitsAchievement.awardLater((killed as? ServerPlayer) ?: return@onKillEntity)
                return@onKillEntity
            }

            // bin rr nicht fähig das zu testen geht aber glaube
            killed.level().explode(serverPlayer, killed.damageSources().playerAttack(serverPlayer), null, killed.pos, explosionSize, false, Level.ExplosionInteraction.TNT)
        }

        onCraft { hgPlayer, itemStack, recipe, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onCraft
            if (itemStack.item != Items.MUSHROOM_STEW) return@onCraft

            serverPlayer.level().explode(serverPlayer, serverPlayer.x, serverPlayer.y, serverPlayer.z, recraftExplosionSize, false, Level.ExplosionInteraction.TNT)

        }
    }
}