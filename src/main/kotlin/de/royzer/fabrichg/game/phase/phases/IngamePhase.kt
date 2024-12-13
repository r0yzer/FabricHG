package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.bots.player.FakeServerPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.feast.Feast
import de.royzer.fabrichg.feast.Minifeast
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.kit.kits.backupKit
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.util.getRandomHighestPos
import de.royzer.fabrichg.util.lerp
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Items
import net.silkmc.silk.core.logging.logInfo
import net.silkmc.silk.core.text.literalText
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.random.Random

open class IngamePhase : GamePhase() {
    var winner: HGPlayer? = null
    override val phaseType = PhaseType.INGAME
    override val maxPhaseTime = 30 * 60
    override val nextPhase by lazy { EndPhase(winner) }

    companion object {
        private const val feastStartTime = 600

        private const val minifeastStartTime = 300
        private const val minifeastEndTime = 550

        val INSTANCE = IngamePhase()
    }

    private var minifeasts by Delegates.notNull<Int>()
    internal lateinit var minifeastStartTimes: List<Int>

    val maxPlayers by lazy { PlayerList.alivePlayers.size }

    open fun shouldSpawnMinifeasts(): Boolean {
        return ConfigManager.gameSettings.minifeastEnabled
    }

    override fun init() {
        GamePhaseManager.server.motd = "${GamePhaseManager.MOTD_STRING}\nCURRENT GAME PHASE: \u00A7eINGAME"
        logInfo("IngamePhase startet")
        broadcastComponent(
            literalText {
                text("The invincibility is over")
                color = TEXT_BLUE
            }
        )
        GamePhaseManager.server.isPvpAllowed = true
        minifeasts = ((minifeastEndTime - minifeastStartTime) / 150 - 5 + lerp(
            0.5f,
            2f,
            min(2f, PlayerList.alivePlayers.size.toFloat() / 20) + min(5, PlayerList.alivePlayers.size / 3)
        )).toInt()
        minifeastStartTimes = if(shouldSpawnMinifeasts()) List(max(1, minifeasts)) {
            Random.nextInt(
                minifeastStartTime,
                minifeastEndTime
            )
        } else listOf()
        PlayerList.alivePlayers.forEach { hgPlayer ->
            hgPlayer.serverPlayer?.closeContainer()
            if (hgPlayer.serverPlayer is FakeServerPlayer) (hgPlayer.serverPlayer as FakeServerPlayer).hgBot.setItemSlot(
                EquipmentSlot.MAINHAND,
                Items.STONE_SWORD.defaultInstance
                )

        }
    }

    override fun tick(timer: Int) {
        if (PlayerList.alivePlayers.size <= 1) {
            winner = PlayerList.alivePlayers.firstOrNull()
            startNextPhase()
        }

        if (timer == feastStartTime) {
            Feast.start()
        }

        if (minifeastStartTimes.contains(timer)) {
            Minifeast(getRandomHighestPos(200)).also {
                it.start()
            }
        }

        when (val timeLeft = maxPhaseTime - timer) {
            60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcastComponent(literalText {
                text("Das Spiel endet in ")
                text(timeLeft.toString()) { color = TEXT_BLUE }
                text(" Sekunden")
                color = TEXT_GRAY
            })

            0 -> {
                winner = PlayerList.alivePlayers.shuffled().maxByOrNull { it.kills }
                startNextPhase()
            }
        }
    }

    override fun allowsKitChanges(player: HGPlayer, index: Int): Boolean {
        val isBackup = player.canUseKit(backupKit)

        return isBackup
    }
}