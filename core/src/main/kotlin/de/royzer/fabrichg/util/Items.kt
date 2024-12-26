package de.royzer.fabrichg.util

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import java.util.Optional

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

fun kitSelector(index: Int) = if (index == 0) kitSelector else itemStack(Items.CHEST) {
    setCustomName { text("Kit Selector ${index + 1}") {
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

fun ItemStack.noPotionEffects() {
    val potionContents = get(DataComponents.POTION_CONTENTS) ?: return

    set(DataComponents.POTION_CONTENTS, PotionContents(
        potionContents.potion,
        potionContents.customColor,
        listOf()
    ))
}