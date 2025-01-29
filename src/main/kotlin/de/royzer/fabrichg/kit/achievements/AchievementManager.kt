package de.royzer.fabrichg.kit.achievements

import de.royzer.fabrichg.mongodb.MongoManager
import net.minecraft.world.entity.player.Player

object AchievementManager : IAchievementStore {
    override suspend fun init(): IAchievementStore {
        MemoryAchievementStore.init()

        if (MongoManager.isConnected) {
            runCatching {
                DatabaseAchievementStore.init()
                DatabaseAchievementStore.getAll().forEach { achievement ->
                    MemoryAchievementStore.update(achievement)
                }
            }
        }
        return this
    }

    override suspend fun update(achievements: PlayerAchievementDto) {
        MemoryAchievementStore.update(achievements)
    }

    override suspend fun get(
        player: Player,
        achievementId: Int,
    ): PlayerAchievementDto {
        return MemoryAchievementStore.get(player, achievementId)
    }

    override suspend fun initAchievement(player: Player, achievementId: Int) {
        MemoryAchievementStore.initAchievement(player, achievementId)
    }

    suspend fun copyMemoryToDb() {
        if (!MongoManager.isConnected) return
        MemoryAchievementStore.achievementsMap.forEach { (_, achievement) ->
            DatabaseAchievementStore.update(achievement)
        }
    }
}
