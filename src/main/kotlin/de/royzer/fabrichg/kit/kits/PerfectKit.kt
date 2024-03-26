package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.mixinskt.SOUP_HEAL
import de.royzer.fabrichg.util.giveOrDropItem
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import kotlin.math.round
import kotlin.math.sqrt

val perfectKit = kit("Perfect") {
    kitSelectorItem = Items.BEACON.defaultInstance

    description = "Get rewarded for not presouping"

    val streakKey = "perfectStreak"

    val soupsForReward by property(7, "soups for reward")

    kitEvents {
        onSoupEat { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer ?: return@onSoupEat
            val streak = hgPlayer.getPlayerData<Int>(streakKey) ?: 0

            val presouped =
                (serverPlayer.health + SOUP_HEAL) > serverPlayer.attributes.getBaseValue(Attributes.MAX_HEALTH)

            if (!presouped) {
                hgPlayer.playerData[streakKey] = streak + 1
                if ((streak + 1) % soupsForReward == 0) {
                    val soupsToBeAdded =
                        round(sqrt(((streak + 1) / soupsForReward).toDouble() * 0.8)).toInt() + 1 // round(sqrt(x*0.8))+1 auf https://www.geogebra.org/calculator
                    repeat(soupsToBeAdded) {
                        serverPlayer.giveOrDropItem(itemStack(Items.RABBIT_STEW) {})
                        serverPlayer.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER,1f, 1f)
                    }
                }

            } else {
                if (streak > 1) {
                    serverPlayer.playNotifySound(SoundEvents.DONKEY_DEATH, SoundSource.MASTER,1f, 1f)
                }
                hgPlayer.playerData[streakKey] = 0
            }
        }
    }
}