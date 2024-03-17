package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.game.phase.phases.winnerText
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
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.nbt.set
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

fun colourToNumber(r: Int, g: Int, b: Int): Int {
    return (r shl 16) + (g shl 8) + b
}

val pilsPotion = Registry.register(BuiltInRegistries.POTION,
    "pils",
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 17, 1),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 24, 3),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 1),
    )
)
val weizenPotion = Registry.register(BuiltInRegistries.POTION,
    "weizen",
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 10, 1),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 13, 3),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 20, 3),
        MobEffectInstance(MobEffects.SATURATION, 20 * 30),
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
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 2),
    )
)
val bierschissPotion = Registry.register(BuiltInRegistries.POTION,
    "bierschiss",
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 25, 2),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 24, 4),
        MobEffectInstance(MobEffects.WEAKNESS, 20 * 24, 3),
        MobEffectInstance(MobEffects.WITHER, 20 * 5, 1),
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

val bierschissPotionItem = beerItem(bierschissPotion, "BIERSCHISS", colourToNumber(139,69,19), 1)

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

    kitEvents {
        onDrink { hgPlayer, itemStack ->
            val potion = PotionUtils.getPotion(itemStack)
            var numBierschiss: Long = 0
            var bierschisssDuration: Duration = 0.seconds
            when (potion) {
                pilsPotion -> {
                    numBierschiss = 4
                    bierschisssDuration = 20.seconds
                }
                weizenPotion -> {
                    numBierschiss = 2
                    bierschisssDuration = 40.seconds
                }
                hellesPotion -> {
                    numBierschiss = 6
                    bierschisssDuration = 60.seconds
                }
                starkbierPotion -> {
                    numBierschiss = 2
                    bierschisssDuration = 5.seconds
                }
            }

            if (numBierschiss > 0)
                mcCoroutineTask(
                    howOften = numBierschiss,
                    period = bierschisssDuration/numBierschiss.toInt(),
                    delay = (bierschisssDuration * Random.nextDouble())/3
                ) {
                    hgPlayer.serverPlayer?.inventory?.add(bierschissPotionItem.copy())
                    hgPlayer.serverPlayer?.sendText(literalText("looks like you have had too many beers") {
                        color = 0xCE4B14
                    })
                }
        }
    }
}
