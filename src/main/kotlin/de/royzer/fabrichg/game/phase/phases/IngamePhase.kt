package de.royzer.fabrichg.game.phase.phases

import de.royzer.fabrichg.TEXT_BLUE
import de.royzer.fabrichg.TEXT_GRAY
import de.royzer.fabrichg.bots.player.FakeServerPlayer
import de.royzer.fabrichg.data.hgplayer.HGPlayer
import de.royzer.fabrichg.feast.Feast
import de.royzer.fabrichg.feast.Minifeast
import de.royzer.fabrichg.game.GamePhaseManager
import de.royzer.fabrichg.game.Pit
import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.game.broadcastComponent
import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.teams.HGTeam
import de.royzer.fabrichg.game.teams.hgTeam
import de.royzer.fabrichg.game.teams.teams
import de.royzer.fabrichg.gulag.GulagManager
import de.royzer.fabrichg.kit.achievements.AchievementManager
import de.royzer.fabrichg.mongodb.mongoScope
import de.royzer.fabrichg.settings.ConfigManager
import de.royzer.fabrichg.util.getRandomHighestPos
import de.royzer.fabrichg.util.lerp
import de.royzer.fabrichg.util.recraft
import kotlinx.coroutines.launch
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Items
import net.silkmc.silk.core.logging.logInfo
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

data class HGWinnable(
    val text: Component,
    val winners: List<HGPlayer>
) {
    companion object {
        private fun singleWinnerText(winner: HGPlayer) = literalText {
            color = TEXT_GRAY
            text(winner.name) {
                color = TEXT_BLUE
                underline = true
            }
            text(" hat gewonnen!")
            hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, playerInfoText(winner))
        }

        private fun teamWinnerText(team: HGTeam) = literalText {
            color = TEXT_GRAY
            text(team.name) {
                color = TEXT_BLUE
                underline = true
            }
            text(" hat gewonnen!")
            newLine()
            team.hgPlayers.forEach {
                text {
                    text(" - ")
                    text(it.name) { color = TEXT_BLUE }
                    text(" (")
                    text(it.kills.toString()) { color = TEXT_BLUE }
                    text(" kills)")

                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, playerInfoText(it))
                }

                newLine()
            }

            hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, teamInfoText(team))
        }

        fun fromPlayer(winner: HGPlayer) = HGWinnable(singleWinnerText(winner), listOf(winner))
        fun fromTeam(team: HGTeam) = HGWinnable(teamWinnerText(team), team.hgPlayers)
    }
}

object IngamePhase : GamePhase() {
    var winner: HGWinnable? = null
    override val phaseType = PhaseType.INGAME
    override val maxPhaseTime by lazy { ConfigManager.gameSettings.maxIngameTime }
    override val nextPhase by lazy { EndPhase(winner) }

    private val feastStartTime by lazy { ConfigManager.gameSettings.feastStartTime }
    private val pitEnabled by lazy { ConfigManager.gameSettings.pitEnabled }
    val pitStartTime by lazy { ConfigManager.gameSettings.pitStartTime }
    private val maxRecraft by lazy { ConfigManager.gameSettings.maxRecraftBeforeFeast }

    private const val minifeastStartTime = 300
    private const val minifeastEndTime = 550
    private var minifeasts by Delegates.notNull<Int>()
    private lateinit var minifeastStartTimes: List<Int>

    val maxPlayers by lazy { PlayerList.alivePlayers.size }

    override fun init() {
        mongoScope.launch {
            AchievementManager.copyMemoryToDb()
        }

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
        minifeastStartTimes = if (ConfigManager.gameSettings.minifeastEnabled) List(max(1, minifeasts)) {
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
        if (PlayerList.alivePlayers.size <= GulagManager.minPlayersOutsideGulag) {
            GulagManager.close(sendMessage = false)
        }
    }

    private fun checkForWinner() {
        if (ConfigManager.gameSettings.teamsEnabled) {
            val aliveTeams = teams.filter { it.hgPlayers.any { it.isAlive } }
            val playersWithoutTeam = PlayerList.alivePlayers.filter { it.hgTeam == null }

            if (aliveTeams.size <= 1 && playersWithoutTeam.isEmpty()) {
                winner = aliveTeams.firstOrNull()?.let { HGWinnable.fromTeam(it) }
                startNextPhase()
            } else if (aliveTeams.isEmpty() && playersWithoutTeam.size == 1) {
                winner = HGWinnable.fromPlayer(playersWithoutTeam.first())
                startNextPhase()
            }
            // gibt es da noch mehr cases?
        } else {
            if (PlayerList.alivePlayers.size <= 1) {
                winner = PlayerList.alivePlayers.firstOrNull()?.let { HGWinnable.fromPlayer(it) }
                startNextPhase()
            }
        }
    }

    override fun tick(timer: Int) {
        checkForWinner()

        if (timer == feastStartTime) {
            Feast.spawn()
        }

        // nach 10 min normalerweise (config)
        if (timer == GulagManager.gulagEndTime && GulagManager.gulagEnabled) {
            GulagManager.close()
        }

        if (minifeastStartTimes.contains(timer)) {
            Minifeast(getRandomHighestPos(200)).also {
                it.start()
            }
        }

        if (!Feast.spawned) {
            PlayerList.alivePlayers.forEach {
                val serverPlayer = it.serverPlayer ?: return@forEach
                if (serverPlayer.recraft > maxRecraft) {
                    serverPlayer.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 21, 3, false, false, true))
                    serverPlayer.sendText("You are carrying too much recraft") { color = TEXT_GRAY }

                    val anvilSounds = 100
                    val soundDuration = 1.seconds

                    mcCoroutineTask(howOften = anvilSounds.toLong(), period = soundDuration/anvilSounds) {
                        SoundSource.entries.forEach { source ->
                            listOf(SoundEvents.ANVIL_PLACE, SoundEvents.ANVIL_LAND).forEach { sound ->
                                serverPlayer.playNotifySound(sound, source, 300f, 1f)
                            }
                        }
                    }
                }
            }
        }

        if (timer == pitStartTime - 60 && pitEnabled) {
            broadcastComponent(literalText {
                text("Das Pit startet in ")
                text("60") { color = TEXT_BLUE }
                text(" Sekunden")
                color = TEXT_GRAY
            })
        }

        if (timer == pitStartTime && pitEnabled) {
            Pit.start()
        }

        when (val timeLeft = maxPhaseTime - timer) {
            120, 60, 30, 15, 10, 5, 4, 3, 2, 1 -> broadcastComponent(literalText {
                text("Das Spiel endet in ")
                text(timeLeft.toString()) { color = TEXT_BLUE }
                text(" Sekunden")
                color = TEXT_GRAY
            })

            0 -> {
                winner = PlayerList.alivePlayers.shuffled().maxByOrNull { it.kills }?.let {
                    val team = it.hgTeam
                    if (ConfigManager.gameSettings.teamsEnabled && team != null) {
                        HGWinnable.fromTeam(team)
                    } else {
                        HGWinnable.fromPlayer(it)
                    }
                }
                startNextPhase()
            }
        }
    }
}
