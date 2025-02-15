package de.royzer.fabrichg.gui.gameSettins

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.GamePhaseManager.currentPhase
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.kit.kits
import de.royzer.fabrichg.kit.kits.noneKit
import de.royzer.fabrichg.settings.ConfigManager
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
import net.silkmc.silk.igui.GuiActionType.PICKUP
import net.silkmc.silk.igui.GuiActionType.SHIFT_CLICK
import net.silkmc.silk.igui.elements.GuiButtonPageChange
import net.silkmc.silk.igui.observable.GuiProperty
import net.silkmc.silk.igui.observable.toMutableGuiList


internal val kitGuiList = kits.sortedBy { it.name.first() }.toMutableGuiList()

private const val MAX_KITS = 4

suspend fun gameSettingsGUI(serverPlayer: ServerPlayer): Gui {
    val gameSettings = ConfigManager.gameSettings
    return igui(GuiType.NINE_BY_SIX, "Game settings".literal, 1) {
        page(1) {
            val minifeastStatus = GuiProperty(gameSettings.minifeastEnabled)
            val cowStatus = GuiProperty(gameSettings.mushroomCowNerf)
            val gulagStatus = GuiProperty(gameSettings.gulagEnabled)
            val achievementStatus = GuiProperty(gameSettings.achievementsEnabled)
            val gulagMinPlayersStatus = GuiProperty(gameSettings.minPlayersOutsideGulag)
            val gulagEndTime = GuiProperty(gameSettings.gulagEndTime)
            val kitAmountStatus = GuiProperty(gameSettings.kitAmount)
            val surpriseOnlyEnabledKitsStatus = GuiProperty(gameSettings.surpriseOnlyEnabledKits)
            val soupModeStatus = GuiProperty(gameSettings.soupMode)

            placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)
            button(5 sl 2, minifeastStatus.guiIcon { enabled ->
                itemStack(Items.ENCHANTING_TABLE) {
                    this.setCustomName {
                        text("Minifeasts: ")
                        text(if (enabled) "enabled" else "disabled") {
                            color = if (enabled) 0x00FF00 else 0xFF0000
                            bold = true
                        }
                        italic = false
                        color = TEXT_GRAY
                    }
                }
            }, onClick = {
                gameSettings.minifeastEnabled = !gameSettings.minifeastEnabled
                minifeastStatus.set(gameSettings.minifeastEnabled)
            })
            button(5 sl 3, cowStatus.guiIcon { enabled ->
                itemStack(Items.RED_MUSHROOM) {
                    this.setCustomName {
                        text("Mushroom cow nerf: ")
                        text(if (enabled) "enabled" else "disabled") {
                            color = if (enabled) 0x00FF00 else 0xFF0000
                            bold = true
                        }
                        italic = false
                        color = TEXT_GRAY
                    }
                }
            }, onClick = {
                gameSettings.mushroomCowNerf = !gameSettings.mushroomCowNerf
                cowStatus.set(gameSettings.mushroomCowNerf)
            })
            button(5 sl 4, gulagStatus.guiIcon { enabled ->
                itemStack(Items.BEDROCK) {
                    this.setCustomName {
                        text("Gulag: ")
                        text(if (enabled) "enabled" else "disabled") {
                            color = if (enabled) 0x00FF00 else 0xFF0000
                            bold = true
                        }
                        italic = false
                        color = TEXT_GRAY
                    }
                }
            }, onClick = {
                // erstmal raus
//                gameSettings.gulagEnabled = !gameSettings.gulagEnabled
//                gulagStatus.set(gameSettings.gulagEnabled)
            })
            button(5 sl 5, achievementStatus.guiIcon { enabled ->
                itemStack(Items.NETHER_STAR) {
                    this.setCustomName {
                        text("Achievements: ")
                        text(if (enabled) "enabled" else "disabled") {
                            color = if (enabled) 0x00FF00 else 0xFF0000
                            bold = true
                        }
                        italic = false
                        color = TEXT_GRAY
                    }
                }
            }, onClick = {
                // erstmal raus
                gameSettings.achievementsEnabled = !gameSettings.achievementsEnabled
                achievementStatus.set(gameSettings.achievementsEnabled)
            })
            button(4 sl 2, cowStatus.guiIcon { enabled ->
                itemStack(Items.PUFFERFISH) {
                    this.setCustomName {
                        text("Only enabled kits for Surprise: ")
                        text(if (enabled) "enabled" else "disabled") {
                            color = if (enabled) 0x00FF00 else 0xFF0000
                            bold = true
                        }
                        italic = false
                        color = TEXT_GRAY
                    }
                }
            }, onClick = {
                gameSettings.surpriseOnlyEnabledKits = !gameSettings.surpriseOnlyEnabledKits
                cowStatus.set(gameSettings.surpriseOnlyEnabledKits)
            })
            button(4 sl 3, soupModeStatus.guiIcon { soupMode ->
                itemStack(Items.MUSHROOM_STEW) {
                    this.setCustomName {
                        text("Soup mode: ")
                        text(soupMode.toString()) {
                            color = TEXT_BLUE
                            bold = true
                        }
                        italic = false
                        color = TEXT_GRAY
                    }
                }
            }, onClick = {
                val after = if (it.type == SHIFT_CLICK) gameSettings.soupMode.last() else gameSettings.soupMode.next()

                gameSettings.soupMode = after
                soupModeStatus.set(after)
            })

            button(5 sl 7, kitAmountStatus.guiIcon { amount ->
                itemStack(Items.TRAPPED_CHEST) {
                    this.setCustomName {
                        text("Kit amount: ")
                        text(amount.toString()) {
                            bold = true
                            color = TEXT_BLUE
                        }
                        italic = false
                        color = TEXT_GRAY
                    }
                }
            }, onClick = {
                // zu viele edge cases
//                if (it.type == SHIFT_CLICK) {
//                    if (gameSettings.kitAmount <= 1) {
//                        return@button
//                    }
//                    gameSettings.kitAmount -= 1
//                } else if (it.type == PICKUP) {
//                    if (gameSettings.kitAmount >= MAX_KITS) {
//                        return@button
//                    }
//                    gameSettings.kitAmount += 1
//                }
//                kitAmountStatus.set(gameSettings.kitAmount)
            })
            changePageByKey(5 sl 8, Items.CHEST.defaultInstance.also {
                it.setCustomName {
                    text("Kits") {
                        bold = true
                        italic = false
                        color = TEXT_GRAY
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
            }.guiIcon, onClick = {
                if (currentPhase != LobbyPhase) return@button
                GamePhaseManager.timer.set(currentPhase.maxPhaseTime - 15)
                serverPlayer.closeContainer()
            })
        }
        page("Kits") {
            placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)
            val compound = compound((2 sl 2) rectTo (5 sl 8), kitGuiList, iconGenerator = { kit ->
                itemStack(kit.kitSelectorItem?.item ?: Items.BARRIER) {
//                    tag = kit.kitSelectorItem?.tag TODO
                    setLore(listOf())
                    setCustomName {
                        val disabled = !kit.enabled
                        text(kit.name) {
                            strikethrough = disabled
                            italic = false
                            color = if (disabled) 0xFF0000 else 0x00FF00
                        }
                    }
                }
            }, onClick = { event, kit ->
                val pageKey = kit.name
                val newPage = GuiButtonPageChange.Calculator.StaticPageKey(pageKey).calculateNewPage(event.gui)
                if (newPage != null) {
                    event.gui.changePage(event.gui.currentPage, newPage)
                }
                // danke BLUEfireoly (updated die farbe wenn disabled)
                kitGuiList.mutate {}
            })

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

                kits.forEach { it.enabled = true }

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

                kits.filter { it != noneKit }.forEach {
                    it.enabled = false
                    ConfigManager.updateKit(it.name)
                }

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

            button(1 sl 1, Items.COMMAND_BLOCK.defaultInstance.also {
                it.setCustomName {
                    text("Save config") {
                        color = 0xE1A4FF
                    }
                    italic = false
                }
            }.guiIcon) {
                ConfigManager.updateConfigFile()
                it.player.sendText {
                    text("Config saved") {
                        color = TEXT_BLUE
                    }
                }
            }

            compoundScrollBackwards(6 sl 5, Items.RED_STAINED_GLASS_PANE.guiIcon, compound, speed = 5.ticks)
            compoundScrollForwards(1 sl 5, Items.GREEN_STAINED_GLASS_PANE.guiIcon, compound, speed = 5.ticks)
        }

        kits.forEach { kit ->
            page(kit.name) {
                val isEnabledProp = GuiProperty(kit.enabled)
                val cooldownProp = GuiProperty(kit.cooldown)
                val usableInInvincibilityProp = GuiProperty(kit.usableInInvincibility)
                val maxUsesProp = GuiProperty(kit.maxUses)

                placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)

                changePageByKey(6 sl 1, Items.FEATHER.defaultInstance.also {
                    it.setCustomName {
                        text("Back") {
                            color = TEXT_GRAY
                            italic = false
                        }
                    }
                }.guiIcon, "Kits")

                changePageByKey(1 sl 5, Items.FEATHER.defaultInstance.also {
                    it.setCustomName {
                        text("Additional Properties") {
                            color = TEXT_BLUE
                            italic = false
                        }
                    }
                }.guiIcon, "${kit.name}_properties")

                // kit enabled?
                button(5 sl 2, isEnabledProp.guiIcon { isEnabled ->
                    val icon = if (isEnabled) Items.GREEN_WOOL else Items.RED_WOOL
                    itemStack(icon, builder = {
                        this.setCustomName {
                            text("Enabled: ") {
                                color = TEXT_GRAY
                            }
                            text(isEnabled.toString()) {
                                color = if (isEnabled) 0x00FF00 else 0xFF0000
                                bold = true
                            }
                            italic = false
                        }
                    })
                }) { _ ->
                    val isEnabled = isEnabledProp.get()
                    kit.enabled = !isEnabled
                    isEnabledProp.set(!isEnabled)
                    ConfigManager.updateKit(kit.name)
                    kitGuiList.mutate { }
                }

                // usable in invincibility
                button(5 sl 3, usableInInvincibilityProp.guiIcon { usableInInvincibility ->
                    val icon = if (usableInInvincibility) Items.GREEN_WOOL else Items.RED_WOOL
                    itemStack(icon, builder = {
                        this.setCustomName {
                            text("Useable in in invincibility: ") {
                                color = TEXT_GRAY
                            }
                            text(usableInInvincibility.toString()) {
                                color = if (usableInInvincibility) 0x00FF00 else 0xFF0000
                                bold = true
                            }
                            italic = false
                        }
                    })
                }) { _ ->
                    val usableInInvincibility = usableInInvincibilityProp.get()
                    kit.usableInInvincibility = !usableInInvincibility
                    usableInInvincibilityProp.set(!usableInInvincibility)
                    ConfigManager.updateKit(kit.name)
                    kitGuiList.mutate { }
                }

                if (kit.maxUses != null) {
                    button(3 sl 5, maxUsesProp.guiIcon { maxUses ->
                        val icon = Items.DIAMOND_SWORD
                        itemStack(icon, builder = {
                            this.setCustomName {
                                text("Max amount of uses before cooldown: ") {
                                    color = TEXT_GRAY
                                }
                                text(maxUses.toString()) {
                                    color = 0xFFB125
                                }
                                italic = false
                            }
                        })
                    }) {

                    }

                    button(3 sl 4, maxUsesProp.guiIcon {
                        val icon = Items.STONE_BUTTON
                        itemStack(icon, builder = {
                            this.setCustomName {
                                text("-") {
                                    color = 0xFF0000
                                }
                                italic = false
                                bold = true
                            }
                        })
                    }) {
                        var newUses = maxUsesProp.get()?.minus(1)!!
                        if (newUses < 0) newUses = 0
                        kit.maxUses = newUses
                        maxUsesProp.set(newUses)
                        ConfigManager.updateKit(kit.name)
                        kitGuiList.mutate { }
                    }

                    button(3 sl 6, maxUsesProp.guiIcon {
                        val icon = Items.OAK_BUTTON
                        itemStack(icon, builder = {
                            this.setCustomName {
                                text("+") {
                                    color = 0x00FF00
                                }
                                italic = false
                                bold = true
                            }
                        })
                    }) {
                        val newUses = maxUsesProp.get()!!.plus(1)
                        kit.maxUses = newUses
                        maxUsesProp.set(newUses)
                        ConfigManager.updateKit(kit.name)
                        kitGuiList.mutate { }
                    }
                }


                // cooldown button
                if (kit.cooldown != null) {
                    button(2 sl 5, cooldownProp.guiIcon {
                        val icon = Items.CLOCK
                        itemStack(icon, builder = {
                            this.setCustomName {
                                text("Cooldown: ") {
                                    color = TEXT_GRAY
                                }
                                text(kit.cooldown.toString()) {
                                    color = 0xFFB125
                                }
                                italic = false
                            }
                        })
                    }) {

                    }

                    // cooldown minus button
                    button(2 sl 4, cooldownProp.guiIcon { cooldown ->
                        val icon = Items.STONE_BUTTON
                        itemStack(icon, builder = {
                            this.setCustomName {
                                text("-") {
                                    color = 0xFF0000
                                }
                                italic = false
                                bold = true
                            }
                        })
                    }) { event ->
                        var newCooldown = cooldownProp.get()!!
                        when (event.type) {
                            PICKUP -> {
                                newCooldown -= 0.5
                            }

                            SHIFT_CLICK -> {
                                newCooldown -= 1
                            }

                            else -> {}
                        }
                        if (newCooldown < 0) newCooldown = 0.0
                        kit.cooldown = newCooldown
                        cooldownProp.set(newCooldown)
                        ConfigManager.updateKit(kit.name)
                        kitGuiList.mutate { }
                    }

                    // cooldown add button
                    button(2 sl 6, cooldownProp.guiIcon {
                        val icon = Items.OAK_BUTTON
                        itemStack(icon, builder = {
                            this.setCustomName {
                                text("+") {
                                    color = 0x00FF00
                                }
                                italic = false
                                bold = true
                            }
                        })
                    }) { event ->
                        var newCooldown = cooldownProp.get()!!
                        when (event.type) {
                            PICKUP -> {
                                newCooldown += 0.5
                            }

                            SHIFT_CLICK -> {
                                newCooldown += 1
                            }

                            else -> {}
                        }

                        kit.cooldown = newCooldown
                        cooldownProp.set(newCooldown)
                        ConfigManager.updateKit(kit.name)
                        kitGuiList.mutate { }
                    }

                }
            }

            page("${kit.name}_properties") {
                kitPropertiesPage(kit)
            }
        }
    }
}
