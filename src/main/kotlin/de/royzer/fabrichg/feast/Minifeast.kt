package de.royzer.fabrichg.feast

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.server
import de.royzer.fabrichg.util.WeightedCollection
import de.royzer.fabrichg.util.forceGiveItem
import de.royzer.fabrichg.util.inventoryValue
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.core.component.DataComponents
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.item.setCustomName
import net.silkmc.silk.core.item.setLore
import net.silkmc.silk.core.item.setPotion
import net.silkmc.silk.core.math.geometry.produceFilledCirclePositions
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import kotlin.math.roundToInt
import kotlin.random.Random

class Minifeast(
    private val miniFeastPos: BlockPos
) {
    private val radius = 3

    fun start() {
        repeat(3) { i ->
            miniFeastPos.produceFilledCirclePositions(radius) {
                server.overworld().setBlockAndUpdate(BlockPos(it.x, it.y + i, it.z), Blocks.AIR.defaultBlockState())
            }
        }

        miniFeastPos.produceFilledCirclePositions(radius) {
            server.overworld().setBlockAndUpdate(it, Blocks.WHITE_STAINED_GLASS.defaultBlockState())
        }

        removeEdges()
        spawn()
        announce()
    }

    fun removeEdges() {
        val world = server.overworld()

        val edgeLocations: MutableList<BlockPos> = mutableListOf()
        edgeLocations.add(miniFeastPos.subtract(Vec3i(2, 0, 2)))
        edgeLocations.add(miniFeastPos.subtract(Vec3i(-2, 0, 2)))
        edgeLocations.add(miniFeastPos.subtract(Vec3i(-2, 0, -2)))
        edgeLocations.add(miniFeastPos.subtract(Vec3i(2, 0, -2)))

        edgeLocations.forEach { edgeLocation ->
            world.setBlockAndUpdate(edgeLocation, Blocks.AIR.defaultBlockState())
        }
    }

    fun spawn() {
        val world = server.overworld()

        server.playerList.players.forEach {
            it.connection.send(ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.MASTER, it.x, it.y, it.z, 1f, 1f, Random.nextLong()))
            it.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 1.0f, 1.0f)
        }

        world.setBlock(
            miniFeastPos.subtract(Vec3i(0, -1, 0)),
            Blocks.ENCHANTING_TABLE.defaultBlockState(),
            3
        )
        val chestLocations: MutableList<BlockPos> = mutableListOf()

        chestLocations.add(miniFeastPos.subtract(Vec3i(1, -1, 1)))
        chestLocations.add(miniFeastPos.subtract(Vec3i(-1, -1, 1)))
        chestLocations.add(miniFeastPos.subtract(Vec3i(-1, -1, -1)))
        chestLocations.add(miniFeastPos.subtract(Vec3i(1, -1, -1)))

        chestLocations.forEach { chestLocation ->
            world.setBlockAndUpdate(chestLocation, Blocks.CHEST.defaultBlockState())
            val blockEntity: BlockEntity = world.getBlockEntity(chestLocation) ?: return

            if (blockEntity is ChestBlockEntity) {
                repeat(Random.nextInt(2, 6)) {
                    val slot = Random.nextInt(27)
                    val loot = miniFeastLoot.get() ?: return@repeat
                    val amount = Random.nextInt(1, loot.maxAmount + 1)
                    blockEntity.setItem(slot, loot.item.copy().also { it.count = amount })
                }
            }
        }

    }

    fun announce() {
        PlayerList.players.forEach { (_, hgPlayer) ->
            val value = hgPlayer.serverPlayer?.inventoryValue() ?: 0.0
            val range = when {
                value < 5 -> 50
                value < 10 -> 75
                value < 25 -> 100
                value < 50 -> 150
                else -> -1
            }
            if (range == -1) hgPlayer.serverPlayer?.sendText {
                text("A mini feast appeared, but you are too rich")
                color = TEXT_BLUE
            }
            else {
                val xStart = (miniFeastPos.x - Random.nextDouble(value + range)).roundToInt()
                val xEnd = (miniFeastPos.x + Random.nextDouble(value + range)).roundToInt()

                val zStart = (miniFeastPos.z - Random.nextDouble(value + range)).roundToInt()
                val zEnd = (miniFeastPos.z + Random.nextDouble(value + range)).roundToInt()

                hgPlayer.serverPlayer?.sendText {
                    text("A mini feast appeared between x: ")
                    text("$xStart") { bold = true }
                    text(" and ")
                    text("$xEnd") { bold = true }
                    text(" and z: ")
                    text("$zStart") { bold = true }
                    text(" and ")
                    text("$zEnd") { bold = true }
                    color = TEXT_BLUE
                }

            }
        }
    }
}

