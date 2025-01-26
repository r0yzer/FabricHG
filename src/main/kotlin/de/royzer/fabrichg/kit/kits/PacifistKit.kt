package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.Items

val pacifistKit = kit("Pacifist") {
    description = "Take and deal only 3/4 damage"
    kitSelectorItem = Items.DANDELION.defaultInstance
}