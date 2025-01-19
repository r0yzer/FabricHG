package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.events.kititem.isKitItem
import de.royzer.fabrichg.kit.events.kititem.isKitItemOf
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import de.royzer.fabrichg.mixinskt.itemInMouse
import de.royzer.fabrichg.util.everything
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.silkmc.silk.core.task.infiniteMcCoroutineTask
import net.silkmc.silk.core.text.sendText
import kotlin.time.Duration.Companion.milliseconds

val reviveKit = kit("Revive") {
    val reviveJobKey = "${this.kit.name}JobKey"
    val reviveTimerKey = "${kit.name}TimerKey"

    kitSelectorItem = Items.TOTEM_OF_UNDYING.defaultInstance
    description = "Recieve a totem every 120 seconds"

    val revivePeriod by property(120, "revive period (in seconds)")

    val totemKitItem by kitItem {
        itemStack = kitSelectorItem
        droppable = false
    }

    onEnable { hgPlayer, kit, player ->
        if (hgPlayer.playerData[reviveJobKey] != null) return@onEnable

        val job = infiniteMcCoroutineTask(
            period = 1000L.milliseconds,
            delay = 1000L.milliseconds
        ) {
            if (!hgPlayer.canUseKit(kit)) return@infiniteMcCoroutineTask

            val serverPlayer = hgPlayer.serverPlayer ?: return@infiniteMcCoroutineTask

            fun ItemStack.isReviveItem(): Boolean
                = item == Items.TOTEM_OF_UNDYING && isKitItemOf(kit)

            if (serverPlayer.inventory.everything.any { it.isReviveItem() }) return@infiniteMcCoroutineTask
            if (serverPlayer.itemInMouse?.isReviveItem() == true) return@infiniteMcCoroutineTask

            val reviveTimer = hgPlayer.getPlayerData<Int>(reviveTimerKey) ?: 0

            if (reviveTimer >= revivePeriod) {
                hgPlayer.playerData[reviveTimerKey] = 0
                hgPlayer.serverPlayer?.inventory?.add(totemKitItem)
            } else {
                hgPlayer.playerData[reviveTimerKey] = reviveTimer + 1
            }

        }

        job.start()
        hgPlayer.playerData[reviveJobKey] = job
    }

}