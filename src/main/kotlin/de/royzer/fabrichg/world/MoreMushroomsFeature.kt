package de.royzer.fabrichg.world

import com.mojang.serialization.Codec
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext
import kotlin.random.Random

class MoreMushroomsFeature(configCodec: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(configCodec) {
    companion object {
        private fun getRandomMushroomState() = (if (Random.nextBoolean()) Blocks.BROWN_MUSHROOM else Blocks.RED_MUSHROOM).defaultState
    }

    override fun generate(context: FeatureContext<DefaultFeatureConfig>): Boolean {
        val world = context.world
        val origin = context.origin

        val mushroomsPositions = ArrayList<BlockPos>()

        ((origin.x -8)..(origin.x +8)).forEach { x ->
            ((origin.z -8)..(origin.z +8)).forEach { z ->
                if (Random.nextInt(10) == 3)
                    mushroomsPositions += world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, BlockPos(x, origin.y, z))
            }
        }

        mushroomsPositions.forEach {
            if (world.getBlockState(it).isAir)
                world.setBlockState(it, getRandomMushroomState(), Block.NOTIFY_LISTENERS or Block.FORCE_STATE)
        }

        return true
    }
}