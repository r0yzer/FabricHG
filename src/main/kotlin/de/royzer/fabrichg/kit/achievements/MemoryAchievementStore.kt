package de.royzer.fabrichg.kit.achievements

import net.minecraft.world.entity.player.Player

object MemoryAchievementStore : IAchievementStore {
    val achievementsMap = hashMapOf<String, PlayerAchievementDto>()

    override suspend fun init(): IAchievementStore {
        return this
    }

    override suspend fun update(achievements: PlayerAchievementDto) {
        achievementsMap[achievements.id] = achievements
    }

    override suspend fun get(
        player: Player,
        achievementId: Int
    ): PlayerAchievementDto {
        val id = PlayerAchievementDto.id(player.uuid, achievementId)
        return achievementsMap.computeIfAbsent(id) { PlayerAchievementDto(id, player.uuid, achievementId, 0) }
    }

    fun getInstant(
        player: Player,
        achievementId: Int
    ): PlayerAchievementDto {
        val id = PlayerAchievementDto.id(player.uuid, achievementId)
        return achievementsMap.computeIfAbsent(id) { PlayerAchievementDto(id, player.uuid, achievementId, 0) }
    }

    override suspend fun initAchievement(player: Player, achievementId: Int) {
        val id = PlayerAchievementDto.id(player.uuid, achievementId)
        achievementsMap.computeIfAbsent(id) { PlayerAchievementDto(id, player.uuid, achievementId, 0) }
    }
}
