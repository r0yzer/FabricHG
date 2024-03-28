package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.ThrownTrident
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.math.vector.times
import net.silkmc.silk.core.task.mcCoroutineScope
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

const val TRIDENT_KEY = "tridents"

val poseidonKit = kit("Poseidon") {
    kitSelectorItem = Items.TRIDENT.defaultInstance.apply {
        enchant(Enchantments.RIPTIDE, 1)
    }

    description = "Summon and throw tridents"

    cooldown = 32.0

    val tridentAmount by property(4, "trident amount")


    kitItem {
        itemStack = kitSelectorItem

        onClick { hgPlayer, kit ->
            val tridents = hgPlayer.getPlayerData<MutableList<ThrownTrident>>(TRIDENT_KEY) ?: Collections.synchronizedList(mutableListOf<ThrownTrident>())
            tridents.forEach {
                it.remove(Entity.RemovalReason.DISCARDED)
            }
            tridents.clear()
            val player = hgPlayer.serverPlayer ?: return@onClick

            repeat(tridentAmount) {
                tridents.add(ThrownTrident(player.world, player, Items.TRIDENT.defaultInstance).also {
                    it.setPos(player.eyePosition)
                    player.world.addFreshEntity(it)
                    it.pickup = AbstractArrow.Pickup.CREATIVE_ONLY
                })
            }

            mcCoroutineScope.launch {
                var i = 0
                while (tridents.isNotEmpty()) {
                    i++
                    tridents.forEachIndexed { index, thrownTrident ->
                        val angle = (2 * Math.PI * index / tridentAmount) + (i / 2)
                        val xOffset = 0.5 * cos(angle)
                        val zOffset = 0.5 * sin(angle)

                        val pos = player.eyePosition.add(xOffset, 0.3, zOffset)

                        thrownTrident.setPos(pos)
                        thrownTrident.deltaMovement = Vec3(0.0, 0.3, 0.0)
                    }
                    delay(1.ticks)
                }
            }

            player.hgPlayer.playerData[TRIDENT_KEY] = tridents

            hgPlayer.activateCooldown(kit)
        }
    }

    onEnable { hgPlayer, kit, serverPlayer ->
        hgPlayer.playerData[TRIDENT_KEY] = Collections.synchronizedList(mutableListOf<ThrownTrident>())
    }

    onDisable { hgPlayer, kit ->
        val tridents = hgPlayer.getPlayerData<MutableList<ThrownTrident>>(TRIDENT_KEY) ?: return@onDisable
        tridents.forEach {
            it.remove(Entity.RemovalReason.DISCARDED)
        }
        tridents.clear()
    }

    kitEvents {
        onSneak(ignoreCooldown = true) { hgPlayer, kit ->
            val tridents = hgPlayer.getPlayerData<MutableList<ThrownTrident>>(TRIDENT_KEY) ?: return@onSneak
            if (tridents.isEmpty()) return@onSneak
            val player = hgPlayer.serverPlayer ?: return@onSneak

            val trident = tridents.removeLast()

            trident.deltaMovement = player.lookAngle.normalize().times(2)
        }
    }

}