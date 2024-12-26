package de.royzer.fabrichg.util

import de.royzer.fabrichg.server
import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
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

fun BlockPos.toVec3(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())

fun BlockPos.higherBy(height: Int) = BlockPos(x, y + height, z)