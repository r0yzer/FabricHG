package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.seconds

// nochmal besser machen erst
val poseidonKit = kit("Poseidon") {
    kitSelectorItem = Items.TRIDENT.defaultInstance.also {
        it.enchant(Enchantments.RIPTIDE, 2)
        it.damageValue = 0
    }

    description = "Get a trident to move faster"

    kitItem {
        itemStack = kitSelectorItem
    }
    kitItem {
        itemStack = Items.WATER_BUCKET.defaultInstance
    }

    kitEvents {
        onKillPlayer { hgPlayer, kit, killed ->
            mcCoroutineTask(delay = 1.seconds, howOften = 10L) {
                hgPlayer.serverPlayer?.world?.setRainLevel(1.0f)
                hgPlayer.serverPlayer?.world?.setThunderLevel(1.0f)
            }
            hgPlayer.serverPlayer?.world?.setRainLevel(0.0f)
            hgPlayer.serverPlayer?.world?.setThunderLevel(0.0f)
        }
    }
}