package de.royzer.fabrichg.util

import de.royzer.fabrichg.server
import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.Heightmap
import kotlin.random.Random

fun getRandomHighestPos(radius: Int): BlockPos {
    return BlockPos(
        Random.nextInt(-radius, radius),
        0,
        Random.nextInt(-radius, radius),
    ).toHighestPos()
}

fun BlockPos.toHighestPos(): BlockPos {
    server.overworld().getChunk(this)
    return BlockPos(
        x,
        server.overworld().getHeight(Heightmap.Types.WORLD_SURFACE, x, z),
        z
    )
}