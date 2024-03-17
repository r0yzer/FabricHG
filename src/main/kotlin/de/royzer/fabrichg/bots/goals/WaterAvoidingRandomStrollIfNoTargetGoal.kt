package de.royzer.fabrichg.bots.goals

import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal

class WaterAvoidingRandomStrollIfNoTargetGoal(
    val targetMob: PathfinderMob,
    speedModifier: Double,
    probability: Float
) : WaterAvoidingRandomStrollGoal(targetMob, speedModifier, probability) {
    override fun start() {
        if (targetMob.target != null) return
        super.start()
    }
}