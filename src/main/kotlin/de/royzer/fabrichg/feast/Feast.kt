package de.royzer.fabrichg.feast

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.phases.tracker
import de.royzer.fabrichg.scoreboard.formattedTime
import de.royzer.fabrichg.sendPlayerStatus
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.WeightedCollection
import de.royzer.fabrichg.util.getRandomHighestPos
import kotlinx.coroutines.Job
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
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
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


// TODO
object Feast {
    var started = false
    var timeLeft = 300

    var feastJob: Job? = null
    var feastCenter: BlockPos = BlockPos.ZERO
    var feastTimestamp: Instant? = null

    private var radius = 25

    fun start() {
        feastStrengthPotion // sonst ist null oder so
        server.playerList.players.forEach { it.sendPlayerStatus() }
        started = true
        feastCenter = getRandomHighestPos(150)
        feastTimestamp = Instant.now().plusSeconds(timeLeft.toLong())

        PlayerList.players.forEach { e ->
            e.value.kits.forEach {
                if (it.name == "Anchor") {
                    e.value.serverPlayer?.inventory?.armor?.set(2, Items.AIR.defaultInstance)
                    e.value.serverPlayer?.inventory?.armor?.set(3, Items.AIR.defaultInstance)

                }
            }
        }

        repeat(10) { i ->
            feastCenter.produceFilledCirclePositions(radius) {
                server.overworld().setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.AIR.defaultBlockState())
            }
        }

        feastCenter.produceFilledCirclePositions(radius) {
            server.overworld().setBlockAndUpdate(it, Blocks.GRASS_BLOCK.defaultBlockState())
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
                        spawn()
                    }
                }
                timeLeft -= 1
            }
    }

    fun spawn() {
        val world = server.overworld()

        server.playerList.players.forEach {
            it.connection.send(ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.MASTER, it.x, it.y, it.z, 1f, 1f, Random.nextLong()))
            it.playSound(SoundEvents.RAID_HORN.value(), 1.0f, 1.0f)
        }

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
                    val amount = Random.nextInt(1, loot.maxAmount + 1)
                    blockEntity.setItem(slot, loot.item.copy().also { it.count = amount })
                }
            }
        }

    }
}

private val feastStrengthPotion = itemStack(Items.SPLASH_POTION) {
    setPotion(Potions.STRENGTH)
    count = 1
}

private val feastLoot = WeightedCollection<FeastLoot>().also {
    it.add(FeastLoot(Items.DIAMOND_HELMET.defaultInstance, 1), 1.0)
    it.add(FeastLoot(Items.DIAMOND_LEGGINGS.defaultInstance, 1), 1.0)
    it.add(FeastLoot(Items.DIAMOND_CHESTPLATE.defaultInstance, 1), 1.0)
    it.add(FeastLoot(Items.DIAMOND_BOOTS.defaultInstance, 1), 1.0)
    it.add(FeastLoot(Items.DIAMOND_SWORD.defaultInstance, 1), 1.0)
    it.add(FeastLoot(feastStrengthPotion, 1), 0.3)
    it.add(FeastLoot(tracker, 1), 0.5)
    it.add(FeastLoot(Items.COBWEB.defaultInstance, 5), 2.0)
    it.add(FeastLoot(Items.BOW.defaultInstance, 1), 1.5)
    it.add(FeastLoot(Items.ENDER_PEARL.defaultInstance, 4), 1.5)
    it.add(FeastLoot(Items.ARROW.defaultInstance, 8), 3.0)
    it.add(FeastLoot(Items.COOKED_MUTTON.defaultInstance, 6), 2.0)
    it.add(FeastLoot(Items.MUSHROOM_STEW.defaultInstance, 8), 2.5)
    it.add(FeastLoot(Items.EXPERIENCE_BOTTLE.defaultInstance, 3), 1.5)
}




private data class FeastLoot(
    val item: ItemStack,
    val maxAmount: Int,
)