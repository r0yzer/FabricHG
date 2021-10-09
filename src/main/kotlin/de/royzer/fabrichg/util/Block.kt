package de.royzer.fabrichg.util

import de.royzer.fabrichg.server
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import kotlin.random.Random

fun getRandomHighestPos(radius: Int): BlockPos {
    return BlockPos(
        Random.nextInt(-radius, radius),
        0,
        Random.nextInt(-radius, radius),
    ).toHighestPos()
}

fun BlockPos.toHighestPos() = BlockPos(
    x,
    server.overworld.getTopY(Heightmap.Type.WORLD_SURFACE, x, z),
    z
)