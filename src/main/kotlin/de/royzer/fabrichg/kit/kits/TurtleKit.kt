package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.cooldown.activateCooldown
import de.royzer.fabrichg.kit.kit
import de.royzer.fabrichg.kit.property.property
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.silkmc.silk.core.entity.blockPos
import net.silkmc.silk.core.entity.world
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.seconds

val shellBlocks = arrayListOf<BlockPos>()

val turtleKit = kit("Turtle"){
    kitSelectorItem = Items.TURTLE_SCUTE.defaultInstance
    cooldown = 30.0
    description = "create a shield around yourself"

    val shellDisappearTime by property(5, "How long should the shell exist")

    kitItem(itemStack = kitSelectorItem){
        onClick { hgPlayer, kit ->
            val serverPlayer = hgPlayer.serverPlayer
            val pos = serverPlayer?.blockPos
            val world = serverPlayer?.world
            val blockPositions = hashMapOf<BlockPos, BlockState?>()

            hgPlayer.activateCooldown(kit)

            blockPositions[pos?.atY(pos.y+3)!!] = world?.getBlockState(pos.atY(pos.y+3)!!)
            blockPositions[pos.atY(pos.y-1)!!] = world?.getBlockState(pos.atY(pos.y-1)!!)
            repeat(3){
                Direction.entries.filter { it != Direction.UP && it != Direction.DOWN }.forEach { direction ->
                    val newPos = pos.relative(direction)?.atY(pos.y+(it))!!
                    blockPositions[newPos] = world?.getBlockState(newPos)
                }
            }
            Direction.entries.filter { it != Direction.UP && it != Direction.DOWN }.forEach { direction ->
                val newPos = pos.relative(direction, 1).relative(Direction.UP).relative(direction.clockWise)!!
                blockPositions[newPos] = world?.getBlockState(newPos)
            }
            blockPositions.forEach {
                world?.setBlockAndUpdate(it.key, Blocks.WARPED_HYPHAE.defaultBlockState())
                shellBlocks.add(it.key)
            }
            serverPlayer.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, (shellDisappearTime+2)*20, 100))
            serverPlayer.addEffect(MobEffectInstance(MobEffects.REGENERATION, shellDisappearTime*20, 100))

            mcCoroutineTask(delay = shellDisappearTime.seconds){
                blockPositions.forEach {
                    world?.setBlockAndUpdate(it.key, it.value ?: Blocks.AIR.defaultBlockState())
                    shellBlocks.remove(it.key)
                }
            }
        }
    }
}