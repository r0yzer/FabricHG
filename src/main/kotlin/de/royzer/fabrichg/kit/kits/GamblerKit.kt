package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcast
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.randomKit
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.WeightedCollection
import de.royzer.fabrichg.util.giveOrDropItem
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.MushroomCow
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.entity.blockPos
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

val gamblerKit = kit("Gambler") {
    kitSelectorItem = Items.OAK_BUTTON.defaultInstance

    cooldown = 30.0

    description = "Test your luck"

    val goodGamblerAchievement by achievement("lucky gambler") {
        level(200)
        level(1000)
        level(2500)
    }
    val diaGamblerAchievement by achievement("gamble diamonds") {
        level(30)
        level(100)
        level(700)
    }

    kitItem {
        itemStack = kitSelectorItem
        onClick { hgPlayer, kit ->
            val good = Random.nextBoolean()
            val loot = (if (good) goodGambler.get() else badGambler.get()) ?: return@onClick
            val serverPlayer = hgPlayer.serverPlayer ?: return@onClick

            if (loot.text == "You won a full diamond kit") {
                diaGamblerAchievement.awardLater(serverPlayer, 26)
            } else if (loot.text == "You won a diamond") {
                diaGamblerAchievement.awardLater(serverPlayer, 26)
            }
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
    collection.add(GamblerAction("You won a full diamond kit") {
        it.giveOrDropItem(itemStack(Items.DIAMOND_SWORD, 1) {})
        it.giveOrDropItem(itemStack(Items.DIAMOND_HELMET, 1) {})
        it.giveOrDropItem(itemStack(Items.DIAMOND_CHESTPLATE, 1) {})
        it.giveOrDropItem(itemStack(Items.DIAMOND_LEGGINGS, 1) {})
        it.giveOrDropItem(itemStack(Items.DIAMOND_BOOTS, 1) {})
    }, 0.01)
    collection.add(GamblerAction("You won a diamond") {
        it.giveOrDropItem(itemStack(Items.DIAMOND, 1) {})
    }, 0.075)
    collection.add(GamblerAction("You won a iron ingot") {
        it.giveOrDropItem(itemStack(Items.IRON_INGOT, 1) {})
    }, 0.2)
    collection.add(GamblerAction("You won a iron sword") {
        it.giveOrDropItem(itemStack(Items.IRON_SWORD, 1) {})
    }, 0.1)
    collection.add(GamblerAction("You won wood") {
        it.giveOrDropItem(itemStack(Items.OAK_PLANKS, 32) {})
    }, 0.8)
    collection.add(GamblerAction("You a full chain set") {
        it.giveOrDropItem(itemStack(Items.CHAINMAIL_HELMET, 1) {})
        it.giveOrDropItem(itemStack(Items.CHAINMAIL_CHESTPLATE, 1) {})
        it.giveOrDropItem(itemStack(Items.CHAINMAIL_LEGGINGS, 1) {})
        it.giveOrDropItem(itemStack(Items.CHAINMAIL_BOOTS, 1) {})
        it.giveOrDropItem(itemStack(Items.STONE_SWORD, 1) {})
    }, 0.20)
    collection.add(GamblerAction("You a full gold set") {
        it.giveOrDropItem(itemStack(Items.GOLDEN_HELMET, 1) {})
        it.giveOrDropItem(itemStack(Items.GOLDEN_CHESTPLATE, 1) {})
        it.giveOrDropItem(itemStack(Items.GOLDEN_LEGGINGS, 1) {})
        it.giveOrDropItem(itemStack(Items.GOLDEN_BOOTS, 1) {})
        it.giveOrDropItem(itemStack(Items.GOLDEN_SWORD, 1) {})
    }, 0.3)
    collection.add(GamblerAction("Coco farm") {
        it.giveOrDropItem(itemStack(Items.JUNGLE_LOG, 3) {})
        it.giveOrDropItem(itemStack(Items.COCOA_BEANS, 12) {})
    }, 0.2)
    collection.add(GamblerAction("You won strength") {
        it.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 15))
    }, 0.4)
    collection.add(GamblerAction("You won jump boost") {
        it.addEffect(MobEffectInstance(MobEffects.JUMP, 20 * 15, 3))
    }, 0.5)
    collection.add(GamblerAction("You won resistance") {
        it.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 15, 0))
    }, 0.5)
    collection.add(GamblerAction("Extra heart") {
        val max = it.attributes.getBaseValue(Attributes.MAX_HEALTH)
        it.attributes.getInstance(Attributes.MAX_HEALTH)?.baseValue = max + 2.0
    }, 0.1)
    collection.add(GamblerAction("You won some recraft") {
        val amount = 2f.pow(Random.nextInt(1, 5)).toInt()
        it.giveOrDropItem(itemStack(Items.BOWL, amount) {})
        it.giveOrDropItem(itemStack(Items.RED_MUSHROOM, amount) {})
        it.giveOrDropItem(itemStack(Items.BROWN_MUSHROOM, amount) {})
    }, 0.5)
    collection.add(GamblerAction("You won a friend") {
        val wolf = Wolf(EntityType.WOLF, it.level())
        wolf.tame(it)
        it.level().addFreshEntity(wolf)
        wolf.setPos(it.pos)
    }, 0.2)
    collection.add(GamblerAction("Random kill") {
        val killed = PlayerList.alivePlayers.random()
        val serverPlayer = killed.serverPlayer ?: return@GamblerAction
        broadcastComponent(literalText {
            text("${killed.name} was randomkilled from a gambler lachkick")
            color = 0xFFFF55
        })
        serverPlayer.hurt(serverPlayer.damageSources().playerAttack(serverPlayer), 1000f)
    }, 0.005)
    collection.add(GamblerAction("OP") {
        server.playerList.op(it.gameProfile)
        broadcast("tmm")
    }, 0.000001)
    collection.add(GamblerAction("Helm") {
        it.inventory.armor[3] = Items.IRON_HELMET.defaultInstance
    }, 0.1)
    collection.add(GamblerAction("End crystal") {
        it.giveOrDropItem(itemStack(Items.END_CRYSTAL, 1) {})
        it.giveOrDropItem(itemStack(Items.OBSIDIAN, 1) {})
    }, 0.05)
    collection.add(GamblerAction("Bow") {
        it.giveOrDropItem(itemStack(Items.BOW, 1) {})
        it.giveOrDropItem(itemStack(Items.ARROW, 5) {})
    }, 0.25)
    collection.add(GamblerAction("TNT") {
        it.giveOrDropItem(itemStack(Items.TNT, 1) {})
        it.giveOrDropItem(itemStack(Items.FLINT_AND_STEEL, 5) {})
    }, 0.2)
    collection.add(GamblerAction("Mooshroom") {
        val mooshroom = MushroomCow(EntityType.MOOSHROOM, it.level())
        it.level().addFreshEntity(mooshroom)
        mooshroom.setPos(it.pos)
    }, 0.075)
}


