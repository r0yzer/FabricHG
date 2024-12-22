package de.royzer.fabrichg.gui

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.gui.gameSettins.kitGuiList
import de.royzer.fabrichg.gui.gameSettins.kitPropertiesPage
import de.royzer.fabrichg.gui.gameSettins.plus
import de.royzer.fabrichg.kit.achievements.AchievementManager
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.util.noPotionEffects
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.igui.GuiType
import net.silkmc.silk.igui.Slots
import net.silkmc.silk.igui.changePage
import net.silkmc.silk.igui.elements.GuiButtonPageChange
import net.silkmc.silk.igui.guiIcon
import net.silkmc.silk.igui.igui
import net.silkmc.silk.igui.observable.toGuiList
import net.silkmc.silk.igui.rectTo
import net.silkmc.silk.igui.sl


fun kitAchievementsGui(serverPlayerEntity: ServerPlayer) = igui(GuiType.NINE_BY_FIVE, "Kit Achievements".literal, 1) {
    val hgPlayer = serverPlayerEntity.hgPlayer

    page(1) {
        placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)
        val compound = compound(
            (2 sl 2) rectTo (4 sl 8),
            kits.sortedBy { it.name.first() }.toGuiList(),
            iconGenerator = { kit ->
                val defaultItemStack = itemStack(Items.BARRIER) { }

                (kit.kitSelectorItem ?: defaultItemStack).copy().apply {
                    noPotionEffects()

                    setCustomName {
                        text(kit.name) {
                            color = if (hgPlayer.hasKit(kit)) 0x00FF00 else 0x00FFFF
                            strikethrough = false
                            bold = hgPlayer.hasKit(kit)
                            italic = false
                        }
                    }

                    if (kit.description.isNotBlank())
                        setLore(listOf(literalText(kit.description) {
                            color = if (hgPlayer.hasKit(kit)) 0xF1F2F1 else 0x4C3E4B
                            strikethrough = false
                            bold = false
                            italic = false
                        }))
                }
            }, onClick = { event, kit ->
                val pageKey = "${kit.name}_achievements"
                val newPage = GuiButtonPageChange.Calculator.StaticPageKey(pageKey).calculateNewPage(event.gui)
                if (newPage != null) {
                    event.gui.changePage(event.gui.currentPage, newPage)
                }
                // danke BLUEfireoly (updated die farbe wenn disabled)
                kitGuiList.mutate {}
            }
        )

        compoundScrollBackwards(5 sl 5, Items.RED_STAINED_GLASS_PANE.guiIcon, compound, speed = 3.ticks)
        compoundScrollForwards(1 sl 5, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound, speed = 3.ticks)
    }

    kits.sortedBy { it.name.first() }.forEach { kit ->
        page("${kit.name}_achievements") {
            kit.achievements.forEachIndexed { index, achievement ->
                val state = achievement.getMemoryState(serverPlayerEntity)

                button(
                    3 sl index + 2,
                    itemStack(Items.FEATHER) {
                        setCustomName {
                            if (state.level == null) {
                                text(state.achievement.name) { color = TEXT_GRAY; bold = true }
                                text(" ${state.state}") { color = TEXT_BLUE; italic = true }
                            } else {
                                text(state.achievement.name) { color = TEXT_GRAY; bold = true }
                                text("(") { color = TEXT_GRAY }
                                text("${state.level.number}") { color = TEXT_BLUE; italic = true }
                                text(")") { color = TEXT_GRAY }
                                text(" ${state.state}") { color = TEXT_BLUE; italic = true }
                                text("/") { color = TEXT_GRAY }
                                text("${state.level.required}") { color = TEXT_BLUE; italic = true }
                            }
                        }
                    }.guiIcon
                ) {
                    serverPlayerEntity.sendSystemMessage("nice hast $state".literal)
                }
            }
        }
    }
}
