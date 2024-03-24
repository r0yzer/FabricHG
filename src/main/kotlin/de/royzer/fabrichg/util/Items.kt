package de.royzer.fabrichg.util

import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName

val tracker get() = itemStack(Items.COMPASS) {
    setCustomName { text("Tracker") {
        bold = true
        italic = false
    } }
}

val kitSelector get() = itemStack(Items.CHEST) {
    setCustomName { text("Kit Selector") {
        bold = true
        italic = false
    } }
}

fun kitSelector(index: Int) = if (index == 1) kitSelector else itemStack(Items.CHEST) {
    setCustomName { text("Kit Selector $index") {
        bold = true
        italic = false
    } }
}

val gameSettingsItem get() = itemStack(Items.COMPARATOR) {
    setCustomName { text("Game settings") {
        bold = true
        italic = false
    } }
}