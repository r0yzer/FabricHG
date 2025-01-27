package de.royzer.fabrichg.kit.achievements

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.kit.achievements.IAchievementStore.Companion.achievementScope
import de.royzer.fabrichg.mongodb.MongoManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import net.minecraft.world.entity.player.Player

object DatabaseAchievementStore : IAchievementStore {
    private lateinit var collection: MongoCollection<PlayerAchievementDto>

    override fun init(): DatabaseAchievementStore {
        collection = MongoManager.getOrCreateCollection("achievements")
        return this
    }

    override fun update(achievements: PlayerAchievementDto) {
        achievementScope.launch {
            collection.replaceOne(
                Filters.eq("_id", achievements.playerAchievementId),
                achievements,
                ReplaceOptions().upsert(true)
            )
        }
    }

    fun getAll(): Deferred<List<PlayerAchievementDto>> {
        return achievementScope.async {
            collection.find().toList()
        }
    }

    override fun get(player: Player, achievementId: Int): Deferred<PlayerAchievementDto> {
        val id = "${player.uuid}$achievementId"

        return achievementScope.async {
            val achievement = collection.find(Filters.eq("_id")).firstOrNull()
                ?: PlayerAchievementDto(id, player.uuid, achievementId, 0).also { update(it) }
            achievement
        }
    }

    override fun initAchievement(player: Player, achievementId: Int) {
        achievementScope.launch {
            val achievement = get(player, achievementId).await()
            player.hgPlayer!!.achievements = listOf(achievement)
        }
    }
}
