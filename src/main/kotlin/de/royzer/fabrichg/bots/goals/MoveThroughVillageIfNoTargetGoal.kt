package de.royzer.fabrichg.bots.goals

import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal
import java.util.function.BooleanSupplier

class MoveThroughVillageIfNoTargetGoal(
    val goalMob: PathfinderMob, speedModifier: Double, onlyAtNight: Boolean,
    distanceToPoi: Int, canDealWithDoors: BooleanSupplier
) : MoveThroughVillageGoal(
    goalMob, speedModifier, onlyAtNight, distanceToPoi, canDealWithDoors
) {
    override fun start() {
        if (goalMob.target != null) return;
        super.start()
    }
}
