package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.Items
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText

val jokerKit = kit("Joker") {
    kitSelectorItem = Items.MUSIC_DISC_5.defaultInstance
    cooldown = 45.0

    val shuffleDelay = 7.ticks
    val shuffleTimes = 7L
    val shufflesPerShuffle = 7

    kitItem {
        itemStack = kitSelectorItem.copy()

        onClickAtPlayer { hgPlayer, kit, clickedPlayer, hand ->
            hgPlayer.activateCooldown(kit)
            mcCoroutineTask(howOften = shuffleTimes, period = shuffleDelay) {
                repeat(shufflesPerShuffle) {
                    val item1 = clickedPlayer.inventory.items.random()
                    val item1Index = clickedPlayer.inventory.items.indexOf(item1)
                    val item2 = clickedPlayer.inventory.items.random()
                    val item2Index = clickedPlayer.inventory.items.indexOf(item2)

                    clickedPlayer.inventory.setItem(item1Index, item2)
                    clickedPlayer.inventory.setItem(item2Index, item1)
                }
            }.invokeOnCompletion {
                clickedPlayer.sendSystemMessage(literalText {
                    text("You have been jokered") { color = TEXT_GRAY }
                })
            }
        }
    }
}