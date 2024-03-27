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
import net.silkmc.silk.igui.*
import net.silkmc.silk.igui.events.GuiClickEvent
import net.silkmc.silk.igui.observable.GuiProperty

const val propertiesPerSlot = 4

suspend fun GuiBuilder.PageBuilder.kitPropertiesPage(kit: Kit) {
    placeholder(Slots.Border, Items.GRAY_STAINED_GLASS_PANE.guiIcon)

    changePageByKey(6 sl 1, Items.FEATHER.defaultInstance.also {
        it.setCustomName {
            text("Back") {
                color = TEXT_GRAY
                italic = false
            }
        }
    }.guiIcon, kit.name)

    val otherProperties: List<GuiProperty<Pair<String, KitProperty>>> = kit.properties.map { GuiProperty(it.toPair()) }

    otherProperties.forEachIndexed { index, guiProperty ->
        when (guiProperty.get().second) {
            is KitProperty.DoubleKitProperty ->
                doubleProperty(kit, index, otherProperties.size, guiProperty as GuiProperty<Pair<String, KitProperty.DoubleKitProperty>>)
            is KitProperty.IntKitProperty ->
                intProperty(kit, index, otherProperties.size, guiProperty as GuiProperty<Pair<String, KitProperty.IntKitProperty>>)
            is KitProperty.BooleanKitProperty ->
                booleanProperty(kit, index, otherProperties.size, guiProperty as GuiProperty<Pair<String, KitProperty.BooleanKitProperty>>)
            is KitProperty.FloatKitProperty ->
                floatProperty(kit, index, otherProperties.size, guiProperty as GuiProperty<Pair<String, KitProperty.FloatKitProperty>>)

        }
    }
}

fun GuiBuilder.PageBuilder.doubleProperty(
    kit: Kit,
    index: Int,
    maxSlots: Int,
    guiProperty: GuiProperty<Pair<String, KitProperty.DoubleKitProperty>>
) {
    numberProperty(kit, index, maxSlots, guiProperty, clickPlus = { guiProperty, event ->
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


fun GuiBuilder.PageBuilder.floatProperty(
    kit: Kit,
    index: Int,
    maxSlots: Int,
    guiProperty: GuiProperty<Pair<String, KitProperty.FloatKitProperty>>
) {
    numberProperty(kit, index, maxSlots, guiProperty, clickPlus = { guiProperty, event ->
        val newCooldown = guiProperty.get().second
        when (event.type) {
            GuiActionType.PICKUP -> {
                newCooldown.data += 0.1f
            }

            GuiActionType.SHIFT_CLICK -> {
                newCooldown.data += 0.5f
            }

            else -> {}
        }
        if (newCooldown.data < 0) newCooldown.data = 0.0f
        kit.properties[guiProperty.get().first] = newCooldown
        guiProperty.set(guiProperty.get().first to newCooldown)
        ConfigManager.updateKit(kit.name)
    }, clickMinus = { guiProperty, event ->
        var newCooldown = guiProperty.get().second
        when (event.type) {
            GuiActionType.PICKUP -> {
                newCooldown.data -= 0.1f
            }

            GuiActionType.SHIFT_CLICK -> {
                newCooldown.data -= 0.5f
            }

            else -> {}
        }
        if (newCooldown.data < 0) newCooldown.data = 0.0f
        kit.properties[guiProperty.get().first] = newCooldown
        guiProperty.set(guiProperty.get().first to newCooldown)
        ConfigManager.updateKit(kit.name)
    })
}

fun GuiBuilder.PageBuilder.intProperty(
    kit: Kit,
    index: Int,
    maxSlots: Int,
    guiProperty: GuiProperty<Pair<String, KitProperty.IntKitProperty>>
) {
    numberProperty(kit, index, maxSlots, guiProperty, clickPlus = { guiProperty, event ->
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
    size: Int,
    guiProperty: GuiProperty<Pair<String, T>>,
    clickPlus: suspend (property: GuiProperty<Pair<String, T>>, event: GuiClickEvent) -> Unit,
    clickMinus: suspend (property: GuiProperty<Pair<String, T>>, event: GuiClickEvent) -> Unit
) where T: KitProperty, T: Value<V> {
    val pos = getPosition(index, size)
    button(pos, guiProperty.guiIcon {
        val icon = Items.CLOCK
        itemStack(icon, builder = {
            setCustomName {
                text("${it.first}: ") {
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
    button(pos - (0 to 1), guiProperty.guiIcon { cooldown ->
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

    // property add button
    button(pos + (0 to 1), guiProperty.guiIcon {
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
    size: Int,
    guiProperty: GuiProperty<Pair<String, KitProperty.BooleanKitProperty>>
) {
    val pos = getPosition(index, size)
    button(pos, guiProperty.guiIcon {
        val icon = Items.GLASS_PANE
        itemStack(icon, builder = {
            setCustomName {
                text("${it.first}: ") {
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

fun getPosition(
    index: Int,
    size: Int
): GuiSlot {
    val useTwoColumns = size > propertiesPerSlot
    val onSecondColumn = useTwoColumns && (index + 1) > propertiesPerSlot

    return when {
        !useTwoColumns -> (index + 2) sl 5

        // auf jeden fall 2 columns
        !onSecondColumn -> (index + 2) sl 3
        onSecondColumn -> (index + 2 - propertiesPerSlot) sl 7

        else -> -1 sl -1
    }

}

operator fun GuiSlot.plus(brain: Pair<Int, Int>)
    = (row + brain.first) sl (slotInRow + brain.second)

operator fun GuiSlot.minus(brain: Pair<Int, Int>)
    = (row - brain.first) sl (slotInRow - brain.second)