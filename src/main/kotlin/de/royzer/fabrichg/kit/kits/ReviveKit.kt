package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.axay.fabrik.core.task.coroutineTask
import net.minecraft.item.Items

val reviveKit = kit("Revive") {
    val reviveJobKey = "${this.kit.name}JobKey"
    val defaultPeriod = 120 * 1000L
    kitSelectorItem = Items.TOTEM_OF_UNDYING.defaultStack

    kitItem {
        itemStack = kitSelectorItem
        droppable = false
    }

    onEnable { hgPlayer, kit ->
        if (hgPlayer.playerData[reviveJobKey] != null) return@onEnable
        val job = coroutineTask(howOften = Long.MAX_VALUE, period = defaultPeriod, delay = defaultPeriod) {
            if (hgPlayer.serverPlayerEntity?.inventory?.contains(kitSelectorItem) == false && hgPlayer.canUseKit(kit))
                hgPlayer.serverPlayerEntity?.inventory?.insertStack(kitSelectorItem.copy())
        }
        job.start()
        hgPlayer.playerData[reviveJobKey] = job
    }

}