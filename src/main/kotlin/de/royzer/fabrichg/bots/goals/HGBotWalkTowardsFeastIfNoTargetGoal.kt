package de.royzer.fabrichg.bots.goals

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.feast.Feast
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.pathfinder.Path

class HGBotWalkTowardsFeastIfNoTargetGoal(
    val hgBot: HGBot,
    val speedModifier: Double = 1.15
): Goal() {
    var path: Path? = null
    var ticksUntilPathRecalculation = 20

    override fun canUse(): Boolean {
        return hgBot.target == null && hgBot.shouldWalkToFeast()
    }

    override fun tick() {
        val feastTrackPos = BlockPos(Feast.feastCenter.x, Feast.feastCenter.y + 2, Feast.feastCenter.z)

        if (this.path == null || ticksUntilPathRecalculation <= 0) {
            path = this.hgBot.navigation.createPath(feastTrackPos, 0)
            ticksUntilPathRecalculation = 20
        }

        this.hgBot.tracking = !hgBot.hasLineOfSight(feastTrackPos.center)
        this.hgBot.navigation.moveTo(path, this.speedModifier)

        ticksUntilPathRecalculation--
    }
}