package de.royzer.fabrichg.kit.property

import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.KitBuilder
import de.royzer.fabrichg.settings.KitProperty
import kotlin.reflect.KProperty

interface Value<T> {
    var data: T
}

class KitPropertyDelegate<T : Any, K>(
    var defaultValue: T,
    val kit: Kit,
    var propertyName: String,
    val kitPropertyConstructor: (value: T) -> KitProperty
) where K : KitProperty, K: Value<T> {
    init {
        val value = kit.properties[propertyName]
        if (value == null) {
            kit.properties[propertyName] = kitPropertyConstructor(defaultValue)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        var value = (kit.properties[propertyName] as? K)?.data
        if (value == null) {
            kit.properties[propertyName] = kitPropertyConstructor(defaultValue)
            value = defaultValue
        }
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        kit.properties[propertyName] = kitPropertyConstructor(value)
    }
}

inline fun <reified T : Any> kitProperty(value: T, kit: Kit, propertyName: String): KitPropertyDelegate<T, *> {
    val constructor: (value: T) -> KitProperty = when (value) {
        is Int -> {
            { _value -> KitProperty.IntKitProperty(_value as Int) }
        }
        is Double -> {
            { _value -> KitProperty.DoubleKitProperty(_value as Double) }
        }
        is Boolean -> {
            { _value -> KitProperty.BooleanKitProperty(_value as Boolean) }
        }
        else -> error("unsupported type: ${T::class.qualifiedName}, value: $value")
    }
    return KitPropertyDelegate(value, kit, propertyName, constructor)
}

inline fun <reified T : Any> KitBuilder.property(value: T, propertyName: String) = kitProperty(value, kit, propertyName)