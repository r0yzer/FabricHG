package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.kit
import kotlinx.coroutines.delay
import net.minecraft.core.component.DataComponents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.CustomData
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import net.silkmc.silk.nbt.dsl.nbtCompound
import java.util.Optional
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun colourToNumber(r: Int, g: Int, b: Int): Int {
    return (r shl 16) + (g shl 8) + b
}

val pilsPotion =
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 17, 1),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 24, 3),
        MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 30, 1),
    )

val weizenPotion =
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 10, 1),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 13, 3),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 20, 3),
        MobEffectInstance(MobEffects.SATURATION, 20 * 30),
    )


val hellesPotion =
    Potion(
        MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 10),
        MobEffectInstance(MobEffects.REGENERATION, 20 * 4, 3),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 14),

)

val starkbierPotion =
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 23, 3),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 24, 4),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 30, 2),
        MobEffectInstance(MobEffects.DIG_SPEED, 20 * 5, 20),
        MobEffectInstance(MobEffects.JUMP, 20 * 7, 20),
    )

val bierschissPotion =
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 25, 2),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 24, 4),
        MobEffectInstance(MobEffects.WEAKNESS, 20 * 24, 3),
        MobEffectInstance(MobEffects.WITHER, 20 * 5, 1),
    )

val asbachPotion =
    Potion(
        MobEffectInstance(MobEffects.BLINDNESS, 20 * 25, 20),
        MobEffectInstance(MobEffects.CONFUSION, 20 * 24, 40),
        MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 3, 5),
        MobEffectInstance(MobEffects.WITHER, 20 * 5, 2),
        MobEffectInstance(MobEffects.HEALTH_BOOST, 20 * 1, 2),
        MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 15, 2),
    )

val beerPotions = hashMapOf<Potion, ItemStack>()


fun beerItem(potion: Potion, name: String, beerColor: Int, itemCount: Int = 2): ItemStack {
    val item = itemStack(Items.POTION) {
        set(DataComponents.POTION_CONTENTS, PotionContents(
            Optional.empty(), // wenn man da die potion reinmacht sucht er in der registry wenn der in der registry ist hat der client die nicht in der registry und wenn er mit fabric spielt gehts nicht danke frau merkel
            Optional.of(beerColor),
            potion.effects
        ))
        set(DataComponents.CUSTOM_DATA, CustomData.of(nbtCompound {
            put("potionname", name)
        }))


        count = itemCount

        setCustomName {
            text(name)
            color = 0xF3A101
        }
    }

    beerPotions[potion] = item

    return item
}

val pilsBeerItem = beerItem(pilsPotion, "PILS", colourToNumber(236, 144, 1))
val weizenBierItem = beerItem(weizenPotion, "WEIZEN", colourToNumber(240, 150, 20))
val hellesBierItem = beerItem(hellesPotion, "HELLES", colourToNumber(255, 158, 3))
val starkbierPotionItem = beerItem(starkbierPotion, "STARKBIER", colourToNumber(158, 43, 0), 1)
val asbachPotionItem = beerItem(asbachPotion, "ASBACH URLAT", colourToNumber(150, 33, 13), 1)

val bierschissPotionItem = beerItem(bierschissPotion, "BIERSCHISS", colourToNumber(139,69,19), 1)


val ItemStack.beerPotion: Potion? get() {
    val pot = get(DataComponents.CUSTOM_DATA) ?: return null

    val name = pot.copyTag()?.get("potionname") ?: return null

    return beerPotions.entries.firstOrNull { entry ->
        entry.value.displayName.string == name.asString
    }?.key
}

const val ON_BEER_KEY = "on_beer"

val beerKit = kit("Beer") {
    kitSelectorItem = weizenBierItem.copy() // TODO: keine farbe ?!?

    description = "Recieve a few beers at the start of the round"

    val drinkBierschissAchievement by achievement("drink bierschiss") {
        level(10)
        level(50)
        level(100)
    }
    val killPlayersOnBeerAchievement by achievement("kill players on bier") {
        level(3)
        level(30)
        level(100)
    }

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

    kitItem {
        itemStack = asbachPotionItem.copy()
        droppable = false
    }

    kitEvents {
        onDrink { hgPlayer, kit, itemStack ->
            val potion = itemStack.beerPotion
            val player = hgPlayer.serverPlayer ?: return@onDrink

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
                bierschissPotion -> {
                    numBierschiss = 20
                    bierschisssDuration = 50.seconds

                    drinkBierschissAchievement.awardLater(player)
                }
                asbachPotion -> { }
            }

            val beer = hgPlayer.getPlayerData<Int>(ON_BEER_KEY)
            hgPlayer.playerData[ON_BEER_KEY] = (beer ?: 0) + 1

            mcCoroutineTask(delay = 5.seconds) {
                val newBeer = hgPlayer.getPlayerData<Int>(ON_BEER_KEY) ?: return@mcCoroutineTask

                if (beer == newBeer) hgPlayer.playerData.remove(ON_BEER_KEY)
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

        onKillPlayer { player, kit, killed ->
            val serverPlayer = player.serverPlayer ?: return@onKillPlayer

            val beer = player.getPlayerData<Int>(ON_BEER_KEY) ?: return@onKillPlayer

            killPlayersOnBeerAchievement.awardLater(serverPlayer)
        }
    }
}