fun clickGift(itemStack: ItemStack, player: Player) {
    clickKitItemGift(itemStack, player)
    clickFeastItemGift(itemStack, player)
}


private fun clickFeastItemGift(itemStack: ItemStack, player: Player) {
    if (player !is ServerPlayer) return

    val lore = itemStack.get(DataComponents.LORE).toString() // TODO
    if (itemStack.displayName.string.contains("Feast item gift") && lore.contains("Item gift")) {
        val loot = feastLoot.get() ?: return
        val amount = Random.nextInt(1, loot.maxAmount + 1)
        player.inventory.removeItem(itemStack)

        player.forceGiveItem(loot.item.copy().also { it.count = amount })
        player.sendSystemMessage(loot.item.displayName)
    }
}

private val feastItemGift = itemStack(Items.CHEST) {
    setLore(listOf(
        literalText("Item gift") {
            color = 0xE3F13F
            bold = true
            italic = true
        }
    ))

    setCustomName("Feast item gift") {
        color = 0xA2E1E3
    }
}


private fun clickKitItemGift(itemStack: ItemStack, player: Player) {
    if (player !is ServerPlayer) return

    val lore = itemStack.get(DataComponents.LORE).toString() // TODO
    if (itemStack.displayName.string.contains("Kit item gift") && lore.contains("Item gift")) {
        player.hgPlayer.kits.forEach { kit ->
            kit.kitItems.random().also { kitItem ->
                player.inventory.removeItem(itemStack)
                player.forceGiveItem(kitItem.itemStack.copy())
                player.sendSystemMessage(kitItem.itemStack.displayName)
            }
        }
    }
}

private val kitItemGift = itemStack(Items.CHEST) {
    setLore(listOf(
        literalText("Item gift") {
            color = 0xE3F13F
            bold = true
            italic = true
        }
    ))

    setCustomName("Kit item gift") {
        color = 0xA2E1E3
    }
}


private val minifeastPoisonPoion = itemStack(Items.SPLASH_POTION) {
    setPotion(Potions.POISON)
    count = 1
}

private val minifeastSlownessPoion = itemStack(Items.SPLASH_POTION) {
    setPotion(Potions.SLOWNESS)
    count = 1
}

private val minifeastSwiftnessPoion = itemStack(Items.SPLASH_POTION) {
    setPotion(Potions.SWIFTNESS)
    count = 1
}

private val miniFeastLoot = WeightedCollection<MiniFeastLoot>().also {
    it.add(MiniFeastLoot(Items.IRON_INGOT.defaultInstance, 3), 0.5)
    it.add(MiniFeastLoot(Items.GOLD_INGOT.defaultInstance, 6), 1.5)
    it.add(MiniFeastLoot(Items.MUSHROOM_STEW.defaultInstance, 5), 2.25)
    it.add(MiniFeastLoot(Items.WHEAT.defaultInstance, 10), 2.25)
    it.add(MiniFeastLoot(Items.LAPIS_LAZULI.defaultInstance, 5), 2.0)
    it.add(MiniFeastLoot(Items.EXPERIENCE_BOTTLE.defaultInstance, 4), 1.5)
    it.add(MiniFeastLoot(Items.DIAMOND.defaultInstance, 2), 0.35)
    it.add(MiniFeastLoot(Items.POISONOUS_POTATO.defaultInstance, 2), 0.5)
    it.add(MiniFeastLoot(Items.POTATO.defaultInstance, 3), 0.6)
    it.add(MiniFeastLoot(Items.BAKED_POTATO.defaultInstance, 4), 0.7)
    it.add(MiniFeastLoot(minifeastPoisonPoion.copy(), 1), 1.0)
    it.add(MiniFeastLoot(minifeastSlownessPoion.copy(), 1), 1.0)
    it.add(MiniFeastLoot(minifeastSwiftnessPoion.copy(), 1), 1.0)
    it.add(MiniFeastLoot(feastItemGift.copy(), 2), 0.25)
    // it.add(MiniFeastLoot(kitItemGift.copy(), 1), 3.0)
}

private data class MiniFeastLoot(
    val item: ItemStack,
    val maxAmount: Int,
)