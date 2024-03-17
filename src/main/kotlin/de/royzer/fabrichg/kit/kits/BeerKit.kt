package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.kit
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionUtils
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setPotion
import net.silkmc.silk.nbt.set
import kotlin.time.Duration

fun colourToNumber(r: Int, g: Int, b: Int): Int {
    return (r shl 16) + (g shl 8) + b
}

val pilsPotion = Registry.register(BuiltInRegistries.POTION,
    "pils",
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 17, 1),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 24, 3),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 3),
    )
)
val weizenPotion = Registry.register(BuiltInRegistries.POTION,
    "weizen",
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 10, 1),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 13, 3),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 20, 3),
        MobEffectInstance(MobEffects.SATURATION, 20 * 60, 1),
    )
)

val hellesPotion = Registry.register(BuiltInRegistries.POTION,
    "helles",
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 10),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 12, 1),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 14),
    )
)

val starkbierPotion = Registry.register(BuiltInRegistries.POTION,
    "starkbier",
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 23, 3),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 24, 4),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 4),
    )
)

fun beerItem(potion: Potion, name: String, beerColor: Int, itemCount: Int = 2): ItemStack {
    return itemStack(Items.POTION) {
        setPotion(potion)
        val tag = orCreateTag
        tag?.set("CustomPotionColor", beerColor)
        count = itemCount
        setCustomName {
            text(name)
            color = 0xF3A101
        }
    }
}

val pilsBeerItem = beerItem(pilsPotion, "PILS", colourToNumber(236, 144, 1))
val weizenBierItem = beerItem(weizenPotion, "WEIZEN", colourToNumber(240, 150, 20))
val hellesBierItem = beerItem(hellesPotion, "HELLES", colourToNumber(255, 158, 3))
val starkbierPotionItem = beerItem(starkbierPotion, "STARKBIER", colourToNumber(158, 43, 0), 1)

val beerKit = kit("Beer") {

    kitSelectorItem = weizenBierItem.copy()

    description = "Recieve a few beers at the start of the round"

    kitItem {
        itemStack = pilsBeerItem.copy()
        droppable = false
    }

    kitItem {
        itemStack = weizenBierItem.copy()
        droppable = false
    }

    kitItem {
        itemStack = hellesBierItem.copy()
        droppable = false
    }

    kitItem {
        itemStack = starkbierPotionItem.copy()
        droppable = false
    }
}
