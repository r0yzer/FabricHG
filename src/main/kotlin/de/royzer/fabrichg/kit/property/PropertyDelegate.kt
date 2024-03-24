package de.royzer.fabrichg.kit.property

import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.KitBuilder
import kotlin.reflect.KProperty

class KitPropertyDelegate<T : Any>(var defaultValue: T, val kit: Kit) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        var value = kit.properties[property.name] as? T?
        if (value == null) {
            kit.properties[property.name] = defaultValue
            value = defaultValue
        }
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        kit.properties[property.name] = value
    }
}

fun <T : Any> kitProperty(value: T, kit: Kit) = KitPropertyDelegate<T>(value, kit)

fun <T : Any> KitBuilder.property(value: T) = KitPropertyDelegate<T>(value, kit)