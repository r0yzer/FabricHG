package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.world.item.Items
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.milliseconds

val reviveKit = kit("Revive") {
    val reviveJobKey = "${this.kit.name}JobKey"
    kitSelectorItem = Items.TOTEM_OF_UNDYING.defaultInstance
    description = "Recieve a totem every 120 seconds"

    val defaultPeriod by property(120, "revive period (in seconds)")

    kitItem {
        itemStack = kitSelectorItem
        droppable = false
    }

    onEnable { hgPlayer, kit, player ->
        if (hgPlayer.playerData[reviveJobKey] != null) return@onEnable
        val job = mcCoroutineTask(
            howOften = Long.MAX_VALUE,
            period = (defaultPeriod * 1000L).milliseconds,
            delay = (defaultPeriod * 1000L).milliseconds
        ) {
            if (hgPlayer.serverPlayer?.inventory?.contains(kitSelectorItem) == false && hgPlayer.canUseKit(kit))
                hgPlayer.serverPlayer?.inventory?.add(kitSelectorItem.copy())
        }
        job.start()
        hgPlayer.playerData[reviveJobKey] = job
    }

}