package de.royzer.fabrichg.feast

import de.royzer.fabrichg.server
import net.axay.fabrik.core.math.geometry.filledCirclePositionSet
import net.minecraft.block.Blocks
import net.minecraft.util.math.Vec3i

// TODO
object Feast {
    fun start() {
        Vec3i(0, 100, 0).filledCirclePositionSet(25).forEach {
            server.overworld.setBlockState(it, Blocks.GRASS_BLOCK.defaultState)
        }
    }
}