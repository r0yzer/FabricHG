package de.royzer.fabrichg.kit.achievements

import de.royzer.fabrichg.stats.StatsStore.Companion.statsScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import net.minecraft.world.entity.player.Player

object MemoryAchievementStore : IAchievementStore {
    val achievementsMap = hashMapOf<String, PlayerAchievementDto>()

    override fun init(): IAchievementStore {
        return this
    }

    override fun update(achievements: PlayerAchievementDto) {
        achievementsMap[achievements.id] = achievements
    }

    override fun get(
        player: Player,
        achievementId: Int
    ): Deferred<PlayerAchievementDto> {
        val id = PlayerAchievementDto.id(player.uuid, achievementId)

        return statsScope.async {
            achievementsMap.computeIfAbsent(id) { PlayerAchievementDto(id, player.uuid, achievementId, 0) }
        }
    }

    fun getInstant(
        player: Player,
        achievementId: Int
    ): PlayerAchievementDto {
        val id = PlayerAchievementDto.id(player.uuid, achievementId)

        return achievementsMap.computeIfAbsent(id) { PlayerAchievementDto(id, player.uuid, achievementId, 0) }
    }

    override fun initAchievement(player: Player, achievementId: Int) {
        val id = PlayerAchievementDto.id(player.uuid, achievementId)

        achievementsMap.computeIfAbsent(id) { PlayerAchievementDto(id, player.uuid, achievementId, 0) }
    }

}
