package de.royzer.fabrichg.kit.achievements

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.server
import it.unimi.dsi.fastutil.ints.IntArrayList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.silkmc.silk.core.item.itemStack
import net.silkmc.silk.core.text.literalText
import org.dizitart.no2.repository.annotations.Id
import java.util.UUID
import kotlin.random.Random

data class KitAchievementState(
    val player: Player,
    val achievement: KitAchievement,
    val level: KitAchievementLevel?,
    val state: Int
)

abstract class KitAchievement {
    abstract val id: Int
    abstract val name: String
    abstract val levels: List<KitAchievementLevel>
    abstract val kit: Kit

    fun getLevel(state: Int): KitAchievementLevel? {
        var currentLevel: KitAchievementLevel? = null

        levels.forEachIndexed { index, level ->
            val beforeLevel = levels.getOrNull(index - 1)
            val beforeMax = beforeLevel?.required ?: 0

            if (state in beforeMax..<level.required) {
                currentLevel = level
            }
        }

        return currentLevel
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getState(player: Player): KitAchievementState {
        val deferredDto = AchievementManager.get(player, id)
        deferredDto.await()
        val dto = deferredDto.getCompleted()

        return KitAchievementState(
            player,
            this,
            getLevel(dto.status),
            dto.status
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMemoryState(player: Player): KitAchievementState {
        val dto = MemoryAchievementStore.getInstant(player, id)

        return KitAchievementState(
            player,
            this,
            getLevel(dto.status),
            dto.status
        )
    }

    fun sendLevelUpMessage(player: Player, newLevel: KitAchievementLevel?) {
        when (newLevel) {
            null -> player.sendSystemMessage(literalText {
                color = TEXT_GRAY

                text("Glückwunsch du bist jetzt fertig mit dem ")
                text(name) { color = TEXT_BLUE }
                text(" Achievement des ")
                text(kit.name) { color = TEXT_BLUE }
                text(" Kits")
            })
            else -> {
                player.sendSystemMessage(literalText {
                    color = TEXT_GRAY

                    text("Glückwunsch du bist jetzt ")
                    text("Level ${newLevel.number}") { color = TEXT_BLUE }
                    text(" beim ")
                    text(name) { color = TEXT_BLUE }
                    text(" Achievement des ")
                    text(kit.name) { color = TEXT_BLUE }
                    text(" Kits")
                })
            }
        }

        player.level().playSound(player, player.x, player.y, player.z, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS)

        val starExplosion = FireworkExplosion(
            FireworkExplosion.Shape.STAR,
            IntArrayList(listOf(0xFF0000)),
            IntArrayList(listOf(0xFF0000)),
            true,
            true
        )

        val fireworkStack = itemStack(Items.FIREWORK_ROCKET) {
            set(
                DataComponents.FIREWORKS,
                Fireworks(1, listOf(starExplosion))
            )
        }

        val fireworkRocketEntity = FireworkRocketEntity(
            server.overworld(),
            player,
            player.x,
            player.y,
            player.z,
            fireworkStack.copy()
        );
        server.overworld().addFreshEntity(fireworkRocketEntity);
    }

    fun awardLater(player: Player, amount: Int = 1) = IAchievementStore.achievementScope.async { award(player, amount) }
    suspend fun award(player: Player, amount: Int = 1) {
        val state = getState(player)

        val beforeLevel = getLevel(state.state)

        val newState = state.state + amount

        val newDto = PlayerAchievementDto(PlayerAchievementDto.id(player.uuid, id),
            player.stringUUID, id, newState)

        val newLevel = getLevel(newState)

        if (newLevel != beforeLevel) {
            sendLevelUpMessage(player, newLevel)
        }
        AchievementManager.update(newDto)
    }
}

@Serializable
data class PlayerAchievementDto(@Id val playerAchievementId: String, val playerId: String, val achievementId: Int, val status: Int) {
    val id: String get() = "$playerId$achievementId"

    companion object {
        fun id(playerId: UUID, achievementId: Int) = "$playerId$achievementId"
    }
}