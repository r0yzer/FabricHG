package de.royzer.fabrichg.feast

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.kit.kits.urgalTime
import de.royzer.fabrichg.scoreboard.formattedTime
import de.royzer.fabrichg.sendPlayerStatus
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.WeightedCollection
import de.royzer.fabrichg.util.getRandomHighestPos
import de.royzer.fabrichg.util.tracker
import kotlinx.coroutines.Job
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.Vec3i
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setPotion
import net.silkmc.silk.core.math.geometry.produceFilledCirclePositions
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText
import java.time.Instant
import kotlin.math.ceil
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


// TODO
object Feast {
    /**
     * If the feast platform has been generated
     */
    var spawned = false
    /**
     * If the feast chests have been generated
     */
    var started = false
    var timeLeft = 300

    var feastJob: Job? = null
    var feastCenter: BlockPos = BlockPos.ZERO
    var feastTimestamp: Instant? = null

    private var radius = 25

    val feastBlockPositions = mutableListOf<BlockPos>()

    fun spawn() {
        feastStrengthPotionItem // sonst ist null oder so
        server.playerList.players.forEach { it.sendPlayerStatus() }
        spawned = true
        feastCenter = getRandomHighestPos(150)
        feastTimestamp = Instant.now().plusSeconds(timeLeft.toLong())

        server.playerList.players.forEach {
            it.playNotifySound(SoundEvents.RAID_HORN.value(), SoundSource.MASTER, 1.0f, 1.0f)
        }

        PlayerList.players.forEach { e ->
            e.value.kits.forEach {
                if (it.name == "Anchor") {
                    e.value.serverPlayer?.inventory?.armor?.set(2, Items.AIR.defaultInstance)
                    e.value.serverPlayer?.inventory?.armor?.set(3, Items.AIR.defaultInstance)
                }
            }
        }

        repeat(50) { i ->
            feastCenter.produceFilledCirclePositions(radius) {
                server.overworld().setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.AIR.defaultBlockState())
            }
        }

        feastCenter.produceFilledCirclePositions(radius) {
            server.overworld().setBlockAndUpdate(it, Blocks.GRASS_BLOCK.defaultBlockState())
            feastBlockPositions.add(it)
        }

        feastJob =
            mcCoroutineTask(howOften = timeLeft.toLong() + 1, period = 1000.milliseconds, delay = 0.milliseconds) {
                when (timeLeft) {
                    300, 180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcastComponent(literalText {
                        text("Das Feast startet in ")
                        text(timeLeft.formattedTime) { color = TEXT_BLUE }
                        text(" Minuten bei ")
                        text("${feastCenter.x} ${feastCenter.y} ${feastCenter.z}") {
                            color = TEXT_BLUE
                            bold = true
                        }
                        color = TEXT_GRAY
                    })

                    0 -> {
                        start()
                    }
                }
                timeLeft -= 1
            }
    }

    fun start() {
        val world = server.overworld()

        world.setBlock(
            feastCenter.subtract(Vec3i(0, -1, 0)),
            Blocks.ENCHANTING_TABLE.defaultBlockState(),
            3
        )
        val chestLocations: MutableList<BlockPos> = mutableListOf()

        chestLocations.add(feastCenter.subtract(Vec3i(1, -1, 1)))
        chestLocations.add(feastCenter.subtract(Vec3i(-1, -1, 1)))
        chestLocations.add(feastCenter.subtract(Vec3i(-1, -1, -1)))
        chestLocations.add(feastCenter.subtract(Vec3i(1, -1, -1)))
        chestLocations.add(feastCenter.subtract(Vec3i(2, -1, 2)))
        chestLocations.add(feastCenter.subtract(Vec3i(0, -1, 2)))
        chestLocations.add(feastCenter.subtract(Vec3i(-2, -1, 2)))
        chestLocations.add(feastCenter.subtract(Vec3i(2, -1, 0)))
        chestLocations.add(feastCenter.subtract(Vec3i(-2, -1, 0)))
        chestLocations.add(feastCenter.subtract(Vec3i(2, -1, -2)))
        chestLocations.add(feastCenter.subtract(Vec3i(0, -1, -2)))
        chestLocations.add(feastCenter.subtract(Vec3i(-2, -1, -2)))

        chestLocations.forEach {chestLocation ->
            world.setBlockAndUpdate(chestLocation, Blocks.CHEST.defaultBlockState())
            val blockEntity: BlockEntity = world.getBlockEntity(chestLocation) ?: return

            if (blockEntity is ChestBlockEntity) {
                repeat(8) {
                    val slot = Random.nextInt(27)
                    val loot = feastLoot.get() ?: return@repeat
                    if (loot.shouldBeDamaged) loot.item.breakItem()
                    val amount = Random.nextInt(1, loot.maxAmount + 1)
                    blockEntity.setItem(slot, loot.item.copy().also { it.count = amount })
                }
            }
        }
        started = true
    }
}

val feastStrengthPotion =
    Potion(
        MobEffectInstance(MobEffects.DAMAGE_BOOST, 60 * 20, 0)
    )

private val feastStrengthPotionItem = itemStack(Items.SPLASH_POTION) {
    setPotion(/*Holder.direct(feastStrengthPotion)*/Potions.STRENGTH)
    count = 1
}

fun ItemStack.breakItem() {
    val max = maxDamage.toDouble()
    val min = max / 2

    val durability = Random.nextDouble(min, max)

    damageValue = ceil(max - durability).toInt()
}

val feastLoot = WeightedCollection<FeastLoot>().also {
    it.add(FeastLoot(Items.DIAMOND_HELMET.defaultInstance, 1, true), 1.0)
    it.add(FeastLoot(Items.DIAMOND_LEGGINGS.defaultInstance, 1, true), 1.0)
    it.add(FeastLoot(Items.DIAMOND_CHESTPLATE.defaultInstance, 1, true), 1.0)
    it.add(FeastLoot(Items.DIAMOND_BOOTS.defaultInstance, 1, true), 1.0)
    it.add(FeastLoot(Items.DIAMOND_SWORD.defaultInstance, 1), 1.0)
    it.add(FeastLoot(feastStrengthPotionItem, 1), 0.3)
    it.add(FeastLoot(tracker, 1), 0.5)
    it.add(FeastLoot(Items.COBWEB.defaultInstance, 5), 2.0)
    it.add(FeastLoot(Items.BOW.defaultInstance, 1), 1.5)
    it.add(FeastLoot(Items.ENDER_PEARL.defaultInstance, 4), 1.5)
    it.add(FeastLoot(Items.ARROW.defaultInstance, 8), 3.0)
    it.add(FeastLoot(Items.COOKED_MUTTON.defaultInstance, 6), 2.0)
    it.add(FeastLoot(Items.MUSHROOM_STEW.defaultInstance, 8), 2.5)
    it.add(FeastLoot(Items.EXPERIENCE_BOTTLE.defaultInstance, 3), 2.0)
    it.add(FeastLoot(Items.LAPIS_LAZULI.defaultInstance, 12), 2.0)
}

data class FeastLoot(
    val item: ItemStack,
    val maxAmount: Int,
    val shouldBeDamaged: Boolean = false
)