package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.mixinskt.SOUP_HEAL
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import kotlin.math.round
import kotlin.math.sqrt

val perfectKit = kit("Perfect") {
    kitSelectorItem = Items.BEACON.defaultInstance

    val streakKey = "perfectStreak"

    val soupsForReward = 7

    kitEvents {
        onSoupEat { hgPlayer ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onSoupEat
            val streak = hgPlayer.getPlayerData<Int>(streakKey) ?: 0

            val presouped =
                (serverPlayer.health + SOUP_HEAL) > serverPlayer.attributes.getBaseValue(Attributes.MAX_HEALTH)

            if (!presouped) {
                hgPlayer.playerData[streakKey] = streak + 1
                if ((streak + 1) % soupsForReward == 0) {
                    val soupsToBeAdded =
                        round(sqrt((streak / soupsForReward).toDouble() * 1.3)).toInt() + 1 // round(sqrt(x*1.3))+1 auf https://www.geogebra.org/calculator
                    repeat(soupsToBeAdded) {
                        serverPlayer.inventory.add(itemStack(Items.RABBIT_STEW) {})
                        serverPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP)
                    }
                }

            } else {
                if (streak > 1) {
                    serverPlayer.playSound(SoundEvents.DONKEY_HURT)
                }
                hgPlayer.playerData[streakKey] = 0
            }
        }
    }
}