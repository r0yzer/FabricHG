package de.royzer.fabrichg.gui

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.GamePhaseManager.currentPhase
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.kit.kits.noneKit
import de.royzer.fabrichg.settings.GameSettings
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.observable.GuiProperty
import net.silkmc.silk.igui.observable.toGuiList
import net.silkmc.silk.igui.observable.toMutableGuiList


private val kitGuiList = kits.sortedBy { it.name.first() }.toMutableGuiList()

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
            changePageByKey(5 sl 3, Items.CHEST.defaultInstance.also {
                it.setCustomName {
                    text("Kits") {
                        bold = true
                        italic = false
                    }
                }
            }.guiIcon, "Kits")
            button(1 sl 9, Items.IRON_SWORD.defaultInstance.also {
                it.setCustomName {
                    text("Force start") {
                        bold = true
                        italic = false
                    }
                }
            }.guiIcon,
                onClick = {
                    if (currentPhase != LobbyPhase) return@button
                    GamePhaseManager.timer.set(currentPhase.maxPhaseTime - 15)
                    serverPlayer.closeContainer()
                })
        }
        page("Kits") {
            placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)

            val compound = compound(
                (2 sl 2) rectTo (5 sl 8),
                kitGuiList,
                iconGenerator = { kit ->
                    itemStack(kit.kitSelectorItem?.item ?: Items.BARRIER) {
                        tag = kit.kitSelectorItem?.tag
                        setLore(listOf())

                        setCustomName {
                            val disabled = GameSettings.disabledKits.contains(kit)
                            text(kit.name) {
                                strikethrough = disabled
                                italic = false
                                color = if (disabled) 0xFF0000 else 0x00FF00
                            }
                        }
                    }
                },
                onClick = { _, kit ->

                    val disabled = GameSettings.disabledKits.contains(kit)
                    if (!disabled) {
                        GameSettings.disableKit(kit)
                        serverPlayer.sendText {
                            text(kit.toString()) {
                                color = TEXT_BLUE
                                bold = true
                            }
                            text(" is now disabled") {
                                color = TEXT_GRAY
                            }
                        }
                    } else {
                        GameSettings.disabledKits.remove(kit)
                        serverPlayer.sendText {
                            text(kit.toString()) {
                                color = TEXT_BLUE
                                bold = true
                            }
                            text(" is now enabled") {
                                color = TEXT_GRAY
                            }
                        }
                    }
                    // danke BLUEfireoly (updated die farbe wenn disabled)
                    kitGuiList.mutate {}
                }
            )

            changePageByKey(6 sl 1, Items.FEATHER.defaultInstance.also {
                it.setCustomName {
                    text("Back") {
                        color = TEXT_GRAY
                        italic = false
                    }
                }
            }.guiIcon, 1)

            button(6 sl 9, Items.GREEN_WOOL.defaultInstance.also {
                it.setCustomName {
                    text("Enable all") {
                        color = 0x00FF00
                        italic = false
                    }
                }
            }.guiIcon, onClick = {
                GameSettings.disabledKits.clear()
                serverPlayer.sendText {
                    text("All kits") {
                        color = TEXT_BLUE
                        bold = true
                    }
                    text(" are now enabled") {
                        color = TEXT_GRAY
                    }
                }
                kitGuiList.mutate {}
            })
            button(1 sl 9, Items.RED_WOOL.defaultInstance.also {
                it.setCustomName {
                    text("Disable all")
                    color = 0xFF0000
                    italic = false
                }
            }.guiIcon, onClick = {
                GameSettings.disabledKits.addAll(kits)
                GameSettings.disabledKits.remove(noneKit)
                serverPlayer.sendText {
                    text("All kits") {
                        color = TEXT_BLUE
                        bold = true
                    }
                    text(" are now disabled") {
                        color = TEXT_GRAY
                    }
                }
                kitGuiList.mutate {}
            })

            compoundScrollBackwards(6 sl 5, Items.RED_STAINED_GLASS_PANE.guiIcon, compound, speed = 5.ticks)
            compoundScrollForwards(1 sl 5, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound, speed = 5.ticks)
        }
    }
}