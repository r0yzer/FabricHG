package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.Items
import net.silkmc.silk.core.text.literalText

val monkKit = kit("Monk") {
    kitSelectorItem = Items.BLAZE_ROD.defaultInstance
    cooldown = 14.0 / 100
    usableInInvincibility = false
    description = "Switch the item the enemy is currently holding with a random one of their inventory"

    kitItem {
        itemStack = kitSelectorItem

        onClickAtPlayer { hgPlayer, kit, clickedPlayer, hand ->
            val current = clickedPlayer.mainHandItem
            val currentIndex = clickedPlayer.inventory.items.indexOf(current)
            val random = clickedPlayer.inventory.items.random()
            val randomIndex = clickedPlayer.inventory.items.indexOf(random)
            clickedPlayer.inventory.setItem(currentIndex, random)
            clickedPlayer.inventory.setItem(randomIndex, current)
            clickedPlayer.sendSystemMessage(literalText {
                text("You have been monked") { color = TEXT_GRAY }
            })
            hgPlayer.activateCooldown(kit)
        }
    }
}