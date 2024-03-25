@file:Suppress("UNCHECKED_CAST") // unchecked_castfireoly
package de.royzer.fabrichg.gui.gameSettins

import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.property.Value
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.settings.KitProperty
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.igui.GuiActionType
import net.silkmc.silk.igui.GuiBuilder
import net.silkmc.silk.igui.events.GuiClickEvent
import net.silkmc.silk.igui.guiIcon
import net.silkmc.silk.igui.observable.GuiProperty
import net.silkmc.silk.igui.sl


suspend fun GuiBuilder.PageBuilder.kitPropertiesPage(kit: Kit) {
    val otherProperties: List<GuiProperty<Pair<String, KitProperty>>> = kit.properties.map { GuiProperty(it.toPair()) }

    otherProperties.forEachIndexed { index, guiProperty ->
        when (guiProperty.get().second) {
            is KitProperty.DoubleKitProperty -> doubleProperty(kit, index, guiProperty as GuiProperty<Pair<String, KitProperty.DoubleKitProperty>>)
            is KitProperty.IntKitProperty -> intProperty(kit, index, guiProperty as GuiProperty<Pair<String, KitProperty.IntKitProperty>>)
            is KitProperty.BooleanKitProperty -> booleanProperty(kit, index, guiProperty as GuiProperty<Pair<String, KitProperty.BooleanKitProperty>>)
        }
    }
}

fun GuiBuilder.PageBuilder.doubleProperty(kit: Kit, index: Int, guiProperty: GuiProperty<Pair<String, KitProperty.DoubleKitProperty>>) {
    numberProperty(kit, index, guiProperty, clickPlus = { guiProperty, event ->
        val newCooldown = guiProperty.get().second
        when (event.type) {
            GuiActionType.PICKUP -> {
                newCooldown.data += 0.5
            }

            GuiActionType.SHIFT_CLICK -> {
                newCooldown.data += 1
            }

            else -> {}
        }
        if (newCooldown.data < 0) newCooldown.data = 0.0
        kit.properties[guiProperty.get().first] = newCooldown
        guiProperty.set(guiProperty.get().first to newCooldown)
        ConfigManager.updateKit(kit.name)
    }, clickMinus = { guiProperty, event ->
        var newCooldown = guiProperty.get().second
        when (event.type) {
            GuiActionType.PICKUP -> {
                newCooldown.data -= 0.5
            }

            GuiActionType.SHIFT_CLICK -> {
                newCooldown.data -= 1
            }

            else -> {}
        }
        if (newCooldown.data < 0) newCooldown.data = 0.0
        kit.properties[guiProperty.get().first] = newCooldown
        guiProperty.set(guiProperty.get().first to newCooldown)
        ConfigManager.updateKit(kit.name)
    })
}


fun GuiBuilder.PageBuilder.intProperty(kit: Kit, index: Int, guiProperty: GuiProperty<Pair<String, KitProperty.IntKitProperty>>) {
    numberProperty(kit, index, guiProperty, clickPlus = { guiProperty, event ->
        var newCooldown = guiProperty.get().second
        when (event.type) {
            GuiActionType.PICKUP -> {
                newCooldown.data += 1
            }

            GuiActionType.SHIFT_CLICK -> {
                newCooldown.data += 3
            }

            else -> {}
        }
        if (newCooldown.data < 0) newCooldown.data = 0
        kit.properties[guiProperty.get().first] = newCooldown
        guiProperty.set(guiProperty.get().first to newCooldown)
        ConfigManager.updateKit(kit.name)
    }, clickMinus = { guiProperty, event ->
        var newCooldown = guiProperty.get().second as KitProperty.IntKitProperty
        when (event.type) {
            GuiActionType.PICKUP -> {
                newCooldown.data -= 1
            }

            GuiActionType.SHIFT_CLICK -> {
                newCooldown.data -= 3
            }

            else -> {}
        }
        if (newCooldown.data < 0) newCooldown.data = 0
        kit.properties[guiProperty.get().first] = newCooldown
        guiProperty.set(guiProperty.get().first to newCooldown)
        ConfigManager.updateKit(kit.name)
    })
}

fun <T, V> GuiBuilder.PageBuilder.numberProperty(
    kit: Kit,
    index: Int,
    guiProperty: GuiProperty<Pair<String, T>>,
    clickPlus: suspend (property: GuiProperty<Pair<String, T>>, event: GuiClickEvent) -> Unit,
    clickMinus: suspend (property: GuiProperty<Pair<String, T>>, event: GuiClickEvent) -> Unit
) where T: KitProperty, T: Value<V> {
    button((index + 1) sl 5, guiProperty.guiIcon {
        val icon = Items.CLOCK
        itemStack(icon, builder = {
            this.setCustomName {
                text(it.first) {
                    color = TEXT_GRAY
                }
                text(it.second.data.toString()) {
                    color = 0xFFB125
                }
            }
        })
    }) {

    }

    // property - button
    button((index + 1) sl 4, guiProperty.guiIcon { cooldown ->
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
        clickMinus(guiProperty, event)
        kitGuiList.mutate { }
    }

    // cooldown add button
    button((index + 1) sl 6, guiProperty.guiIcon {
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
        clickPlus(guiProperty, event)
        kitGuiList.mutate { }
    }
}

fun GuiBuilder.PageBuilder.booleanProperty(
    kit: Kit,
    index: Int,
    guiProperty: GuiProperty<Pair<String, KitProperty.BooleanKitProperty>>
) {
    button((index + 1) sl 5, guiProperty.guiIcon {
        val icon = Items.GLASS_PANE
        itemStack(icon, builder = {
            setCustomName {
                text(it.first) {
                    color = TEXT_GRAY
                }
                text(it.second.data.toString()) {
                    color = 0xFFB125
                }
            }
        })
    }) {
        val newCooldown = KitProperty.BooleanKitProperty(!guiProperty.get().second.data)
        guiProperty.set(guiProperty.get().first to newCooldown)
        kit.properties[guiProperty.get().first] = newCooldown
        ConfigManager.updateKit(kit.name)
        kitGuiList.mutate {  }
    }
}