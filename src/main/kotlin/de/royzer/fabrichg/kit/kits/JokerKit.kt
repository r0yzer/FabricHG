package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.world.item.Items
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText

val jokerKit = kit("Joker") {
    kitSelectorItem = Items.MUSIC_DISC_5.defaultInstance
    cooldown = 70.0

    val shuffleDelay by property(7, "shuffle delay (in ticks)")
    val shuffleTimes by property(6, "shuffle times")
    val shufflesPerShuffle by property(4, "shuffles per shuffle")

    val shuffleTimesAchievement by achievement("shuffle times") {
        level(500)
        level(900)
        level(3000)
    }

    kitItem {
        itemStack = kitSelectorItem.copy()

        onClickAtPlayer { hgPlayer, kit, clickedPlayer, hand ->
            if (clickedPlayer.hgPlayer.isNeo) return@onClickAtPlayer
            val player = hgPlayer.serverPlayer ?: return@onClickAtPlayer

            hgPlayer.activateCooldown(kit)
            mcCoroutineTask(howOften = shuffleTimes.toLong(), period = shuffleDelay.ticks) {
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

            shuffleTimesAchievement.awardLater(player)
        }
    }
}