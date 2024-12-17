package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Bee
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

val beeKit = kit("Bee") {
    kitSelectorItem = Items.HONEYCOMB.defaultInstance

    description = "Fight with the help of bees"

    cooldown = 28.0

    val bees by property(4, "bees")
    val beeTime by property(5, "bee kit time")

    kitItem {
        itemStack = kitSelectorItem
        onClickAtPlayer { hgPlayer, kit, clickedPlayer, hand ->
            val world = clickedPlayer.world
            if (clickedPlayer.hgPlayer.isNeo) return@onClickAtPlayer
            repeat(bees) {
                val bee = Bee(EntityType.BEE, world)
                bee.target = clickedPlayer
                bee.setPos(clickedPlayer.eyePosition)
                world.addFreshEntity(bee)

                mcCoroutineTask(delay = beeTime.seconds) {
                    bee.kill()
                }
            }
            mcCoroutineTask(howOften = 20L * (beeTime + 1)) {
                val blocks: List<BlockPos> = listOf(
                    clickedPlayer.onPos,
                    clickedPlayer.onPos.north(),
                    clickedPlayer.onPos.west(),
                    clickedPlayer.onPos.south(),
                )

                blocks.forEach {
                    val blockBefore = world.getBlockState(it)
                    if (blockBefore.block !in notReplaceBlocks) {
                        world.setBlockAndUpdate(it, Blocks.HONEY_BLOCK.defaultBlockState())
                        mcCoroutineTask(delay = 500.milliseconds) { _ ->
                            world.setBlockAndUpdate(it, blockBefore)
                        }
                    }
                }

            }
            hgPlayer.activateCooldown(kit)
        }
    }
}

private val notReplaceBlocks: List<Block> = listOf(Blocks.AIR, Blocks.HONEY_BLOCK)