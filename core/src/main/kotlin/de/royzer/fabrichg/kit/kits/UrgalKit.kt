package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.silkmc.silk.core.item.setCustomName
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setPotion

val urgalPotion = itemStack(Items.POTION) {
    setPotion(Potions.STRENGTH)
    count = 1
    setCustomName {
        text("Urgal Potion")
        color = 0x64F0FF
    }
}

val urgalKit = kit("Urgal") {

    kitSelectorItem = urgalPotion.copy()

    description = "Recieve a strength potion at the start of the round"

    kitItem {
        itemStack = urgalPotion.copy()
        droppable = false
    }
}
