package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.feast.Feast
import de.royzer.fabrichg.feast.Minifeast
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.MOTD_STRING
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.util.getRandomHighestPos
import de.royzer.fabrichg.util.lerp
import net.silkmc.silk.core.logging.logInfo
import net.silkmc.silk.core.text.literalText
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.random.Random

object IngamePhase : GamePhase() {
    var winner: HGPlayer? = null
    override val phaseType = PhaseType.INGAME
    override val maxPhaseTime = 30 * 60
    override val nextPhase by lazy { EndPhase(winner) }

    private const val feastStartTime = 600

    private const val minifeastStartTime = 0
    private const val minifeastEndTime = 100
    private var minifeasts by Delegates.notNull<Int>()
    private lateinit var minifeastStartTimes: List<Int>

    val maxPlayers by lazy { PlayerList.alivePlayers.size }

    override fun init() {
        GamePhaseManager.server.motd = "$MOTD_STRING\nCURRENT GAME PHASE: \u00A7eINGAME"
        logInfo("IngamePhase startet")
        GamePhaseManager.server.isPvpAllowed = true
        minifeasts = ((minifeastEndTime - minifeastStartTime) / 150 - 5 + lerp(
            0.5f,
            2f,
            min(2f, PlayerList.alivePlayers.size.toFloat()/20) + min(5, PlayerList.alivePlayers.size/3)
        )).toInt()
        minifeastStartTimes = List(max(1, minifeasts)) { Random.nextInt(minifeastStartTime, minifeastEndTime) }
        PlayerList.alivePlayers.forEach { hgPlayer ->
            hgPlayer.serverPlayer?.closeContainer()
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
}