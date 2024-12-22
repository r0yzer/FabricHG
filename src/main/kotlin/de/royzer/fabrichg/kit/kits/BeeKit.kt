package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.achievements.delegate.achievement
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

    val replaceBlocksAchievement by achievement("replace blocks") {
        level(200)
        level(1000)
        level(2500)
    }
    val summonBeesAchievement by achievement("summon bees") {
        level(100)
        level(500)
        level(2000)
    }

    kitItem {
        itemStack = kitSelectorItem
        onClickAtPlayer { hgPlayer, kit, clickedPlayer, hand ->
            val player = hgPlayer.serverPlayer ?: return@onClickAtPlayer
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
            summonBeesAchievement.awardLater(player, bees)
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
                        replaceBlocksAchievement.award(player)
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