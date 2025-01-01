package de.royzer.fabrichg.kit.achievements

import de.royzer.fabrichg.data.hgplayer.hgPlayer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.minecraft.world.entity.player.Player
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.kno2.serialization.KotlinXSerializationMapper
import org.dizitart.no2.Nitrite
import org.dizitart.no2.common.module.NitriteModule
import org.dizitart.no2.mvstore.MVStoreModule
import org.dizitart.no2.repository.Cursor
import org.dizitart.no2.repository.ObjectRepository

object DatabaseAchievementStore : IAchievementStore {
    private lateinit var storeModule: MVStoreModule

    private lateinit var db: Nitrite
    private lateinit var repository: ObjectRepository<PlayerAchievementDto>

    override fun init(): DatabaseAchievementStore {
        storeModule = MVStoreModule.withConfig()
            .filePath("achievements.db")
            .build()
        db = nitrite {
            loadModule(storeModule)
            loadModule(NitriteModule.module(KotlinXSerializationMapper()))
        }

        repository = db.getRepository<PlayerAchievementDto>()
        return this
    }

    override fun update(stats: PlayerAchievementDto) {
        IAchievementStore.achievementScope.launch {
            if (repository.getById(stats.playerAchievementId) == null) {
                repository.insert(stats)
            } else {
                repository.update(stats)
            }
        }
    }

    fun getAll(): Deferred<Cursor<PlayerAchievementDto>> {
        return IAchievementStore.achievementScope.async {
            repository.find()
        }
    }

    override fun get(player: Player, achievementId: Int): Deferred<PlayerAchievementDto> {
        val id = "${player.uuid}$achievementId"

        return IAchievementStore.achievementScope.async {
            val achievement = repository.getById(id)
            if (achievement == null) {
                val newStats = PlayerAchievementDto(id, player.uuid.toString(), achievementId, 0)
                update(newStats)
                newStats
            } else achievement
        }
    }

    override fun initAchievement(player: Player, achievementId: Int) {
        val id = "${player.uuid}$achievementId"

        val stats = PlayerAchievementDto(id, player.uuid.toString(), achievementId, 0)
        IAchievementStore.achievementScope.launch {
            val result = repository.getById(player.uuid.toString())
            if (result == null) {
                update(stats)
                return@launch
            } else player.hgPlayer!!.achievements = listOf(result)
        }
    }

}
