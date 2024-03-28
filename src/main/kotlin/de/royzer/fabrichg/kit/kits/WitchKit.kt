package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.mixins.world.CombatTrackerAcessor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.ThrownPotion
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.alchemy.Potions
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setPotion
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import kotlin.random.Random

val witchPotionTag = "WITCH_KIT_POTION"

val witchKit = kit("Witch") {
    kitSelectorItem = itemStack(Items.POTION) {
        setPotion(
            Potions.HARMING
        )
    }

    val potionCooldown by property(5, "how often the kit should be called (seconds)")
    val random = Random
    onEnable { hgPlayer, kit, serverPlayer ->
        infiniteMcCoroutineTask(period = (20 * potionCooldown).ticks) {
            if (hgPlayer.inFight) {
                val lastCombatEntry = (serverPlayer.combatTracker as CombatTrackerAcessor).entries.lastOrNull()
                if (lastCombatEntry == null) println("null")
                if (lastCombatEntry?.source?.entity is ServerPlayer) {
                    val target = lastCombatEntry.source.entity as ServerPlayer
                    if(!target.hgPlayer.isAlive) return@infiniteMcCoroutineTask
                    val toX: Double = target.x - serverPlayer.x
                    val toY: Double = target.eyeY - 1.1 - serverPlayer.y
                    val toZ: Double = target.z - serverPlayer.z
                    val potion = if (random.nextFloat() <= 0.20f) {
                        Potions.SLOWNESS
                    } else if (random.nextFloat() <= 0.20f) {
                        Potions.POISON
                    } else {
                        Potions.HARMING
                    }
                    val thrownPotion = ThrownPotion(serverPlayer.level(), serverPlayer)
                    thrownPotion.item =
                        PotionUtils.setPotion(ItemStack(Items.SPLASH_POTION), potion)
                    thrownPotion.addTag(witchPotionTag)
                    thrownPotion.xRot -= -20.0f
                    thrownPotion.shoot(toX, toY, toZ, 0.75f, 8.0f)
                    serverPlayer.level().addFreshEntity(thrownPotion)
                }
            }
        }
    }

}