package de.royzer.fabrichg.bots.goals

import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal

class RandomLookAroundIfNoTargetGoal(val goalMob: Mob) : RandomLookAroundGoal(goalMob) {
    override fun start() {
        if (goalMob.target != null) return
        super.start()
    }
}