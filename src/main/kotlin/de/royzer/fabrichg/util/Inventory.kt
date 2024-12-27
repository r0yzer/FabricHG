package de.royzer.fabrichg.util

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

val Inventory.everything: List<ItemStack> get() = items + armor + offhand

inline fun Inventory.forEach(crossinline block: (ItemStack) -> Unit) = everything.forEach(block)
