package de.royzer.fabrichg.kit.achievements

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import net.minecraft.world.entity.player.Player

interface IAchievementStore {
    companion object {
        val achievementScope = CoroutineScope(Dispatchers.IO)
    }

    fun init(): IAchievementStore
    fun update(achievements: PlayerAchievementDto)
    fun get(player: Player, achievementId: Int): Deferred<PlayerAchievementDto>
    fun initAchievement(player: Player, achievementId: Int)
}
