package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.util.WeightedCollection
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.entity.blockPos
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import kotlin.math.pow
import kotlin.random.Random

val gamblerKit = kit("Gambler") {
    kitSelectorItem = Items.OAK_BUTTON.defaultInstance

    cooldown = 30.0

    description = "Test your luck"

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
    }, 0.1)
    collection.add(GamblerAction("You won a iron ingot") {
        it.inventory.add(itemStack(Items.IRON_INGOT, 1) {})
    }, 0.3)
    collection.add(GamblerAction("You won wood") {
        it.inventory.add(itemStack(Items.OAK_PLANKS, 32) {})
    }, 0.8)
    collection.add(GamblerAction("Coco farm") {
        it.inventory.add(itemStack(Items.JUNGLE_LOG, 3) {})
        it.inventory.add(itemStack(Items.COCOA_BEANS, 6) {})
    }, 0.2)
    collection.add(GamblerAction("You won strength") {
        it.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 15))
    }, 0.5)
    collection.add(GamblerAction("You won some recraft") {
        val amount = 2f.pow(Random.nextInt(1, 5)).toInt()
        it.inventory.add(itemStack(Items.BOWL, amount) {})
        it.inventory.add(itemStack(Items.RED_MUSHROOM, amount) {})
        it.inventory.add(itemStack(Items.BROWN_MUSHROOM, amount) {})
    }, 1.0)
    collection.add(GamblerAction("You won a friend") {
        val wolf = Wolf(EntityType.WOLF, it.level())
        wolf.tame(it)
        it.level().addFreshEntity(wolf)
        wolf.setPos(it.pos)
    }, 0.2)
    collection.add(GamblerAction("Random kill") {
        val killed = PlayerList.alivePlayers.random()
        val serverPlayer = killed.serverPlayer ?: return@GamblerAction
        serverPlayer.hurt(serverPlayer.damageSources().playerAttack(serverPlayer), 1000f)
    }, 0.005)
}


private val badGambler = WeightedCollection<GamblerAction>().also { collection ->
    collection.add(GamblerAction("You won dirt...") {
        it.inventory.add(itemStack(Items.DIRT, 16) {})
    }, 1.0)
    collection.add(GamblerAction("You won poision...") {
        it.addEffect(MobEffectInstance(MobEffects.POISON, 20 * 10))
    }, 1.0)
    collection.add(GamblerAction("You won poision...") {
        it.addEffect(MobEffectInstance(MobEffects.POISON, 20 * 10))
    }, 1.0)
    collection.add(GamblerAction("You won levitation") {
        it.addEffect(MobEffectInstance(MobEffects.LEVITATION, 20 * 10))
    }, 1.0)
    collection.add(GamblerAction("You won some wheat") {
        it.inventory.add(itemStack(Items.WHEAT, 13) {})
    }, 1.0)
    collection.add(GamblerAction("You may want to look above you...") {
        it.world.setBlockAndUpdate(it.blockPos.subtract(Vec3i(0, -20, 0)), Blocks.ANVIL.defaultBlockState())
    }, 0.1)
    collection.add(GamblerAction("Instant death") {
        it.kill()
    }, 0.01)
    collection.add(GamblerAction("Coords leak") {
        broadcastComponent(
            literalText {
                text("${it.name}´s coordinates are: ") { color = TEXT_GRAY }
                text("${it.pos.x} ${it.pos.y} ${it.pos.z}") {
                    color = TEXT_BLUE
                    bold = true
                }
            }
        )
    }, 0.15)
    collection.add(GamblerAction("Nothing") {}, 0.2)
    collection.add(GamblerAction("Time to read") {
        it.world.setBlockAndUpdate(it.blockPos, Blocks.LECTERN.defaultBlockState())
    }, 0.35)

}


private data class GamblerAction(
    val text: String,
    val action: ((ServerPlayer) -> Unit),
)