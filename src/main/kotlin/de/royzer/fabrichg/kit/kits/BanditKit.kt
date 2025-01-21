package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.kit
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.HoverEvent
import net.minecraft.server.network.Filterable
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.WrittenBookContent
import net.silkmc.silk.core.text.literalText

const val BANDIT_KITS_KEY = "BanditKits"
const val BANDIT_KIT_KEY = "BanditKit"

private fun ItemStack.setBanditContent(player: HGPlayer?) {
    val kits = player?.getPlayerData<List<Kit>>(BANDIT_KITS_KEY) ?: listOf()

    if (kits.isEmpty()) {
        set(
            DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent(
                Filterable.passThrough("Literatur"),
                "Bandit",
                0,
                listOf(Filterable.passThrough(literalText("Du hast noch keine kits zur verfügung!") {
                    color = TEXT_GRAY
                })),
                true
            )
        )

        return
    }

    val maxKitsPerPage = 10

    val texts = kits.chunked(maxKitsPerPage).map { kitList ->
        literalText {
            kitList.forEach { kit ->
                text(kit.name) {
                    color = TEXT_BLUE

                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, literalText {
                        text("Click to select the ") { color = TEXT_GRAY }
                        text(kit.name) { color = TEXT_BLUE }
                        text(" Kit") { color = TEXT_GRAY }
                    })
                    clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/banditKit ${kit.name}")
                }
                newLine()
            }
        }
    }

    set(
        DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent(
            Filterable.passThrough("Literatur"),
            "Bandit",
            0,
            texts.map { Filterable.passThrough(it) },
            true
        )
    )
}

private fun Kit.isBanditApplicable() = this != noneKit && this.name != "Bandit"

val banditKit = kit("Bandit") {
    kitSelectorItem = Items.WRITABLE_BOOK.defaultInstance
    description = "steal enemies kits"

    cooldown = 120.0

    val kitItem = kitItem {
        itemStack = Items.WRITTEN_BOOK.defaultInstance.also {
            it.setBanditContent(null)
        }
    }

    info { hgPlayer, _ ->
        val kit = hgPlayer.getPlayerData<Kit>(BANDIT_KIT_KEY) ?: return@info null

        return@info literalText {
            text("Bandit kit: ") { color = TEXT_GRAY }
            text(kit.name) { color = TEXT_BLUE }
        }
    }

    kitEvents {
        onKillPlayer(ignoreCooldown = true) { hgPlayer, _, killed ->
            val killedHGPlayer = killed.hgPlayer

            val killedPlayerKits = killedHGPlayer.kits.filter { it.isBanditApplicable() }
            println("applicable: $killedPlayerKits")

            val currentKits = hgPlayer.getPlayerData<List<Kit>>(BANDIT_KITS_KEY) ?: listOf()
            val newKits = currentKits.plus(killedPlayerKits)

            println("new: $newKits")
            hgPlayer.playerData[BANDIT_KITS_KEY] = newKits

            kitItem.updateFor(hgPlayer) {
                setBanditContent(hgPlayer)
            }
        }
    }
}