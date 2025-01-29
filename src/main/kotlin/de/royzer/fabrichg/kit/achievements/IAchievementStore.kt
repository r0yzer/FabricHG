package de.royzer.fabrichg.kit.achievements

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import net.minecraft.world.entity.player.Player

interface IAchievementStore {
    companion object {
        val achievementScope = CoroutineScope(Dispatchers.IO)
    }

    suspend fun init(): IAchievementStore
    suspend fun update(achievements: PlayerAchievementDto)
    suspend fun get(player: Player, achievementId: Int): PlayerAchievementDto
    suspend fun initAchievement(player: Player, achievementId: Int)
}
