package de.royzer.fabrichg.bots.goals

import de.royzer.fabrichg.bots.HGBot
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal

class HGBotAttackGoal(val hgBot: HGBot, speedModifier: Double, followingTargetEvenIfNotSeen: Boolean) :
    ZombieAttackGoal(
        hgBot, speedModifier,
        followingTargetEvenIfNotSeen
    ) {
    override fun adjustedTickDelay(adjustment: Int): Int {
        return (if (this.requiresUpdateEveryTick()) adjustment else reducedTickDelay(adjustment)) / 2
    }

    override fun tick() {
        if (hgBot.target != null) hgBot.tracking = !hgBot.hasLineOfSight(hgBot.target!!)
        else hgBot.tracking = false
        super.tick()
    }
}
