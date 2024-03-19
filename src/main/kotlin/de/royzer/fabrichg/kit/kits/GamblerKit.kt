package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.util.WeightedCollection
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.text.sendText
import kotlin.random.Random

val gamblerKit = kit("Gambler") {
    kitSelectorItem = Items.OAK_BUTTON.defaultInstance

    cooldown = 30.0 / 100

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val good = Random.nextBoolean()
            val loot = (if (good) goodGambler.get() else badGambler.get()) ?: return@onClick
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClick

            loot.action.invoke(serverPlayer)
            serverPlayer.sendText {
                text(loot.text)
                color = if (good) 0x46FF38 else 0xFF2530
            }

            hgPlayer.activateCooldown(kit)
        }
    }
}

private val goodGambler = WeightedCollection<GamblerAction>().also { collection ->
    collection.add(GamblerAction("You won a diamond") {
        it.inventory.add(itemStack(Items.DIAMOND, 1) {})
    }, 1.0)
    collection.add(GamblerAction("You won strength") {
        it.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 15))
    }, 1.0)
    collection.add(GamblerAction("You won some recraft") {
        it.inventory.add(itemStack(Items.BOWL, 16) {})
        it.inventory.add(itemStack(Items.RED_MUSHROOM, 16) {})
        it.inventory.add(itemStack(Items.BROWN_MUSHROOM, 16) {})
    }, 1.0)
}


private val badGambler = WeightedCollection<GamblerAction>().also { collection ->
    collection.add(GamblerAction("You won dirt...") {
        it.inventory.add(itemStack(Items.DIRT, 16) {})
    }, 1.0)
    collection.add(GamblerAction("You won poision...") {
        it.addEffect(MobEffectInstance(MobEffects.POISON, 20 * 10))
    }, 1.0)
}


private data class GamblerAction(
    val text: String,
    val action: ((ServerPlayer) -> Unit),
)