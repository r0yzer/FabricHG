package de.royzer.fabrichg.util

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask

fun ServerPlayer.giveOrDropItem(item: ItemStack) {
    if (!inventory.add(item)) {
        val itemEntity: ItemEntity? = drop(item, false)
        itemEntity?.setNoPickUpDelay()
        itemEntity?.setTarget(this.uuid)
    }
}
fun ServerPlayer.giveOrDropItems(items: List<ItemStack>) {
    items.forEach {
        this.giveOrDropItem(it)
    }
}

fun ServerPlayer.forceGiveItem(item: ItemStack) {
    if (inventory.add(item)) {
        return
    }
    mcCoroutineTask(delay = 5.ticks) {
        this@forceGiveItem.forceGiveItem(item)
    }
}