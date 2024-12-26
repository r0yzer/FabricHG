package de.royzer.fabrichg.kit.achievements

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import net.minecraft.world.entity.player.Player

object AchievementManager : IAchievementStore {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun init(): IAchievementStore {
        MemoryAchievementStore.init()
        DatabaseAchievementStore.init()

        val all = DatabaseAchievementStore.getAll()

        IAchievementStore.achievementScope.launch {
            all.join()
            println("completed: ${all.getCompleted()}")
            all.getCompleted().forEach { achievement ->
                println("achievement in db: $achievement")
                MemoryAchievementStore.update(achievement)
            }
        }

        return this
    }

    override fun update(achievements: PlayerAchievementDto) {
        MemoryAchievementStore.update(achievements)
    }

    override fun get(
        player: Player,
        achievementId: Int
    ): Deferred<PlayerAchievementDto> {
        return MemoryAchievementStore.get(player, achievementId)
    }

    override fun initAchievement(player: Player, achievementId: Int) {
        MemoryAchievementStore.initAchievement(player, achievementId)
    }

    fun copyMemoryToDb() {
        MemoryAchievementStore.achievementsMap.forEach { key, achievement ->
            DatabaseAchievementStore.update(achievement)
        }
    }
}