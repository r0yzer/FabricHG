package de.royzer.fabrichg.gui

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.GamePhaseManager.currentPhase
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.settings.GameSettings
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.observable.GuiProperty

fun gameSettingsGUI(serverPlayer: ServerPlayer): Gui {
    return igui(GuiType.NINE_BY_SIX, "Game settings".literal, 1) {
        page(1) {
            val minifeastStatus = GuiProperty(GameSettings.minifeastEnabled)
            placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)
            button(5 sl 2, minifeastStatus.guiIcon { enabled ->
                itemStack(Items.ENCHANTING_TABLE) {
                    this.setCustomName {
                        text("Minifeasts: ")
                        text(if (enabled) "enabled" else "disabled") {
                            color = if (enabled) 0x00FF00 else 0xFF0000
                            bold = enabled
                        }
                        italic = false
                        color = TEXT_GRAY
                    }
                }
            }, onClick = {
                GameSettings.minifeastEnabled = !GameSettings.minifeastEnabled
                minifeastStatus.set(GameSettings.minifeastEnabled)
            })
            button(1 sl 9, Items.IRON_SWORD.defaultInstance.also { it.setCustomName {
                text("Force start") {
                    bold = true
                    italic = false
                }
            } }.guiIcon,
                onClick = {
                    if (currentPhase != LobbyPhase) return@button
                    GamePhaseManager.timer.set(currentPhase.maxPhaseTime - 15)
                    serverPlayer.closeContainer()
                })
        }
    }
}