private val badGambler = WeightedCollection<GamblerAction>().also { collection ->
    collection.add(GamblerAction("You won dirt...") {
        it.giveOrDropItem(itemStack(Items.DIRT, 16) {})
    }, 0.6)
    collection.add(GamblerAction("You won poision...") {
        it.addEffect(MobEffectInstance(MobEffects.POISON, 20 * 10))
    }, 0.6)
    collection.add(GamblerAction("You won nausea...") {
        it.addEffect(MobEffectInstance(MobEffects.CONFUSION, 20 * 10))
    }, 0.6)
    collection.add(GamblerAction("You won levitation") {
        it.addEffect(MobEffectInstance(MobEffects.LEVITATION, 20 * 10))
    }, 0.6)
    collection.add(GamblerAction("You won some wheat") {
        it.giveOrDropItem(itemStack(Items.WHEAT, 10 + Random.nextInt(-10, 10)) {})
    }, 0.5)
    collection.add(GamblerAction("Hoe") {
        it.giveOrDropItem(itemStack(Items.NETHERITE_HOE, 1) {})
    }, 0.1)
    collection.add(GamblerAction("You may want to look above you...") {
        it.world.setBlockAndUpdate(it.blockPos.subtract(Vec3i(0, -15, 0)), Blocks.ANVIL.defaultBlockState())
    }, 0.1)
    collection.add(GamblerAction("Instant death") {
        it.kill()
    }, 0.005)
    collection.add(GamblerAction("Kit change") {
        it.hgPlayer.kits.clear()
        val kit = randomKit()
        it.hgPlayer.kits.add(kit)
        kit.onEnable?.invoke(it.hgPlayer, kit, it)
        it.hgPlayer.giveKitItems()
    }, 0.03)
    collection.add(GamblerAction("Coords leak") {
        broadcastComponent(
            literalText {
                text("${it.name.string}´s coordinates are: ") { color = TEXT_GRAY }
                text("${it.pos.x.toInt()} ${it.pos.y.toInt()} ${it.pos.z.toInt()}") {
                    color = TEXT_BLUE
                    bold = true
                }
            }
        )
    }, 0.15)
    collection.add(GamblerAction("Nothing") {}, 0.2)
    collection.add(GamblerAction("Time to read") {
        it.world.setBlockAndUpdate(it.blockPos, Blocks.LECTERN.defaultBlockState())
    }, 0.15)
    collection.add(GamblerAction("Inventory clear") {
        it.inventory.clearContent()
        it.hgPlayer.giveKitItems()
    }, 0.004)
    collection.add(GamblerAction("Halbes") {
        it.health = 1f
    }, 0.04)
    collection.add(GamblerAction("Wither") {
        val wither = WitherBoss(EntityType.WITHER, it.level())
        it.level().addFreshEntity(wither)
        wither.setPos(it.pos)
    }, 0.00327)
    collection.add(GamblerAction("MLG") {
        val before = it.inventory.getSelected()
        val slot = it.inventory.selected
        it.inventory.setItem(slot, Items.WATER_BUCKET.defaultInstance)
        it.teleportTo(it.x, it.y + 30, it.z)
        mcCoroutineTask(delay = 4.seconds) { _ ->
            it.inventory.setItem(slot, before)
        }
    }, 0.1)
    collection.add(GamblerAction("Random tp") {
        val random = PlayerList.alivePlayers.random()
        val serverPlayer = random.serverPlayer ?: run {
            it.sendText {
                text("You were lucky. The player you wanted to teleport to is disconnected.")
                color = TEXT_GRAY
            }
            return@GamblerAction
        }
        it.teleportTo(serverPlayer.x, serverPlayer.y, serverPlayer.z)
    }, 0.1)
    collection.add(GamblerAction("Creeper") {
        val creeper = Creeper(EntityType.CREEPER, it.level())
        it.level().addFreshEntity(creeper)
        creeper.setPos(it.pos)
    }, 0.075)
    collection.add(GamblerAction("Pumpkin head") {
        it.inventory.armor[3] = Items.PUMPKIN.defaultInstance
    }, 0.175)
    collection.add(GamblerAction("You won kelp...") {
        it.giveOrDropItem(itemStack(Items.DRIED_KELP, 16) {})
    }, 0.4)
}


private data class GamblerAction(
    val text: String,
    val action: ((ServerPlayer) -> Unit),
)