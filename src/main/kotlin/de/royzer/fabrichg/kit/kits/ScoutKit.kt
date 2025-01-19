package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.events.kititem.isKitItemOf
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.mixinskt.itemInMouse
import de.royzer.fabrichg.util.everything
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.world.item.ItemStack
import net.silkmc.silk.core.item.setCustomName
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setPotion
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import net.silkmc.silk.core.text.sendText
import kotlin.time.Duration.Companion.milliseconds


//private val scoutPotion = PotionUtils.setPotion(
//    ItemStack(Items.SPLASH_POTION, 2),
//    Potion(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1))
//).also {
//    it.setCustomName{
//        text("Scout Potion")
//        color = 0x64F0FF
//    }
//    it.setLore(listOf(literalText("Kititem")))
//}

val scoutPotion = itemStack(Items.SPLASH_POTION) {
    setPotion(Potions.SWIFTNESS)
    count = 1
    setCustomName {
        text("Scout Potion")
        color = 0x64F0FF
    }
}

val scoutKit = kit("Scout") {
    val scoutJobKey = "${this.kit.name}JobKey"
    val scoutTimerKey = "${this.kit.name}TimerKey"

    val scoutPotionPeriod by property(5.0, "scout potion period (minutes)")


    kitSelectorItem = scoutPotion.copy()

    description = "Recieve a speed potions every 5 minutes"

    val scoutPotionKitItem by kitItem {
        itemStack = scoutPotion.copy()
        droppable = false
    }


    onEnable { hgPlayer, kit, player ->
        if (hgPlayer.playerData[scoutTimerKey] != null) return@onEnable

        val job = infiniteMcCoroutineTask(
            period = 1000L.milliseconds,
            delay = 1000L.milliseconds
        ) {
            if (!hgPlayer.canUseKit(kit)) return@infiniteMcCoroutineTask

            val serverPlayer = hgPlayer.serverPlayer ?: return@infiniteMcCoroutineTask

            fun ItemStack.isScoutItem(): Boolean
                    = item == Items.SPLASH_POTION && isKitItemOf(kit)

            if (serverPlayer.inventory.everything.any { it.isScoutItem() }) return@infiniteMcCoroutineTask
            if (serverPlayer.itemInMouse?.isScoutItem() == true) return@infiniteMcCoroutineTask

            val scoutTimer = hgPlayer.getPlayerData<Int>(scoutTimerKey) ?: 0

            if (scoutTimer >= (scoutPotionPeriod * 60)) {
                hgPlayer.playerData[scoutTimerKey] = 0
                hgPlayer.serverPlayer?.inventory?.add(scoutPotionKitItem)
            } else {
                hgPlayer.playerData[scoutTimerKey] = scoutTimer + 1
            }

        }

        job.start()
        hgPlayer.playerData[scoutJobKey] = job
    }
}
