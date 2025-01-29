package de.royzer.fabrichg.kit.achievements

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import de.royzer.fabrichg.data.hgplayer.hgPlayer
import de.royzer.fabrichg.mongodb.MongoManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import net.minecraft.world.entity.player.Player

object DatabaseAchievementStore : IAchievementStore {
    private lateinit var collection: MongoCollection<PlayerAchievementDto>

    override suspend fun init(): DatabaseAchievementStore {
        collection = MongoManager.getOrCreateCollection("achievements")
        return this
    }

    override suspend fun update(achievements: PlayerAchievementDto) {
        collection.replaceOne(
            Filters.eq("_id", achievements.playerAchievementId),
            achievements,
            ReplaceOptions().upsert(true)
        )
    }

    suspend fun getAll(): List<PlayerAchievementDto> {
        return collection.find().toList()
    }

    override suspend fun get(player: Player, achievementId: Int): PlayerAchievementDto {
        val id = "${player.uuid}$achievementId"
        val achievement = collection.find(Filters.eq("_id")).firstOrNull()
            ?: PlayerAchievementDto(id, player.uuid, achievementId, 0).also { update(it) }
        return achievement
    }

    override suspend fun initAchievement(player: Player, achievementId: Int) {
        val achievement = get(player, achievementId)
        player.hgPlayer!!.achievements = listOf(achievement)
    }
}
