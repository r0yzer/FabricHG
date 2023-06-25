package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.Items
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.milliseconds

val reviveKit = kit("Revive") {
    val reviveJobKey = "${this.kit.name}JobKey"
    val defaultPeriod = 120 * 1000L
    kitSelectorItem = Items.TOTEM_OF_UNDYING.defaultInstance

    kitItem {
        itemStack = kitSelectorItem
        droppable = false
    }

    onEnable { hgPlayer, kit ->
        if (hgPlayer.playerData[reviveJobKey] != null) return@onEnable
        val job = mcCoroutineTask(
            howOften = Long.MAX_VALUE,
            period = defaultPeriod.milliseconds,
            delay = defaultPeriod.milliseconds
        ) {
            if (hgPlayer.serverPlayer?.inventory?.contains(kitSelectorItem) == false && hgPlayer.canUseKit(kit))
                hgPlayer.serverPlayer?.inventory?.add(kitSelectorItem.copy())
        }
        job.start()
        hgPlayer.playerData[reviveJobKey] = job
    }

}