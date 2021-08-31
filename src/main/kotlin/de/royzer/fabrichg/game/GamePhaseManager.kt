package de.royzer.fabrichg.game

import de.royzer.fabrichg.game.phase.GamePhase
import de.royzer.fabrichg.game.phase.PhaseType
import de.royzer.fabrichg.game.phase.phases.LobbyPhase
import de.royzer.fabrichg.scoreboard.showScoreboard
import kotlinx.coroutines.*
import net.axay.fabrik.core.logging.logInfo
import net.axay.fabrik.core.task.coroutineTask
import net.axay.fabrik.core.text.literalText
import net.minecraft.network.MessageType
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import net.minecraft.text.LiteralText
import net.minecraft.util.Util
import net.minecraft.world.GameRules
import java.util.concurrent.atomic.AtomicInteger

object GamePhaseManager {
    lateinit var server: MinecraftDedicatedServer
    val timer = AtomicInteger()
    var currentPhase: GamePhase = LobbyPhase
    val currentPhaseType: PhaseType get() = currentPhase.phaseType

    fun enable(minecraftDedicatedServer: MinecraftDedicatedServer) {
        server = minecraftDedicatedServer
        server.gameRules[GameRules.SHOW_DEATH_MESSAGES].set(false, server)
        currentPhase.init()
        coroutineTask(period = 1000, howOften = Long.MAX_VALUE) {
            currentPhase.tick(timer.getAndIncrement())
        }
    }
    fun resetTimer() = timer.set(0)
}

fun broadcast(text: LiteralText) {
    GamePhaseManager.server.playerManager.broadcastChatMessage(text, MessageType.SYSTEM, Util.NIL_UUID)
}

fun broadcast(text: String) {
    broadcast(literalText(text))
}