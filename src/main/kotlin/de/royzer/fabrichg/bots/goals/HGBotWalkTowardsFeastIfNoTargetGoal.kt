package de.royzer.fabrichg.bots.goals

import de.royzer.fabrichg.bots.HGBot
import de.royzer.fabrichg.feast.Feast
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
        if (this.path == null || ticksUntilPathRecalculation <= 0) {
            path = this.hgBot.navigation.createPath(Feast.feastCenter, 0)
            ticksUntilPathRecalculation = 20
        }

        this.hgBot.navigation.moveTo(path, this.speedModifier)

        ticksUntilPathRecalculation--
    }
}